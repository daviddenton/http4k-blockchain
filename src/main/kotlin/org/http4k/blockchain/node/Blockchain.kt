package org.http4k.blockchain.node

import org.http4k.blockchain.Block
import org.http4k.blockchain.BlockHash
import org.http4k.blockchain.Proof
import org.http4k.blockchain.Transaction
import org.http4k.blockchain.Wallet
import java.util.*

data class Blockchain(val unconfirmed: Set<Transaction> = emptySet(),
                      val chain: List<Block> = emptyList()) {
    operator fun plus(new: Transaction): Blockchain = copy(unconfirmed = unconfirmed + new)

    fun balanceOf(wallet: Wallet): Int = chain.flatMap { it.transactions }
        .plus(unconfirmed)
        .balanceFor(wallet)

    fun size() = chain.size

    fun last(): Block = chain.last()

    fun withNewBlock(proof: Proof, previousHash: BlockHash? = null) =
        Blockchain(chain = chain + Block(chain.size + 1, proof, System.currentTimeMillis(), unconfirmed, previousHash ?: last().hash()))

    companion object {
        private val SEED_WALLET = Wallet(UUID.fromString("00000000-0000-0000-0000-000000000000"))

        fun newChain(chainWallet: Wallet): Blockchain =
            (Blockchain() + Transaction(SEED_WALLET, chainWallet, 10))
                .withNewBlock(Proof(100), BlockHash("1"))
    }

    private fun Iterable<Transaction>.balanceFor(wallet: Wallet): Int =
        fold(0) { acc: Int, (sender, recipient, amount) ->
            when (wallet) {
                sender -> acc - amount
                recipient -> acc + amount
                else -> acc
            }
        }
}
