package com.strumenta.rpg.lsp.server

import org.eclipse.lsp4j.DidChangeConfigurationParams
import org.eclipse.lsp4j.DidChangeWatchedFilesParams
import org.eclipse.lsp4j.DidChangeWorkspaceFoldersParams
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.LanguageClientAware
import org.eclipse.lsp4j.services.WorkspaceService
import org.slf4j.LoggerFactory
import java.io.Closeable

class RPGWorkspaceService (val server : RPGLanguageServer, val sources : MutableList<String> = mutableListOf()) : WorkspaceService,
    LanguageClientAware, Closeable {
    private val LOG = LoggerFactory.getLogger(javaClass)


    override fun didChangeWatchedFiles(params: DidChangeWatchedFilesParams) {
        LOG.info("didChangeWatchedFiles")
    }

    override fun didChangeWorkspaceFolders(params: DidChangeWorkspaceFoldersParams) {
        LOG.info("didChangeWorkspaceFolders")
    }
    override fun didChangeConfiguration(params: DidChangeConfigurationParams) {
        LOG.info("didChangeConfiguration")
    }

    override fun connect(client: LanguageClient) {
        LOG.info("connect")
    }

    override fun close() {
        LOG.info("close")
    }
}