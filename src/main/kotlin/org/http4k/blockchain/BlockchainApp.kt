package org.http4k.blockchain

import org.http4k.blockchain.Protocol.block
import org.http4k.blockchain.Protocol.chain
import org.http4k.blockchain.Protocol.nodes
import org.http4k.blockchain.Protocol.register
import org.http4k.blockchain.Protocol.resolution
import org.http4k.blockchain.Protocol.transaction
import org.http4k.blockchain.Protocol.transactionCreated
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
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
                    "/transactions" bind routes(
                        POST to { req: Request ->
                            Response(OK).with(
                                transactionCreated of blockchain.newTransaction(transaction.extract(req)))
                        },
                        GET to { _: Request ->
                            Response(OK).with(nodes of blockchain.nodes.map { it.address.toString() })
                        }),
                    "/mine" bind GET to {
                        Response(CREATED).with(block of mineBlock())
                    },
                    "/chain" bind GET to {
                        Response(OK).with(chain of blockchain.chain())
                    },
                    "/nodes" bind routes(
                        "/resolve" bind GET to
                            {
                                val replaced = blockchain.resolveConflicts()
                                val message = if (replaced) "replaced" else "not replaced"
                                Response(OK).with(resolution of Resolution(message, blockchain.chain()))
                            },
                        "/" bind routes(
                            POST to { req: Request ->
                                register.extract(req).map(::RemoteBlockchainNode).map(blockchain::registerNode)
                                Response(CREATED).with(nodes of blockchain.nodes.map { it.address.toString() })
                            },
                            GET to { _: Request ->
                                Response(OK).with(nodes of blockchain.nodes.map { it.address.toString() })
                            }
                        )
                    )
                ))
    }
}
