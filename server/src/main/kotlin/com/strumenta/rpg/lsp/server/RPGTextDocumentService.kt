package com.strumenta.rpg.lsp.server

import com.strumenta.lsif.core.normalizeUri
import com.strumenta.rpg.lsp.server.actions.definitionAt
import org.eclipse.lsp4j.*
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.LanguageClientAware
import org.eclipse.lsp4j.services.TextDocumentService
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.util.concurrent.CompletableFuture



class RPGTextDocumentService (val server : RPGLanguageServer) : TextDocumentService, LanguageClientAware, Closeable {
    private val LOG = LoggerFactory.getLogger(javaClass)
    private lateinit var client: LanguageClient
    private val async = AsyncExecutor()

    override fun connect(client: LanguageClient) {
        LOG.info("connect")
        this.client = client
    }

    override fun didOpen(params: DidOpenTextDocumentParams) {
        LOG.info("didOpen: ${params.textDocument.uri}")
    }

    override fun didSave(params: DidSaveTextDocumentParams) {
        LOG.info("didSave")
    }

    override fun didClose(params: DidCloseTextDocumentParams) {
        LOG.info("didClose")
    }

    override fun didChange(params: DidChangeTextDocumentParams) {
        LOG.info("didChange")
    }

    override fun close() {
        LOG.info("close")
        async.shutdown(awaitTermination = true)
    }

    override fun documentSymbol(params: DocumentSymbolParams?): CompletableFuture<MutableList<Either<SymbolInformation, DocumentSymbol>>> {
        LOG.info("documentSymbol  $params")
        val result: MutableList<Either<SymbolInformation, DocumentSymbol>> = mutableListOf()
        return CompletableFuture.completedFuture(result);
    }

    override fun definition(params: DefinitionParams?): CompletableFuture<Either<MutableList<out Location>, MutableList<out LocationLink>>> {

        val line = params!!.position.line + 1
        val column = params!!.position.character
        var uri = normalizeUri(params.textDocument.uri)
        LOG.info("definition $uri $line,$column")

        val locations = definitionAt(line,column,uri,LOG)

        locations.forEach { it
            LOG.info("definition $it")
        }
        return CompletableFuture.completedFuture(Either.forLeft(locations))
    }
}

