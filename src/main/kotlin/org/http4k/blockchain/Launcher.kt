package org.http4k.blockchain

import org.http4k.blockchain.node.BlockchainNodeServer

fun main(args: Array<String>) {
    BlockchainNodeServer(Settings.defaults
        .withProp(Settings.NODE_PORT, if (args.isNotEmpty()) args[0].toInt() else 5000)
        .reify()).startAndBlock()
}
