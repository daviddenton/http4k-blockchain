package org.http4k.blockchain.node

import org.http4k.blockchain.Block
import org.http4k.blockchain.Transaction
import org.http4k.blockchain.Wallet
import java.util.*

data class Blockchain(val unconfirmed: Set<Transaction> = emptySet(),
                      val chain: List<Block> = emptyList()) {
    operator fun plus(new: Transaction): Blockchain = copy(unconfirmed = unconfirmed + new)
    operator fun plus(new: Block): Blockchain = copy(chain = chain + new)

    fun balanceOf(wallet: Wallet): Int = chain.flatMap { it.transactions }
        .plus(unconfirmed)
        .balanceFor(wallet)

    fun last(): Block = chain.last()

    companion object {
        private val SEED_WALLET = Wallet(UUID.fromString("00000000-0000-0000-0000-000000000000"))
        fun newChain(chainWallet: Wallet) = Blockchain() + Transaction(SEED_WALLET, chainWallet, 10)
    }
}
