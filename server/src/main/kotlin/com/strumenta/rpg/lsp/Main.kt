package com.strumenta.rpg.lsp

import com.strumenta.rpg.lsp.server.RPGLanguageServer
import java.util.concurrent.Executors
import org.eclipse.lsp4j.launch.LSPLauncher

fun main(argv: Array<String>) {
    // Redirect java.util.logging calls (e.g. from LSP4J)
    //LOG.connectJULFrontend()

    val inStream = System.`in`
    val outStream = System.out

    val server = RPGLanguageServer()
    val threads = Executors.newSingleThreadExecutor { Thread(it, "client") }
    val launcher = LSPLauncher.createServerLauncher(server, inStream, outStream, threads, { it })

    server.connect(launcher.remoteProxy)
    launcher.startListening()
}
