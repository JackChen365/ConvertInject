package com.cz.convert.model

/**
 * Created by cz on 2017/6/26.
 */
class InjectItem{
    var name:String?=null
    val actionItems= mutableListOf<ActionItem>()
    override fun toString(): String =name?:""
}
