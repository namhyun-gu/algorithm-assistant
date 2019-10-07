package dev.namhyun.algorithm.assist.java.analyzer.model

open class Frame(
    open val methodName: String,
    open val line: Int
) {
    override fun toString(): String {
        return "Frame(methodName='$methodName', line=$line)"
    }
}

class StepFrame(
    override val methodName: String,
    override val line: Int,
    val variables: List<Variable>
) : Frame(methodName, line) {
    override fun toString(): String {
        return "StepFrame(methodName='$methodName', line=$line, variables=$variables)"
    }
}

class ExceptionFrame(
    override val methodName: String,
    override val line: Int,
    val exceptionName: String
) : Frame(methodName, line) {
    override fun toString(): String {
        return "ExceptionFrame(methodName='$methodName', line=$line, exceptionName='$exceptionName')"
    }
}