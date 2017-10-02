package org.http4k.blockchain

import org.http4k.core.Uri
import org.http4k.server.Jetty
import org.http4k.server.asServer
import java.util.*

object BlockchainNodeServer {
    operator fun invoke(port: Int) =
        BlockchainApp(Uri.of("http://localhost:$port"), Wallet(UUID.randomUUID())).asServer(Jetty(port))
}