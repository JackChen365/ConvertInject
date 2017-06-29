package com.cz.convert

import com.intellij.psi.PsiClass

/**
 * Created by cz on 2017/6/16.
 */
val ACTIVITY="android.app.Activity"
val DIALOG="android.app.Dialog"
val VIEW="android.view.View"
val VIEW_HOLDER="android.support.v7.widget.RecyclerView.ViewHolder"
val FRAGMENT_ITEMS= arrayOf("android.app.Fragment","android.support.v4.app.Fragment")
fun PsiClass.isActivity():Boolean=condition(this){it==ACTIVITY}

fun PsiClass.isFragment():Boolean= condition(this){ qualifiedName->FRAGMENT_ITEMS.any { it==qualifiedName }}


fun PsiClass.isDialog():Boolean=condition(this){it==DIALOG}

fun PsiClass.isView():Boolean=condition(this){it==VIEW}

fun PsiClass.isViewHolder():Boolean=condition(this){it==VIEW_HOLDER}

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