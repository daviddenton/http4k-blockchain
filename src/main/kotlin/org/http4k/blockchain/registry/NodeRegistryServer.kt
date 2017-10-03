package org.http4k.blockchain.registry

import org.http4k.server.Jetty
import org.http4k.server.asServer

object NodeRegistryServer {
    operator fun invoke(port: Int) =
        NodeRegistryApi(LocalNodeRegistry()).asServer(Jetty(port))
}