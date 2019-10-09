package dev.namhyun.java.analyzer

import com.sun.jdi.*
import com.sun.jdi.connect.Connector
import com.sun.jdi.connect.IllegalConnectorArgumentsException
import com.sun.jdi.connect.LaunchingConnector
import com.sun.jdi.connect.VMStartException
import com.sun.jdi.event.*
import com.sun.jdi.request.EventRequest
import dev.namhyun.java.analyzer.model.*
import mu.KotlinLogging
import java.io.*
import kotlin.collections.Map.Entry

class Analyzer(
    private val className: String,
    private val inputFile: File? = null,
    verbose: Boolean
) {
    private val logger = if (verbose) KotlinLogging.logger {} else null

    private val excludeClasses = listOf(
        "java.*", "javax.*", "sun.*", "com.sun.*"
    )

    private val excludeMethods = listOf(
        "<init>", "<clinit>", "equals", "toString", "wait", "finalize"
    )

    private val vm: VirtualMachine

    private var connected = true
    private var vmDied = false

    var analyzeFrames = mutableListOf<Frame>()

    var lineReferencesMap = mutableMapOf<Int, Int>()

    init {
        vm = launchTarget(className)
        vm.setDebugTraceMode(VirtualMachine.TRACE_NONE)
        requestEvents()
    }

    fun analyze(): String {
        writeVMInput(inputFile)
        val eventQueue = vm.eventQueue()
        while (connected) {
            try {
                val eventSet = eventQueue.remove()
                val eventIterator = eventSet.eventIterator()
                for (event in eventIterator) {
                    handleEvent(event)
                }
                eventSet.resume()
            } catch (e: VMDisconnectedException) {
                handleDisconnectedException()
                break
            } catch (e: InterruptedException) {
                // Ignore
            }
        }
        return readVMOutput()
    }

    private fun writeVMInput(file: File?) {
        if (file != null) {
            val fileReader = FileReader(file)
            val processInputWriter = OutputStreamWriter(vm.process().outputStream)
            var readed = 0
            while (readed != -1) {
                readed = fileReader.read()
                processInputWriter.write(readed)
                processInputWriter.flush()
            }
            fileReader.close()
        }
    }

    private fun readVMOutput(): String {
        var outputStr = ""
        val processOutputReader = InputStreamReader(vm.process().inputStream)
        processOutputReader.forEachLine { outputStr += "$it\n" }
        return outputStr
    }

    private fun findLaunchingConnector(): LaunchingConnector? {
        val connectors = Bootstrap.virtualMachineManager().allConnectors()
        val findConnector = connectors.find {
            it.name() == "com.sun.jdi.CommandLineLaunch"
        }
        return findConnector as LaunchingConnector
    }

    private fun connectorArguments(connector: LaunchingConnector, args: String): Map<String, Connector.Argument> {
        val arguments = connector.defaultArguments()
        arguments["main"]?.setValue(args)
        return arguments
    }

    private fun launchTarget(args: String): VirtualMachine {
        val connector = findLaunchingConnector() ?: throw Error("No launching connector")
        val arguments = connectorArguments(connector, args)
        try {
            return connector.launch(arguments)
        } catch (e: IOException) {
            throw Error("Unable to launch target VM: $e")
        } catch (e: IllegalConnectorArgumentsException) {
            throw Error("Internal error: $e")
        } catch (e: VMStartException) {
            throw Error("Target VM failed to initialize: ${e.message}")
        }
    }

    private fun requestEvents() {
        val eventManager = vm.eventRequestManager()

        val exceptionRequest = eventManager.createExceptionRequest(null, true, true)
        exceptionRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL)
        exceptionRequest.enable()

        val methodEntryEvent = eventManager.createMethodEntryRequest()
        methodEntryEvent.addClassFilter(className)
        methodEntryEvent.setSuspendPolicy(EventRequest.SUSPEND_NONE)
        methodEntryEvent.enable()

        val methodExitEvent = eventManager.createMethodExitRequest()
        methodExitEvent.addClassFilter(className)
        methodExitEvent.setSuspendPolicy(EventRequest.SUSPEND_NONE)
        methodExitEvent.enable()

        val classPrepareRequest = eventManager.createClassPrepareRequest()
        excludeClasses.forEach {
            classPrepareRequest.addClassExclusionFilter(it)
        }
        classPrepareRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL)
        classPrepareRequest.enable()
    }

    private fun handleEvent(event: Event) = when (event) {
        is VMStartEvent -> vmStartEvent(event)
        is ClassPrepareEvent -> classPrepareEvent(event)
        is MethodEntryEvent -> methodEntryEvent(event)
        is MethodExitEvent -> methodExitEvent(event)
        is StepEvent -> stepEvent(event)
        is BreakpointEvent -> breakpointEvent(event)
        is ExceptionEvent -> exceptionEvent(event)
        is VMDeathEvent -> vmDeathEvent(event)
        is VMDisconnectEvent -> vmDisconnectEvent(event)
        else -> {
            logger?.info { "Unhandled event: $event" }
        }
    }

    private fun handleDisconnectedException() {
        val eventQueue = vm.eventQueue()
        while (connected) {
            try {
                val eventSet = eventQueue.remove()
                val eventIterator = eventSet.eventIterator()
                for (event in eventIterator) {
                    if (event is VMDeathEvent) {
                        vmDeathEvent(event)
                    } else if (event is VMDisconnectEvent) {
                        vmDisconnectEvent(event)
                    }
                }
                eventSet.resume()
            } catch (e: InterruptedException) {
                // Ignore
            } catch (e: VMDisconnectedException) {
                break
            }
        }
    }

    private fun vmStartEvent(event: VMStartEvent) {
        logger?.info { "VM is started" }
    }

    private fun classPrepareEvent(event: ClassPrepareEvent) {
        val referenceType = event.referenceType()
        if (referenceType.name() == className) {
            logger?.info { "${event.referenceType().name()} class is prepared" }
            val methods = referenceType.visibleMethods()
            methods.filterNot {
                excludeMethods.contains(it.name())
            }.forEach {
                if (!it.isNative) {
                    val breakpointRequest = vm.eventRequestManager().createBreakpointRequest(it.location())
                    if (!breakpointRequest.isEnabled) {
                        logger?.info { "Request breakpoint to ${it.name()}:${it.location().lineNumber()}" }
                        breakpointRequest.enable()
                    }
                }
            }
        }
    }

    private fun methodEntryEvent(event: MethodEntryEvent) {
        val currentMethod = event.method()
        val eventManager = vm.eventRequestManager()

        logger?.info { "Enter method ${currentMethod.name()}" }

        if (!excludeMethods.contains(currentMethod.name())) {
            analyzeFrames.add(
                MethodEntryFrame(
                    methodName = currentMethod.name(),
                    line = event.location().lineNumber()
                )
            )
        }

        val lineLocations = currentMethod.allLineLocations()
        lineLocations.forEach {
            val breakpointRequest = eventManager.createBreakpointRequest(it)
            if (!breakpointRequest.isEnabled) {
                logger?.info { "Request breakpoint to ${currentMethod.name()}:${it.lineNumber()}" }
                breakpointRequest.enable()
            }
        }
    }

    private fun methodExitEvent(event: MethodExitEvent) {
        logger?.info { "Exit method ${event.method().name()}" }

        if (!excludeMethods.contains(event.method().name())) {
            analyzeFrames.add(
                MethodExitFrame(
                    methodName = event.method().name(),
                    line = event.location().lineNumber()
                )
            )
        }
    }

    private fun stepEvent(event: StepEvent) {
        // No-op
    }

    private fun breakpointEvent(event: BreakpointEvent) {
        val threadRef = event.thread()
        if (threadRef.frameCount() > 0) {
            logger?.info { "Breakpoint event ${event.location().method().name()}:${event.location().lineNumber()}" }

            val location = event.location()
            val stackFrame = threadRef.frames().first()
            val localVariables = stackFrame.getValues(stackFrame.visibleVariables())
            val variables = localVariables.toSortedMap().map { transformVariable(it, threadRef) }.toList()

            updateLineReference(location.lineNumber())

            analyzeFrames.add(
                StepFrame(
                    methodName = location.method().name(),
                    line = location.lineNumber(),
                    variables = variables
                )
            )
        }
    }

    private fun updateLineReference(lineNumber: Int) {
        if (lineReferencesMap.containsKey(lineNumber)) {
            lineReferencesMap.put(lineNumber, lineReferencesMap[lineNumber]!! + 1)
        } else {
            lineReferencesMap.put(lineNumber, 1)
        }
    }

    private fun transformVariable(entry: Entry<LocalVariable, Value>, threadRef: ThreadReference): Variable {
        val variable = entry.key
        return when (val value = entry.value) {
            is ArrayReference -> {
                val values = value.values.map { transformValue(it, threadRef) }
                ArrayVariable(
                    name = variable.name(),
                    value = values,
                    size = values.size,
                    uniqueId = value.uniqueID()
                )
            }
            is StringReference -> {
                StringVariable(
                    name = variable.name(),
                    value = value.value(),
                    uniqueId = value.uniqueID()
                )
            }
            is ObjectReference -> transformObjectReference(value, threadRef, variable)
            else -> PrimitiveVariable(
                type = variable.typeName(),
                name = variable.name(),
                value = transformPrimitiveValue(value)
            )
        }
    }

    private fun transformValue(
        value: Value,
        threadRef: ThreadReference
    ): Any? = when (value) {
        is ArrayReference -> value.values.map { transformValue(it, threadRef) }
        is StringReference -> value.value()
        is ObjectReference -> transformObjectReference(value, threadRef)
        else -> transformPrimitiveValue(value)
    }

    private fun transformObjectReference(
        value: ObjectReference,
        threadRef: ThreadReference,
        variable: LocalVariable? = null
    ): Variable {
        val referenceType = value.referenceType()
        return when (referenceType.name()) {
            "java.lang.Boolean" -> {
                val booleanValue = value.invokeMethod(
                    threadRef,
                    referenceType.methodsByName("booleanValue").first(),
                    emptyList(),
                    0
                )
                PrimitiveVariable(
                    type = variable?.typeName() ?: value.type().name(),
                    name = variable?.name() ?: "",
                    value = transformPrimitiveValue(booleanValue)
                )
            }
            "java.lang.Byte",
            "java.lang.Double",
            "java.lang.Float",
            "java.lang.Integer",
            "java.lang.Long",
            "java.lang.Short" -> {
                val intValue = value.invokeMethod(
                    threadRef,
                    referenceType.methodsByName("intValue").first(),
                    emptyList(),
                    0
                )
                PrimitiveVariable(
                    type = variable?.typeName() ?: value.type().name(),
                    name = variable?.name() ?: "",
                    value = transformPrimitiveValue(intValue)
                )
            }
            "java.util.ArrayList" -> {
                val sizeValue = value.invokeMethod(
                    threadRef,
                    referenceType.methodsByName("size").first(),
                    emptyList(), 0
                )
                val size = if (sizeValue is IntegerValue) sizeValue.value() else 0
                val valuesValue = value.invokeMethod(
                    threadRef,
                    referenceType.methodsByName("toArray").first(),
                    emptyList(), 0
                )
                val values = if (valuesValue is ArrayReference)
                    valuesValue.values.map { transformValue(it, threadRef) }
                else
                    emptyList()
                ArrayListVariable(
                    name = variable?.name() ?: "",
                    value = values,
                    size = size,
                    uniqueId = value.uniqueID()
                )
            }
            else -> ObjectVariable(
                type = variable?.typeName() ?: value.type().name(),
                name = variable?.name() ?: "",
                value = null,
                uniqueId = value.uniqueID()
            )
        }
    }

    private fun transformPrimitiveValue(value: Value): Any? = when (value) {
        is BooleanValue -> value.value()
        is ByteValue -> value.value()
        is CharValue -> value.value()
        is DoubleValue -> value.value()
        is FloatValue -> value.value()
        is IntegerValue -> value.value()
        is LongValue -> value.value()
        is ShortValue -> value.value()
        else -> null
    }

    private fun exceptionEvent(event: ExceptionEvent) {
        val location = event.location()
        analyzeFrames.add(
            ExceptionFrame(
                methodName = location.method().name(),
                line = location.lineNumber(),
                exceptionName = event.exception().type().name()
            )
        )
    }

    private fun vmDeathEvent(event: VMDeathEvent) {
        vmDied = true
    }

    private fun vmDisconnectEvent(event: VMDisconnectEvent) {
        connected = false
    }
}