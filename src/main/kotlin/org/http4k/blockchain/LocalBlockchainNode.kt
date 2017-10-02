package org.http4k.blockchain

import org.http4k.core.Uri

class LocalBlockchainNode(override val address: Uri) : BlockchainNode, BlockchainNetwork {

    private var transactions = listOf<Transaction>()
    private var chain = listOf<Block>()
    val nodes = mutableSetOf<BlockchainNode>()

    init {
        newBlock(Proof(100), BlockHash("1"))
    }

    fun registerNode(node: RemoteBlockchainNode) = nodes.add(node)

    override fun register(vararg addresses: Uri): Iterable<Uri> {
        addresses.map(::RemoteBlockchainNode).forEach { nodes.add(it) }
        return nodes.map(BlockchainNode::address)
    }

    override fun nodes() = nodes.map(BlockchainNode::address)

    override fun chain() = chain.toList()

    fun newBlock(proof: Proof, previousHash: BlockHash? = null): Block {
        val newBlock = Block(chain.size + 1, proof, System.currentTimeMillis(), transactions, previousHash ?: lastBlock().hash())
        transactions = mutableListOf()
        chain = chain.plus(newBlock)
        return newBlock
    }

    override fun newTransaction(newTransaction: Transaction): TransactionCreated {
        transactions = transactions.plus(newTransaction)
        return TransactionCreated(lastBlock().index + 1)
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
        val newChain = nodes.fold(chain) { memo, node ->
            val nodeChain = node.chain()
            if (nodeChain.size > memo.size && valid(nodeChain)) nodeChain else memo
        }

        return if (newChain != chain) {
            chain = newChain
            true
        } else false
    }
}