package com.cz.convert.model

import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiField

/**
 * Created by cz on 2017/6/28.
 */
class ClassElementItem(val name: String,val annotation: PsiAnnotation?,val key:String?) {
    operator fun component1(): String=name
    operator fun component2(): PsiAnnotation?=annotation
    operator fun component3(): String?=key
}