package org.http4k.blockchain

import org.http4k.core.Body
import org.http4k.core.Uri
import org.http4k.format.Jackson.auto

data class TransactionCreated(val index: Int)
data class Resolution(val message: String, val chain: List<Block>)

object Protocol {
    val chain = Body.auto<List<Block>>().toLens()
    val block = Body.auto<Block>().toLens()
    val nodeList = Body.auto<List<Uri>>().toLens()
    val transaction = Body.auto<Transaction>().toLens()
    val transactionCreated = Body.auto<TransactionCreated>().toLens()

}