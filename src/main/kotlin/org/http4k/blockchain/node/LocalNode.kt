package org.http4k.blockchain.node

import org.http4k.blockchain.Block
import org.http4k.blockchain.Transaction
import org.http4k.blockchain.TransactionState
import org.http4k.blockchain.TransactionState.Accepted
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

    private var blockchain = Blockchain.newChain(chainWallet)

    fun start() = registry.register(address).forEach { register(it) }

    fun stop() = apply { registry.deregister(address) }

    fun mineBlock(): Block {
        val nextProof = proof(blockchain.last().proof)
        newTransaction(Transaction(chainWallet, nodeWallet, 1))
        blockchain = blockchain.withNewBlock(nextProof)
        return blockchain.last()
    }

    override fun chain() = blockchain.chain.toList()

    override fun transactions() = blockchain.unconfirmed

    override fun newTransaction(newTransaction: Transaction): TransactionState =
        if (blockchain.balanceOf(newTransaction.sender) > newTransaction.amount) {
            blockchain += newTransaction
            Accepted
        } else Rejected

    private fun valid(newChain: List<Block>): Boolean {
        var lastBlock = newChain.first()
        var index = 1
        val block = blockchain.chain[1]
        while (index < blockchain.size()) {
            if (block.previousHash != lastBlock.hash()) return false
            if (!blockchain.last().proof.validate(block.proof)) return false
            lastBlock = blockchain.last()
            index += 1
        }
        return true
    }

    fun resolveConflicts(): Boolean {
        val newChain = nodes().map(::RemoteNode).fold(chain()) { memo, node ->
            val nodeChain = node.chain()
            if (nodeChain.size > memo.size && valid(nodeChain)) nodeChain else memo
        }

        return if (newChain != blockchain.chain) {
            blockchain = Blockchain(newChain)
            true
        } else false
    }
}
