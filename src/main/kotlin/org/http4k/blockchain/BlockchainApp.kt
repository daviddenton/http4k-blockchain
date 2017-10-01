package org.http4k.blockchain

import org.http4k.blockchain.BlockchainApp.Contract.BlockMined
import org.http4k.blockchain.BlockchainApp.Contract.Resolution
import org.http4k.blockchain.BlockchainApp.Contract.TransactionCreated
import org.http4k.blockchain.BlockchainApp.Contract.blockMined
import org.http4k.blockchain.BlockchainApp.Contract.nodes
import org.http4k.blockchain.BlockchainApp.Contract.register
import org.http4k.blockchain.BlockchainApp.Contract.resolution
import org.http4k.blockchain.BlockchainApp.Contract.transaction
import org.http4k.blockchain.BlockchainApp.Contract.transactionCreated
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson.auto
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.util.*

object BlockchainApp {
    private val CHAIN_WALLET = Wallet(UUID.fromString("3070562a-4c95-47d0-9994-a3aaaf44b8b8"))

    private val blockchain = LocalBlockchainNode()

    operator fun invoke(chainWallet: Wallet): HttpHandler =
        ServerFilters.CatchAll()
            .then(ServerFilters.CatchLensFailure)
            .then(

                routes(
                    "/transactions" bind routes(
                        "/new" bind POST to {
                            Response(OK).with(
                                transactionCreated of TransactionCreated(
                                    blockchain.newTransaction(transaction.extract(it))))
                        }),
                    "/mine" bind GET to {
                        val newBlock = mineBlock(chainWallet)
                        Response(Status.CREATED).with(blockMined of BlockMined(newBlock))
                    },
                    "/chain" bind GET to {
                        Response(OK).with(Contract.chain of blockchain.chain())
                    },
                    "/nodes" bind routes(
                        "/register" bind POST to {
                            register.extract(it).map(blockchain::registerNode)
                            Response(OK).with(nodes of blockchain.nodes.map { it.address.toString() })
                        },
                        "/resolve" bind GET to {
                            val replaced = blockchain.resolveConflicts()
                            val message = if (replaced) "replaced" else "not replaced"
                            Response(OK).with(resolution of Resolution(message, blockchain.chain()))
                        }
                    )
                ))

    object Contract {
        data class TransactionCreated(val index: Int)
        data class BlockMined(val block: Block)
        data class Resolution(val message: String, val chain: List<Block>)

        val transaction = Body.auto<Transaction>().toLens()
        val register = Body.auto<List<String>>()
            .map { it.map { RemoteBlockchainNode(Uri.of(it)) } }.toLens()
        val chain = Body.auto<List<Block>>().toLens()
        val nodes = Body.auto<List<String>>().toLens()
        val transactionCreated = Body.auto<TransactionCreated>().toLens()
        val blockMined = Body.auto<BlockMined>().toLens()
        val resolution = Body.auto<Resolution>().toLens()
    }

    private fun mineBlock(nodeWallet: Wallet): Block {
        val nextProof = proof(blockchain.lastBlock().proof)
        blockchain.newTransaction(Transaction(CHAIN_WALLET, nodeWallet, 1))
        return blockchain.newBlock(nextProof)
    }
}
