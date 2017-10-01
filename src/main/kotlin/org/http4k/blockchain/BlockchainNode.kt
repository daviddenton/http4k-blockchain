package org.http4k.blockchain

interface BlockchainNode {
    fun chain(): List<Block>
}