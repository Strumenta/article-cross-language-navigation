package com.strumenta.lsif.core

import java.util.concurrent.ConcurrentHashMap

object Repository {

    private val crossReferenceMap: MutableMap<String, CrossReferenceData> = ConcurrentHashMap<String, CrossReferenceData>()

    fun addCrossReference(name : String, xref : CrossReferenceData) {
        crossReferenceMap[name] = xref
    }
    fun getCrossReference(name: String): CrossReferenceData? {
        return crossReferenceMap[name]
    }

}
