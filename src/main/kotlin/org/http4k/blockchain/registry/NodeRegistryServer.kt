package org.http4k.blockchain.registry

import org.http4k.blockchain.Stack
import org.http4k.core.Uri
import org.http4k.server.Jetty
import org.http4k.server.asServer

object NodeRegistryServer {
    operator fun invoke(port: Int) = Stack.server(
        Uri.of("http://localhost:$port"),
        NodeRegistryApi(LocalNodeRegistry())).asServer(Jetty(port))
}