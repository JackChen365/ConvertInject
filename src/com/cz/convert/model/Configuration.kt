package com.cz.convert.model

/**
 * Created by cz on 2017/6/28.
 */
class Configuration{
    lateinit var methodName:String
    var index:Int=0
    val items= mutableListOf<InjectItem>()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as Configuration
        if (methodName != other.methodName) return false
        if (index != other.index) return false
        if (items != other.items) return false

        return true
    }

    override fun hashCode(): Int {
        var result = methodName.hashCode()
        result = 31 * result + index
        result = 31 * result + items.hashCode()
        return result
    }

}