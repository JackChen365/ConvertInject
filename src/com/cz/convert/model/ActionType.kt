package com.cz.convert.model

/**
 * Created by cz on 2017/6/27.
 */
enum class ActionType(val value:String){
    //初始化类
    BIND_INIT("BindInit"),
    //绑定类
    BIND_CLASS("BindActivity"),
    //绑定字段控件
    BIND_FIELD_VIEW("BindView"),
    //绑定字符串
    BIND_FIELD_STRING("BindString"),
    //绑定颜色值
    BIND_FIELD_COLOR("BindColor"),
    //绑定数组
    BIND_FIELD_ARRAY("BindArray"),
    //绑定bitmap
    BIND_FIELD_BITMAP("BindFieldBitmap"),
    //绑定drawable
    BIND_FIELD_DRAWABLE("BindFieldDrawable"),
    //绑定anim
    BIND_FIELD_ANIM("BindFieldAnim"),
    //绑定dimen
    BIND_FIELD_DIMEN("BindFieldDimen"),
    //绑定方法单击
    BIND_METHOD_CLICK("BindMethodClick"),
    //绑定方法长按
    BIND_METHOD_LONG_CLICK("BindMethodLongClick");

    override fun toString(): String=value
}