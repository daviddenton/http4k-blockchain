package org.http4k.blockchain.registry

import io.github.konfigur8.Configuration
import org.http4k.blockchain.Settings
import org.http4k.blockchain.Stack
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer

object NodeRegistryServer {
    operator fun invoke(configuration: Configuration): Http4kServer {
        val port = configuration[Settings.REGISTRY_PORT]
        return Stack.server(port, NodeRegistryApi(LocalNodeRegistry())).asServer(Jetty(port))
    }
}