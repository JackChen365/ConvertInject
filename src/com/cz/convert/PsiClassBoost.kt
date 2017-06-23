package com.cz.convert

import com.intellij.psi.PsiClass

/**
 * Created by cz on 2017/6/16.
 */
val ACTIVITY="android.app.Activity"
val DIALOG="android.app.Dialog"
val FRAGMENT_ITEMS= arrayOf("android.app.Fragment","android.support.v4.app.Fragment")
fun PsiClass.isActivity():Boolean=condition(this){it==ACTIVITY}

fun PsiClass.isFragment():Boolean= condition(this){ qualifiedName->FRAGMENT_ITEMS.any { it==qualifiedName }}


fun PsiClass.isDialog():Boolean=condition(this){it==DIALOG}

fun condition(clazz:PsiClass,closure:(String?)->Boolean):Boolean{
    var result=false
    var clazz: PsiClass?=clazz
    while(null!=clazz){
        if(closure.invoke(clazz.qualifiedName)){
            result=true
            break
        }
        clazz=clazz?.superClass
    }
    return result
}