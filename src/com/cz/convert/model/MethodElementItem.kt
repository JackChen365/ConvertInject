package com.cz.convert.model

import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMember
import com.intellij.psi.PsiMethod

/**
 * Created by cz on 2017/6/28.
 */
class MethodElementItem(val method: PsiMethod,val annotation: PsiAnnotation?,val actionItem:ActionItem?) {
    operator fun component1(): PsiMethod=method
    operator fun component2(): PsiAnnotation?=annotation
    operator fun component3(): ActionItem?=actionItem
}