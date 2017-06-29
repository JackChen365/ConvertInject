package com.cz.convert.model

import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiField

/**
 * Created by cz on 2017/6/28.
 */
class FieldElementItem(val field: PsiField,val annotation: PsiAnnotation?,val actionItem:ActionItem?) {
    operator fun component1(): PsiField=field
    operator fun component2(): PsiAnnotation?=annotation
    operator fun component3(): ActionItem?=actionItem
}