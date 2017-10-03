package org.http4k.blockchain.node

import io.github.konfigur8.Configuration
import org.http4k.blockchain.Settings
import org.http4k.blockchain.Stack
import org.http4k.blockchain.registry.RemoteNodeRegistry
import org.http4k.core.Uri
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer

object BlockchainNodeServer {
    operator fun invoke(configuration: Configuration) = object : Http4kServer {

        val port = configuration[Settings.NODE_PORT]
        val registryPort = configuration[Settings.REGISTRY_PORT]
        val chainWallet = configuration[Settings.CHAIN_WALLET]
        private val node = LocalNode(port.toLocalUri(), RemoteNodeRegistry(registryPort.toLocalUri()), chainWallet)

        private val server = Stack.server(port, BlockchainNodeServerApi(node)).asServer(Jetty(port))

        override fun start(): Http4kServer {
            server.start()
            node.start()
            return server
        }

        override fun stop() {
            node.stop()
            server.stop()
        }
    }
}

fun Int.toLocalUri() = Uri.of("http://localhost:$this")