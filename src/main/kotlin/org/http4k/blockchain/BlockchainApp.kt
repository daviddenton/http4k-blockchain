package org.http4k.blockchain

import org.http4k.blockchain.BlockchainApp.Contract.BlockMined
import org.http4k.blockchain.BlockchainApp.Contract.Chain
import org.http4k.blockchain.BlockchainApp.Contract.TransactionCreated
import org.http4k.blockchain.BlockchainApp.Contract.blockMined
import org.http4k.blockchain.BlockchainApp.Contract.transaction
import org.http4k.blockchain.BlockchainApp.Contract.transactionCreated
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson.auto
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.util.*

object BlockchainApp {
    private val CHAIN_WALLET = Wallet(UUID.fromString("3070562a-4c95-47d0-9994-a3aaaf44b8b8"))

    private val blockchain = Blockchain()

    operator fun invoke(chainWallet: Wallet): HttpHandler =
        ServerFilters.CatchAll()
            .then(ServerFilters.CatchLensFailure)
            .then(
                routes(
                    "/transactions/new" bind POST to {
                        Response(OK).with(
                            transactionCreated of TransactionCreated(
                                blockchain.newTransaction(transaction.extract(it))))
                    },
                    "/mine" bind GET to {
                        val newBlock = mineBlock(chainWallet)
                        Response(Status.CREATED).with(blockMined of BlockMined(newBlock))
                    },
                    "/chain" bind GET to {
                        Response(OK).with(Contract.chain of
                            Chain(blockchain.chain(), blockchain.chain().size))
                    }

                ))

    object Contract {
        data class Chain(val chain: List<Block>, val length: Int)
        data class TransactionCreated(val index: Int)
        data class BlockMined(val block: Block)

        val transaction = Body.auto<Transaction>().toLens()
        val chain = Body.auto<Chain>().toLens()
        val transactionCreated = Body.auto<TransactionCreated>().toLens()
        val blockMined = Body.auto<BlockMined>().toLens()
    }

    private fun mineBlock(nodeWallet: Wallet): Block {
        val nextProof = proof(blockchain.lastBlock().proof)
        blockchain.newTransaction(Transaction(CHAIN_WALLET, nodeWallet, 1))
        return blockchain.newBlock(nextProof)
    }
}
