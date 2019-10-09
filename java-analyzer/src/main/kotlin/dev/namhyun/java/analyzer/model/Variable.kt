package dev.namhyun.java.analyzer.model

interface Variable {
    val type: String
    val name: String
    val value: Any?
}

data class PrimitiveVariable(
    override val type: String,
    override val name: String,
    override val value: Any?
) : Variable

interface ReferenceVariable : Variable {
    val uniqueId: Long
}

data class ObjectVariable(
    override val type: String,
    override val name: String,
    override val value: Any?,
    override val uniqueId: Long
) : ReferenceVariable

data class StringVariable(
    override val type: String = "String",
    override val name: String,
    override val value: Any?,
    override val uniqueId: Long
) : ReferenceVariable

data class ArrayVariable(
    override val type: String = "Array",
    override val name: String,
    override val value: List<Any?>,
    override val uniqueId: Long,
    val size: Int
) : ReferenceVariable

data class ArrayListVariable(
    override val type: String = "ArrayList",
    override val name: String,
    override val value: List<Any?>,
    override val uniqueId: Long,
    val size: Int
) : ReferenceVariable