package org.http4k.blockchain

class Blockchain {
    private var transactions = listOf<Transaction>()
    private var chain = listOf<Block>()

    init {
        newBlock(Proof(100), BlockHash("1"))
    }

    fun chain() = chain.toList()

    fun newBlock(proof: Proof, previousHash: BlockHash? = null): Block {
        val newBlock = Block(chain.size + 1, proof, System.currentTimeMillis(), transactions, previousHash ?: lastBlock().hash())
        transactions = mutableListOf()
        chain = chain.plus(newBlock)
        return newBlock
    }

    fun newTransaction(transaction: Transaction): Int {
        transactions = transactions.plus(transaction)
        return lastBlock().index + 1
    }

    fun lastBlock(): Block = chain.last()
}