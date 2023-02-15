package com.strumenta.rpg.lsp.server.actions


import com.strumenta.lsif.core.Repository
import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range
import org.slf4j.Logger
import kotlin.system.measureTimeMillis

fun definitionAt(line: Int, column: Int, uri: String, LOG: Logger? = null) : MutableList<Location> {
    val locations : MutableList<Location> = mutableListOf()


    when {
        uri.endsWith("/py") || uri.endsWith("/rpgle") -> {
            val xuri = uri.substring(0,uri.lastIndexOf('/'))
            LOG?.info("xuri: $xuri")
            val xref = Repository.getCrossReference(xuri)
            xref?.apply {
                val targetURI = xref.uri
                LOG?.info("xuri Found")
                val elapsed = measureTimeMillis {
                    xref.crossReference.get("$line")?.apply {
                        val range =
                            Range(Position(this.startLine, this.startCol), Position(this.endLine, this.endColumn))
                        val location = Location(targetURI, range)
                        locations.add(location)
                    }
                }
                LOG?.info("index search time: $elapsed ms")
            }
        }

    }

    return locations
}