package com.strumenta.lsif.core

import com.beust.klaxon.JsonObject
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


object IConstant {
    const val DEFAULT_LSIF_FILE_NAME = "lsif.json"
    const val LSIF_FORMAT_VERSION = "0.5.0"
}

abstract class Element(open val id: String, val type: String, val label: String) {

    companion object {
        const val VERTEX = "vertex"
        const val EDGE = "edge"
    }
}

open class Vertex(override val id: String, label: String?) :
    Element(id, VERTEX, label!!) {
    companion object {
        const val METADATA = "metaData"
        const val EVENT = "\$event"
        const val PROJECT = "project"
        const val RANGE = "range"
        const val LOCATION = "location"
        const val DOCUMENT = "document"
        const val EXTERNALIMPORTITEM = "externalImportItem"
        const val EXPORTITEM = "exportItem"
        const val RESULTSET = "resultSet"
        const val DOCUMENTSYMBOLRESULT = "documentSymbolResult"
        const val FOLDINGRANGERESULT = "foldingRangeResult"
        const val DOCUMENTLINKRESULT = "documentLinkResult"
        const val DIAGNOSTICRESULT = "diagnosticResult"
        const val DECLARATIONRESULT = "declarationResult"
        const val DEFINITIONRESULT = "definitionResult"
        const val TYPEDEFINITIONRESULT = "typeDefinitionResult"
        const val HOVERRESULT = "hoverResult"
        const val REFERENCERESULT = "referenceResult"
        const val IMPLEMENTATIONRESULT = "implementationResult"
        const val GROUP = "group"
        const val MONIKER = "moniker"
        const val PACKAGEINFORMATION = "packageInformation"
    }
}

data class Project(
    override val id: String,
    val name: String) : Vertex(id, PROJECT)

data class Document(
    override val id: String,
    val uri: String,
) : Vertex(id, DOCUMENT)

data class SymbolData(var uri : String = "" ,
                      var fields : MutableMap<String,String> = mutableMapOf() )


data class RefPosition(
    val startLine   : Int,
    val startCol    : Int,
    val endLine     : Int,
    val endColumn   : Int)

data class CrossReference(
    val references : MutableMap<String,CrossReferenceData> = mutableMapOf()
)

data class CrossReferenceData(
    var uri : String = "" ,
    var crossReference  : MutableMap<String,RefPosition> = mutableMapOf()
)


fun normalizeUri(uri: String): String {
    return if(uri.startsWith("file:///"))
        uri.replace("file:///","file:/")
    else uri
}


object LsifUtils {
    /**
     * Normalize the URI to the same format as the client.
     */

    fun encodeToBase64(input: String): String {
        return Base64.getEncoder().withoutPadding().encodeToString(input.toByteArray())
    }
}