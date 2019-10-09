package dev.namhyun.java.analyzer.model

interface Frame {
    val methodName: String
    val line: Int
}

data class StepFrame(
    override val methodName: String,
    override val line: Int,
    val variables: List<Variable>
) : Frame

data class ExceptionFrame(
    override val methodName: String,
    override val line: Int,
    val exceptionName: String
) : Frame

data class MethodEntryFrame(
    override val methodName: String,
    override val line: Int
) : Frame

data class MethodExitFrame(
    override val methodName: String,
    override val line: Int
) : Frame