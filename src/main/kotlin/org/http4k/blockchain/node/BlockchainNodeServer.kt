package org.http4k.blockchain.node

import org.http4k.blockchain.Stack
import org.http4k.blockchain.Wallet
import org.http4k.blockchain.registry.RemoteNodeRegistry
import org.http4k.core.Uri
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import java.util.*

object BlockchainNodeServer {
    operator fun invoke(port: Int, registryPort: Int) = object : Http4kServer {
        private val nodeAddress = Uri.of("http://localhost:$port")

        private val blockchain = LocalNode(nodeAddress)

        private val server = Stack.server(nodeAddress, BlockchainNodeServerApi(blockchain, Wallet(UUID.randomUUID()))).asServer(Jetty(port))

        private val registry = RemoteNodeRegistry(Uri.of("http://localhost:$registryPort"))

        override fun start(): Http4kServer {
            server.start()
            registry.register(nodeAddress).forEach { blockchain.register(it) }
            return server
        }

        override fun stop() {
            registry.deregister(nodeAddress)
            server.stop()
        }

    }
}