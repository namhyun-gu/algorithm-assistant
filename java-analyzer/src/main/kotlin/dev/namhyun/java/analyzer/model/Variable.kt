package dev.namhyun.java.analyzer.model

open class Variable(
    open val type: String,
    open val name: String,
    open val value: Any?
) {
    override fun toString(): String {
        return "Variable(type='$type', name='$name', value=$value)"
    }
}

open class ObjectVariable(
    override val type: String,
    override val name: String,
    override val value: Any?,
    open val uniqueId: Long
) : Variable(type, name, value) {
    override fun toString(): String {
        return "ObjectVariable(type='$type', name='$name', value=$value, uniqueId=$uniqueId)"
    }
}

class StringVariable(
    override val name: String,
    override val value: Any?,
    override val uniqueId: Long
) : ObjectVariable("String", name, value, uniqueId) {
    override fun toString(): String {
        return "StringVariable(name='$name', value=$value, uniqueId=$uniqueId)"
    }
}

class ArrayVariable(
    override val name: String,
    val values: List<Any?>?,
    val size: Int,
    override val uniqueId: Long
) : ObjectVariable("Array", name, values, uniqueId) {
    override fun toString(): String {
        return "ArrayVariable(name='$name', values=$values, size=$size, uniqueId=$uniqueId)"
    }
}

class ArrayListVariable(
    override val name: String,
    val values: List<Any?>?,
    val size: Int,
    override val uniqueId: Long
) : ObjectVariable("ArrayList", name, values, uniqueId) {
    override fun toString(): String {
        return "ArrayListVariable(name='$name', values=$values, size=$size, uniqueId=$uniqueId)"
    }
}