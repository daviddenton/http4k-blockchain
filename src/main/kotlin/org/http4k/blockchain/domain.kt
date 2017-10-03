package org.http4k.blockchain

import java.security.MessageDigest.getInstance
import java.util.*
import javax.xml.bind.DatatypeConverter.printHexBinary

data class BlockHash(val value: String)

data class Transaction(val sender: Wallet, val recipient: Wallet, val amount: Int)

data class Block(
    val index: Int,
    val proof: Proof,
    val timestamp: Long,
    val transactions: Set<Transaction>,
    val previousHash: BlockHash
) {
    fun hash(): BlockHash = BlockHash(toString().digest())
}

data class Wallet(val id: UUID)

data class Proof(val value: Int) {
    fun next(): Proof = Proof(value + 1)

    fun validate(next: Proof) = "${this.value}${next.value}".digest().startsWith("00000")
}

private fun String.digest(): String = printHexBinary(getInstance("SHA-256").digest(toByteArray()))

internal fun proof(lastProof: Proof): Proof {
    var guess = Proof(0)
    while (!lastProof.validate(guess)) {
        guess = guess.next()
    }
    return guess
}
