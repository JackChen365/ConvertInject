package com.cz.convert.model

/**
 * Created by cz on 2017/6/27.
 */
class ActionItem{
    lateinit var name:String
    lateinit var clazz:String
    lateinit var action:ActionType
    var key:String="value"
    override fun toString(): String ="$name($key)[${action.value}]"
}