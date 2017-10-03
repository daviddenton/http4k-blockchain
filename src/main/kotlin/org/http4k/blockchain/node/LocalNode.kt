package org.http4k.blockchain.node

import org.http4k.blockchain.Block
import org.http4k.blockchain.BlockHash
import org.http4k.blockchain.Proof
import org.http4k.blockchain.Transaction
import org.http4k.blockchain.Wallet
import org.http4k.blockchain.proof
import org.http4k.blockchain.registry.LocalNodeRegistry
import org.http4k.blockchain.registry.NodeRegistry
import org.http4k.core.Uri
import java.util.*

class LocalNode(override val address: Uri,
                private val registry: NodeRegistry,
                private val chainWallet: Wallet
) : Node, NodeRegistry by LocalNodeRegistry() {

    val wallet = Wallet(UUID.randomUUID())

    private var transactions = listOf<Transaction>()
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
        newTransaction(Transaction(chainWallet, wallet, 1))
        return newBlock(nextProof)
    }

    override fun chain() = chain.toList()

    fun newBlock(proof: Proof, previousHash: BlockHash? = null): Block {
        val newBlock = Block(chain.size + 1, proof, System.currentTimeMillis(), transactions, previousHash ?: lastBlock().hash())
        transactions = mutableListOf()
        chain = chain.plus(newBlock)
        return newBlock
    }

    override fun newTransaction(newTransaction: Transaction) {
        transactions = transactions.plus(newTransaction)
    }

    fun lastBlock(): Block = chain.last()

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