package org.http4k.blockchain

import org.http4k.server.Jetty
import org.http4k.server.asServer
import java.util.*

fun main(args: Array<String>) {
    val port = if (args.isNotEmpty()) args[0].toInt() else 5000

    BlockchainApp(Wallet(UUID.randomUUID())).asServer(Jetty(port)).startAndBlock()
}
