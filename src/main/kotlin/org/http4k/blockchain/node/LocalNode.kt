package org.http4k.blockchain.node

import org.http4k.blockchain.Block
import org.http4k.blockchain.BlockHash
import org.http4k.blockchain.Proof
import org.http4k.blockchain.Transaction
import org.http4k.blockchain.TransactionState
import org.http4k.blockchain.TransactionState.Rejected
import org.http4k.blockchain.Wallet
import org.http4k.blockchain.proof
import org.http4k.blockchain.registry.LocalNodeRegistry
import org.http4k.blockchain.registry.NodeRegistry
import org.http4k.core.Uri


class LocalNode(override val address: Uri,
                private val registry: NodeRegistry,
                private val chainWallet: Wallet,
                private val nodeWallet: Wallet
) : Node, NodeRegistry by LocalNodeRegistry() {
    private var unconfirmed = setOf<Transaction>()
    private var chain = listOf<Block>()

    init {
        newBlock(Proof(100), BlockHash("1"))
    }

    fun start() {
        registry.register(address).forEach { register(it) }
    }

    fun stop() {
        registry.deregister(address)
    }

    fun mineBlock(): Block {
        val nextProof = proof(lastBlock().proof)
        newTransaction(Transaction(chainWallet, nodeWallet, 1))
        return newBlock(nextProof)
    }

    override fun chain() = chain.toList()

    override fun transactions() = unconfirmed

    override fun newTransaction(newTransaction: Transaction): TransactionState =
        if (balanceOf(newTransaction.sender) > newTransaction.amount) {
            unconfirmed += newTransaction
            TransactionState.Accepted
        } else Rejected

    private fun balanceOf(wallet: Wallet): Int = chain.flatMap { it.transactions }
        .plus(unconfirmed)
        .balanceFor(wallet)

    private fun newBlock(proof: Proof, previousHash: BlockHash? = null): Block {
        val newBlock = Block(chain.size + 1, proof, System.currentTimeMillis(), unconfirmed, previousHash ?: lastBlock().hash())
        unconfirmed = mutableSetOf()
        chain = chain.plus(newBlock)
        return newBlock
    }

    private fun lastBlock(): Block = chain.last()

    private fun valid(newChain: List<Block>): Boolean {
        var lastBlock = newChain.first()
        var index = 1
        val block = chain[1]
        while (index < chain.size) {
            if (block.previousHash != lastBlock.hash()) return false
            if (!lastBlock().proof.validate(block.proof)) return false
            lastBlock = lastBlock()
            index += 1
        }
        return true
    }

    fun resolveConflicts(): Boolean {
        val newChain = nodes().map(::RemoteNode).fold(chain) { memo, node ->
            val nodeChain = node.chain()
            if (nodeChain.size > memo.size && valid(nodeChain)) nodeChain else memo
        }

        return if (newChain != chain) {
            chain = newChain
            true
        } else false
    }
}

internal fun Iterable<Transaction>.balanceFor(wallet: Wallet): Int =
    fold(0) { acc: Int, (sender, recipient, amount) ->
        when (wallet) {
            sender -> acc - amount
            recipient -> acc + amount
            else -> acc
        }
    }