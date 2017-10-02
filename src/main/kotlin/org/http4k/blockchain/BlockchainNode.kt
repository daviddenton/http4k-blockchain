package org.http4k.blockchain

import org.http4k.core.Uri

interface BlockchainNode {
    val address: Uri
    fun newTransaction(newTransaction: Transaction): TransactionCreated
    fun chain(): List<Block>
}