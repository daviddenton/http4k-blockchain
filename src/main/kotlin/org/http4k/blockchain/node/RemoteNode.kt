package org.http4k.blockchain.node

import org.http4k.blockchain.Protocol
import org.http4k.blockchain.Protocol.block
import org.http4k.blockchain.Protocol.chain
import org.http4k.blockchain.Stack
import org.http4k.blockchain.Transaction
import org.http4k.core.Method
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.with

class RemoteNode(override val address: Uri) : Node {
    private val client = Stack.client(address)

    override fun chain() = chain.extract(client(Request(GET, "/chain")))

    fun mine() = block.extract(client(Request(GET, "/mine")))

    override fun newTransaction(newTransaction: Transaction) {
        client(Request(Method.POST, "/transactions")
            .with(Protocol.transaction of newTransaction))
    }
}