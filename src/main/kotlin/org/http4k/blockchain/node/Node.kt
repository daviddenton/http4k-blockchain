package org.http4k.blockchain.node

import org.http4k.blockchain.Block
import org.http4k.blockchain.Transaction
import org.http4k.blockchain.TransactionState
import org.http4k.core.Uri

interface Node {
    val address: Uri
    fun newTransaction(newTransaction: Transaction): TransactionState
    fun transactions(): Set<Transaction>
    fun chain(): List<Block>
}