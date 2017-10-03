package org.http4k.blockchain

import org.http4k.blockchain.node.BlockchainNodeServer
import java.util.*

val CHAIN_WALLET = Wallet(UUID.fromString("3070562a-4c95-47d0-9994-a3aaaf44b8b8"))

fun main(args: Array<String>) {
    BlockchainNodeServer(
        if (args.isNotEmpty()) args[0].toInt() else 5000, 8000, CHAIN_WALLET
    ).startAndBlock()
}
