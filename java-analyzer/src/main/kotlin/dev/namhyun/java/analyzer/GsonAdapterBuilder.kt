package dev.namhyun.java.analyzer

import com.github.salomonbrys.kotson.registerTypeAdapter
import com.github.salomonbrys.kotson.typeAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.namhyun.java.analyzer.model.*

class GsonAdapterBuilder {
    private lateinit var gson: Gson

    private val primitiveVariableTypeAdapter = typeAdapter<PrimitiveVariable> {
        read { PrimitiveVariable("", "", 0) }
        write {
            beginObject()
            name("type")
            value(it.type)
            name("name")
            value(it.name)
            name("value")
            value(it.value?.toString())
            endObject()
        }
    }

    private val objectVariableTypeAdapter = typeAdapter<ObjectVariable> {
        read { ObjectVariable("", "", null, 0) }
        write {
            beginObject()
            name("type")
            value(it.type)
            name("name")
            value(it.name)
            name("uniqueId")
            value(it.uniqueId)
            endObject()
        }
    }

    private val arrayVariableTypeAdapter = typeAdapter<ArrayVariable> {
        read { ArrayVariable("", "", emptyList(), 0, 0) }
        write {
            beginObject()
            name("type")
            value(it.type)
            name("name")
            value(it.name)
            name("value")
            beginArray()
            it.value.forEach { value ->
                when (value) {
                    is ArrayVariable -> {
                        val adapter = gson.getAdapter(ArrayVariable::class.java)
                        adapter.write(this, value)
                    }
                    is ArrayListVariable -> {
                        val adapter = gson.getAdapter(ArrayListVariable::class.java)
                        adapter.write(this, value)
                    }
                    is ObjectVariable -> {
                        val adapter = gson.getAdapter(ObjectVariable::class.java)
                        adapter.write(this, value)
                    }
                    is StringVariable -> {
                        val adapter = gson.getAdapter(StringVariable::class.java)
                        adapter.write(this, value)
                    }
                    is PrimitiveVariable -> {
                        val adapter = gson.getAdapter(PrimitiveVariable::class.java)
                        adapter.write(this, value)
                    }
                }
            }
            endArray()
            name("uniqueId")
            value(it.uniqueId)
            name("size")
            value(it.size)
            endObject()
        }
    }

    private val arrayListVariableTypeAdapter = typeAdapter<ArrayListVariable> {
        read { ArrayListVariable("", "", emptyList(), 0, 0) }
        write {
            beginObject()
            name("type")
            value(it.type)
            name("name")
            value(it.name)
            name("value")
            beginArray()
            it.value.forEach { value ->
                when (value) {
                    is ArrayVariable -> {
                        val adapter = gson.getAdapter(ArrayVariable::class.java)
                        adapter.write(this, value)
                    }
                    is ArrayListVariable -> {
                        val adapter = gson.getAdapter(ArrayListVariable::class.java)
                        adapter.write(this, value)
                    }
                    is ObjectVariable -> {
                        val adapter = gson.getAdapter(ObjectVariable::class.java)
                        adapter.write(this, value)
                    }
                    is StringVariable -> {
                        val adapter = gson.getAdapter(StringVariable::class.java)
                        adapter.write(this, value)
                    }
                    is PrimitiveVariable -> {
                        val adapter = gson.getAdapter(PrimitiveVariable::class.java)
                        adapter.write(this, value)
                    }
                }
            }
            endArray()
            name("uniqueId")
            value(it.uniqueId)
            name("size")
            value(it.size)
            endObject()
        }
    }

    private val stepFrameTypeAdapter = typeAdapter<StepFrame> {
        read { StepFrame("", 0, listOf()) }
        write {
            beginObject()
            name("action")
            value("step")
            name("methodName")
            value(it.methodName)
            name("line")
            value(it.line)
            name("variables")
            beginArray()
            it.variables.forEach { variable ->
                when (variable) {
                    is ArrayVariable -> {
                        val adapter = gson.getAdapter(ArrayVariable::class.java)
                        adapter.write(this, variable)
                    }
                    is ArrayListVariable -> {
                        val adapter = gson.getAdapter(ArrayListVariable::class.java)
                        adapter.write(this, variable)
                    }
                    is ObjectVariable -> {
                        val adapter = gson.getAdapter(ObjectVariable::class.java)
                        adapter.write(this, variable)
                    }
                    is StringVariable -> {
                        val adapter = gson.getAdapter(StringVariable::class.java)
                        adapter.write(this, variable)
                    }
                    is PrimitiveVariable -> {
                        val adapter = gson.getAdapter(PrimitiveVariable::class.java)
                        adapter.write(this, variable)
                    }
                }
            }
            endArray()
            endObject()
        }
    }

    private val exceptionFrameTypeAdapter = typeAdapter<ExceptionFrame> {
        read { ExceptionFrame("", 0, "") }
        write {
            beginObject()
            name("action")
            value("exception")
            name("methodName")
            value(it.methodName)
            name("line")
            value(it.line)
            name("exceptionName")
            value(it.exceptionName)
            endObject()
        }
    }

    private val methodEntryFrameTypeAdapter = typeAdapter<MethodEntryFrame> {
        read { MethodEntryFrame("", 0) }
        write {
            beginObject()
            name("action")
            value("entry")
            name("methodName")
            value(it.methodName)
            name("line")
            value(it.line)
            endObject()
        }
    }

    private val methodExitFrameTypeAdapter = typeAdapter<MethodExitFrame> {
        read { MethodExitFrame("", 0) }
        write {
            beginObject()
            name("action")
            value("exit")
            name("methodName")
            value(it.methodName)
            name("line")
            value(it.line)
            endObject()
        }
    }

    fun build(): Gson {
        gson = GsonBuilder()
            .registerTypeAdapter<PrimitiveVariable>(primitiveVariableTypeAdapter)
            .registerTypeAdapter<ObjectVariable>(objectVariableTypeAdapter)
            .registerTypeAdapter<ArrayVariable>(arrayVariableTypeAdapter)
            .registerTypeAdapter<ArrayListVariable>(arrayListVariableTypeAdapter)
            .registerTypeAdapter<StepFrame>(stepFrameTypeAdapter)
            .registerTypeAdapter<ExceptionFrame>(exceptionFrameTypeAdapter)
            .registerTypeAdapter<MethodEntryFrame>(methodEntryFrameTypeAdapter)
            .registerTypeAdapter<MethodExitFrame>(methodExitFrameTypeAdapter)
            .create()
        return gson
    }
}