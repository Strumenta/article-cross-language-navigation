package com.strumenta.rpg.lsp.server

import com.beust.klaxon.Klaxon
import com.strumenta.lsif.core.CrossReference
import com.strumenta.lsif.core.Repository
import java.io.File
import kotlin.system.measureTimeMillis


class RPGParserRunner(val file: File,val server : RPGLanguageServer) : Runnable {

    override fun run() {
        server.LOG.debug("Parser started : ${file.name} ${file.lastModified()}")
        val elapsed = measureTimeMillis {
            try {
                    if ("idx".equals(file.extension)) {
                        val xref = Klaxon().parse<CrossReference>(file)

                        xref?.references?.forEach { k, v ->
                            Repository.addCrossReference(k, v)
                        }
                    }
            } catch (e: Exception) {
                server.LOG.error(e.message)
            }
        }

        server.LOG.debug("Parser completed: ${file.name} in $elapsed ms")
    }
}