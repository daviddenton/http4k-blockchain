package org.http4k.blockchain.node

import org.http4k.blockchain.Block
import org.http4k.blockchain.Protocol
import org.http4k.blockchain.Transaction
import org.http4k.blockchain.Wallet
import org.http4k.blockchain.proof
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.util.*

fun NodeApi(node: LocalNode, nodeWallet: Wallet): RoutingHttpHandler {
    val CHAIN_WALLET = Wallet(UUID.fromString("3070562a-4c95-47d0-9994-a3aaaf44b8b8"))

    fun mineBlock(): Block {
        val nextProof = proof(node.lastBlock().proof)
        node.newTransaction(Transaction(CHAIN_WALLET, nodeWallet, 1))
        return node.newBlock(nextProof)
    }

    return routes(
        "/chain" bind Method.GET to {
            Response(Status.OK).with(Protocol.chain of node.chain())
        },
        "/mine" bind Method.GET to {
            Response(Status.CREATED).with(Protocol.block of mineBlock())
        },
        "/resolve" bind Method.GET to {
            val status = if (node.resolveConflicts()) Status.CREATED else Status.NOT_MODIFIED
            Response(status).with(Protocol.chain of node.chain())
        },
        "/transactions" bind routes(
            Method.POST to { _: Request -> Response(Status.CREATED) },
            Method.GET to { _: Request -> Response(Status.OK).with(Protocol.nodeList of node.nodes().toList()) })
    )
}