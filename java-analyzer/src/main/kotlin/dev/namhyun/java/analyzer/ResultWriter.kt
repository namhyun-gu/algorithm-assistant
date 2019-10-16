package dev.namhyun.java.analyzer

import dev.namhyun.java.analyzer.model.ExceptionFrame
import dev.namhyun.java.analyzer.model.Frame
import dev.namhyun.java.analyzer.model.StepFrame
import java.io.PrintWriter

abstract class ResultWriter(protected val writer: PrintWriter) {

    abstract fun write(output: String, frames: List<Frame>, referencesCount: Map<Int, Int>)
}

class DefaultWriter(writer: PrintWriter) : ResultWriter(writer) {
    override fun write(
        output: String,
        frames: List<Frame>,
        referencesCount: Map<Int, Int>
    ) {
        writer.println("=== Output ===\n")
        writer.println(output)

        writer.println("=== Analyze frames (size: ${frames.size}) ===\n")
        frames.forEach {
            writer.println("${it.methodName}:${it.line}")
            if (it is StepFrame) {
                it.variables.forEach {
                    writer.println("\t$it")
                }
            } else if (it is ExceptionFrame) {
                writer.println("\t${it.exceptionName}")
            } else {
                writer.println("\t$it")
            }
            writer.println()
        }

        writer.println("\n=== References per line ===\n")
        referencesCount.forEach { (line, referenceCount) ->
            writer.println("\t$line: $referenceCount")
        }
        writer.flush()
    }
}

class JsonWriter(writer: PrintWriter) : ResultWriter(writer) {
    override fun write(
        output: String,
        frames: List<Frame>,
        referencesCount: Map<Int, Int>
    ) {
        val jsonMap = mapOf(
            "output" to output,
            "frames" to frames,
            "referencesCount" to referencesCount
        )

        val json = GsonAdapterBuilder().build().toJson(jsonMap)
        writer.write(json)
        writer.flush()
    }
}