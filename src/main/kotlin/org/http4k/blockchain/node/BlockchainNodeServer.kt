package org.http4k.blockchain.node

import org.http4k.blockchain.Stack
import org.http4k.blockchain.Wallet
import org.http4k.blockchain.registry.RemoteNodeRegistry
import org.http4k.core.Uri
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer

object BlockchainNodeServer {
    operator fun invoke(port: Int, registryPort: Int, chainWallet: Wallet) = object : Http4kServer {

        val address = Uri.of("http://localhost:$port")
        private val node = LocalNode(address, RemoteNodeRegistry(Uri.of("http://localhost:$registryPort")), chainWallet)

        private val server = Stack.server(address, BlockchainNodeServerApi(node)).asServer(Jetty(port))

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