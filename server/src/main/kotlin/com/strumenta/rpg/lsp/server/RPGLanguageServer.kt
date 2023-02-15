package com.strumenta.rpg.lsp.server

import com.strumenta.rpg.lsp.server.progress.LanguageClientProgress
import org.eclipse.lsp4j.*
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.*
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.io.File
import java.net.URI
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.system.exitProcess




class RPGLanguageServer : LanguageServer, LanguageClientAware, Closeable {
    val LOG = LoggerFactory.getLogger(javaClass)
    private lateinit var client: LanguageClient
    private val workspace = RPGWorkspaceService(this)
    private val textDocuments = RPGTextDocumentService(this)
    private val async = AsyncExecutor()

    private val workerPool : ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

    fun getWorkerPool(): ExecutorService = workerPool

    override fun connect(client: LanguageClient) {
        LOG.info("connect")

        this.client = client
        workspace.connect(client)
        textDocuments.connect(client)
    }

    override fun initialize(params: InitializeParams): CompletableFuture<InitializeResult>  = async.compute {
        LOG.info("initialize")

        val serverCapabilities = ServerCapabilities()
        serverCapabilities.setTextDocumentSync(TextDocumentSyncKind.Incremental)
        serverCapabilities.workspace = WorkspaceServerCapabilities()
        serverCapabilities.workspace.workspaceFolders = WorkspaceFoldersOptions()
        serverCapabilities.workspace.workspaceFolders.supported = true
        serverCapabilities.workspace.workspaceFolders.changeNotifications = Either.forRight(true)
        serverCapabilities.hoverProvider = Either.forLeft(false)
        serverCapabilities.completionProvider = CompletionOptions(false, listOf("."))
        serverCapabilities.signatureHelpProvider = SignatureHelpOptions(listOf("(", ","))
        serverCapabilities.definitionProvider = Either.forLeft(true)
        serverCapabilities.documentSymbolProvider = Either.forLeft(true)
        serverCapabilities.workspaceSymbolProvider = Either.forLeft(true)
        serverCapabilities.referencesProvider = Either.forLeft(false)
        serverCapabilities.codeActionProvider = Either.forLeft(false)
        serverCapabilities.documentFormattingProvider = Either.forLeft(true)
        serverCapabilities.documentRangeFormattingProvider = Either.forLeft(true)
        serverCapabilities.callHierarchyProvider = Either.forLeft(true)

        val progress = params.workDoneToken?.let { LanguageClientProgress("Workspace folders", it, client) }

        val extensions = listOf("idx")
        params.workspaceFolders.forEach { ws ->
            val directory = File(URI(ws.uri))
            val files = directory.walk().filter { it.isFile && extensions.contains(it.extension) }.toList()
            LOG.info("Adding workspace ${ws.uri} to source path ${directory.exists()} ${files.size}")

            if(directory.exists()) {
                files.forEachIndexed { i, file ->
                    val progressPrefix = "[${i + 1}/${files.size}] ${file.name}"
                    val progressPercent = (100 * i) / files.size
                    progress?.update("$progressPrefix: Updating source path", progressPercent)

                    workerPool.submit(RPGParserRunner(file,this))

                    LOG.debug("Adding file ${file.absolutePath} to workspace")
                }
            }

        }
        progress?.close()
        val clientCapabilities = params.capabilities
        InitializeResult(serverCapabilities)
    }

    override fun getTextDocumentService(): RPGTextDocumentService = textDocuments

    override fun getWorkspaceService(): RPGWorkspaceService = workspace

    override fun getNotebookDocumentService(): NotebookDocumentService? {
        return null;
    }

    override fun shutdown(): CompletableFuture<Any> {
        LOG.info("shutdown")
        workerPool.shutdown()
        while (!workerPool.isTerminated) { }
        close()
        return CompletableFuture.completedFuture(null)
    }

    override fun close() {
        textDocumentService.close()
        workspace.close()
        LOG.info("close")
        exit()
    }

    override fun exit() {
        LOG.info("exit")
        exitProcess(0)
    }

}