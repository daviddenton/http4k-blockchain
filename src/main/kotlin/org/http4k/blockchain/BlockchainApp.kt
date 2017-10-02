package org.http4k.blockchain

import org.http4k.blockchain.Protocol.block
import org.http4k.blockchain.Protocol.chain
import org.http4k.blockchain.Protocol.nodeList
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.NOT_MODIFIED
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ServerFilters
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.util.*

object BlockchainApp {
    private val CHAIN_WALLET = Wallet(UUID.fromString("3070562a-4c95-47d0-9994-a3aaaf44b8b8"))

    operator fun invoke(address: Uri, nodeWallet: Wallet): HttpHandler {
        val blockchain = LocalBlockchainNode(address)

        fun mineBlock(): Block {
            val nextProof = proof(blockchain.lastBlock().proof)
            blockchain.newTransaction(Transaction(CHAIN_WALLET, nodeWallet, 1))
            return blockchain.newBlock(nextProof)
        }

        return ServerFilters.CatchAll()
            .then(ServerFilters.CatchLensFailure)
            .then(
                routes(
                    "/chain" bind GET to {
                        Response(OK).with(chain of blockchain.chain())
                    },
                    "/mine" bind GET to {
                        Response(CREATED).with(block of mineBlock())
                    },
                    "/nodes" bind routes(
                        "/resolve" bind GET to
                            {
                                val status = if (blockchain.resolveConflicts()) CREATED else NOT_MODIFIED
                                Response(status).with(chain of blockchain.chain())
                            },
                        "/" bind routes(
                            POST to { req: Request ->
                                nodeList.extract(req).map(::RemoteBlockchainNode).map(blockchain::registerNode)
                                Response(CREATED).with(nodeList of blockchain.nodes())
                            },
                            GET to { _: Request ->
                                Response(OK).with(nodeList of blockchain.nodes())
                            }
                        )
                    ),
                    "/transactions" bind routes(
                        POST to { req: Request ->
                            Response(OK).with(
                                Protocol.transactionCreated of blockchain.newTransaction(Protocol.transaction.extract(req)))
                        },
                        GET to { _: Request ->
                            Response(OK).with(nodeList of blockchain.nodes())
                        })
                ))
    }
}
