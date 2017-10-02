package org.http4k.blockchain

import org.http4k.blockchain.Protocol.block
import org.http4k.blockchain.Protocol.chain
import org.http4k.blockchain.Protocol.nodeList
import org.http4k.blockchain.Protocol.transaction
import org.http4k.blockchain.Protocol.transactionCreated
import org.http4k.client.ApacheClient
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.with

class RemoteBlockchainNode(override val address: Uri) : BlockchainNode, BlockchainNetwork {
    private val client = ApacheClient()

    override fun chain() = chain.extract(client(Request(GET, address.path("/chain"))))

    fun mine(): Block = block.extract(client(Request(GET, address.path("/mine"))))

    override fun newTransaction(newTransaction: Transaction) = transactionCreated.extract(client(Request(POST, address.path("/transactions"))
        .with(transaction of newTransaction)))

    override fun register(vararg addresses: Uri) = nodeList.extract(client(Request(POST, this.address.path("/nodes"))
        .with(nodeList of addresses.toList())
    ))

    override fun nodes() = nodeList.extract(client(Request(GET, address.path("/nodes"))))

    fun resolve() = chain.extract(client(Request(GET, address.path("/nodes/resolve"))))
}