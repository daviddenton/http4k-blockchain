package org.http4k.blockchain.node

import org.http4k.blockchain.Block
import org.http4k.blockchain.Protocol.block
import org.http4k.blockchain.Protocol.chain
import org.http4k.blockchain.Protocol.nodeList
import org.http4k.blockchain.Transaction
import org.http4k.blockchain.Wallet
import org.http4k.blockchain.proof
import org.http4k.blockchain.registry.NodeRegistryApi
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.NOT_MODIFIED
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ServerFilters
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.util.*

object BlockchainNodeApi {
    private val CHAIN_WALLET = Wallet(UUID.fromString("3070562a-4c95-47d0-9994-a3aaaf44b8b8"))

    operator fun invoke(node: LocalNode,
                        nodeWallet: Wallet): HttpHandler {

        fun mineBlock(): Block {
            val nextProof = proof(node.lastBlock().proof)
            node.newTransaction(Transaction(CHAIN_WALLET, nodeWallet, 1))
            return node.newBlock(nextProof)
        }

        return ServerFilters.CatchAll()
            .then(ServerFilters.CatchLensFailure)
            .then(
                routes(
                    "/chain" bind GET to {
                        Response(OK).with(chain of node.chain())
                    },
                    "/mine" bind GET to {
                        Response(CREATED).with(block of mineBlock())
                    },
                    "/nodes" bind routes(
                        "/nodes/resolve" bind GET to
                            {
                                val status = if (node.resolveConflicts()) CREATED else NOT_MODIFIED
                                Response(status).with(chain of node.chain())
                            },
                        "/nodes" bind NodeRegistryApi(node)
                    ),
                    "/transactions" bind routes(
                        POST to { _: Request -> Response(CREATED) },
                        GET to { _: Request -> Response(OK).with(nodeList of node.nodes().toList()) })
                ))
    }
}
