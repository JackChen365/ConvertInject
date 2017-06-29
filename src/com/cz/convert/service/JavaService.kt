/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package com.cz.convert.service

import com.google.common.base.Splitter
import com.google.common.collect.Lists
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.TextRange
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiPackage
import com.intellij.psi.PsiType
import com.intellij.psi.impl.JavaPsiFacadeEx
import com.intellij.psi.search.GlobalSearchScope

/**
 * Java psi relative service

 * Author: jianyu.lin
 * Date: 2016/11/23 Time: 上午1:05
 */
class JavaService private constructor(private val project: Project) {
    private val javaPsiFacade: JavaPsiFacade = JavaPsiFacade.getInstance(project)
    private val javaPsiFacadeEx: JavaPsiFacadeEx = JavaPsiFacadeEx.getInstanceEx(project)
    val elementFactory: PsiElementFactory = JavaPsiFacade.getElementFactory(project)

    /**
     * search the directory relate to classPath
     * @param javaPath java path
     * *
     * @return directories
     */
    fun getRelatedDirectories(javaPath: String?): Array<PsiDirectory> {
        if (javaPath == null) {
            return PsiDirectory.EMPTY_ARRAY
        }
        val psiPackage = findPackage(javaPath) ?: return PsiDirectory.EMPTY_ARRAY
        return psiPackage.getDirectories(GlobalSearchScope.projectScope(project))
    }

    /**
     * resolve java class path error indexes
     * @param qualifiedName string path
     * *
     * @return error part indexes
     */
    fun getQualifiedClassErrorRanges(qualifiedName: String?): List<Pair<String, TextRange>> {
        if (qualifiedName == null) {
            return Lists.newArrayList<Pair<String, TextRange>>()
        }
        val iterable = POINT_SPLITTER.split(qualifiedName).iterator()
        val result = Lists.newArrayList<Pair<String, TextRange>>()
        var temp = ""
        while (iterable.hasNext()) {
            val level = iterable.next()
            temp += level
            if (result.size > 0 || iterable.hasNext() && findPackage(temp) == null
                    || !iterable.hasNext() && findProjectClass(temp) == null) {
                val index = qualifiedName.lastIndexOf(level) + 1
                result.add(Pair.createNonNull(level, TextRange.create(index, index + level.length)))
            }
            temp += "."
        }
        return result
    }

    /**
     * resolve java package path error indexes
     * @param packagePath string path
     * *
     * @return error part indexes
     */
    fun getClassPathRelatedPathErrorRanges(packagePath: String): List<Pair<String, TextRange>> {
        return getPackagePathErrorRanges(PHYSICAL_SPLITTER.split(packagePath).iterator(), packagePath)
    }

    /**
     * resolve java package path error indexes
     * @param packagePath string path
     * *
     * @return error part indexes
     */
    fun getPackagePathErrorRanges(packagePath: String): List<Pair<String, TextRange>> {
        return getPackagePathErrorRanges(POINT_SPLITTER.split(packagePath).iterator(), packagePath)
    }

    /**
     * resolve java package or class path related directory path error indexes
     * @param packagePath string path
     * *
     * @return error part indexes
     */
    private fun getPackagePathErrorRanges(iterable: Iterator<String>,
                                          packagePath: String): List<Pair<String, TextRange>> {
        val result = Lists.newArrayList<Pair<String, TextRange>>()
        var temp = ""
        while (iterable.hasNext()) {
            val level = iterable.next()
            temp += level
            if (result.size > 0 || findPackage(temp) == null) {
                val index = packagePath.lastIndexOf(level) + 1
                result.add(Pair.createNonNull(level, TextRange.create(index, index + level.length)))
            }
            temp += "."
        }
        return result
    }

    /**
     * search java class with specified scope
     * @param qualifiedName full qualified class name
     * *
     * @param scope search scope
     * *
     * @return java class obj
     */
    fun findClass(qualifiedName: String?, scope: GlobalSearchScope?): PsiClass? {
        if (qualifiedName == null || scope == null) {
            return null
        }
        return ApplicationManager.getApplication().runReadAction(Computable<PsiClass> { javaPsiFacade.findClass(qualifiedName, scope) })
    }

    /**
     * search java class with project scope
     * @param qualifiedName full qualified class name
     */
    fun findProjectClass(qualifiedName: String): PsiClass? {
        return findClass(qualifiedName, GlobalSearchScope.projectScope(project))
    }

    /**
     * search java class with all scope
     * @param qualifiedName full qualified class name
     * *
     * @return java class obj
     */
    fun findClass(qualifiedName: String?): PsiClass? {
        if (qualifiedName == null) {
            return null
        }
        return ApplicationManager.getApplication().runReadAction(Computable<PsiClass> { javaPsiFacadeEx.findClass(qualifiedName) })
    }

    /**
     * search java package
     * @param qualifiedName full qualified package name
     * *
     * @return java package obj
     */
    fun findPackage(qualifiedName: String?): PsiPackage? {
        if (qualifiedName == null) {
            return null
        }
        return ApplicationManager.getApplication().runReadAction(Computable<PsiPackage> { javaPsiFacade.findPackage(qualifiedName) })
    }

    /**
     * generate java class
     * @param qualifiedName qualified name
     * *
     * @return psi class
     */
    fun generateInterface(qualifiedName: String): PsiClass {
        return elementFactory.createInterface(qualifiedName)
    }

    /**
     * generate java interface
     * @param qualifiedName qualified name
     * *
     * @return psi class
     */
    fun generateClass(qualifiedName: String): PsiClass {
        return elementFactory.createClass(qualifiedName)
    }

    /**
     * generate java method
     * @param psiClass containing class
     * *
     * @param returnType return type
     * *
     * @param name method name
     * *
     * @return psi method
     */
    fun generateMethod(psiClass: PsiClass, returnType: PsiType, name: String): PsiMethod {
        return elementFactory.createMethod(name, returnType, psiClass)
    }

    companion object {

        private val POINT_SPLITTER = Splitter.on('.').omitEmptyStrings()
        private val PHYSICAL_SPLITTER = Splitter.on('/').omitEmptyStrings()

        fun getInstance(project: Project): JavaService {
            return ServiceManager.getService(project, JavaService::class.java)
        }
    }
}
