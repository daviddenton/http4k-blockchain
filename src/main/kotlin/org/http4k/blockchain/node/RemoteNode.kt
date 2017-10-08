package org.http4k.blockchain.node

import org.http4k.blockchain.Protocol
import org.http4k.blockchain.Protocol.block
import org.http4k.blockchain.Protocol.chain
import org.http4k.blockchain.Stack
import org.http4k.blockchain.Transaction
import org.http4k.blockchain.TransactionState
import org.http4k.blockchain.TransactionState.Accepted
import org.http4k.blockchain.TransactionState.Rejected
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.ACCEPTED
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Uri
import org.http4k.core.with

class RemoteNode(override val address: Uri) : Node {

    private val client = Stack.client(address)

    override fun chain() = chain(client(Request(GET, "/chain")))

    fun mine() = block(client(Request(GET, "/mine")))

    override fun newTransaction(newTransaction: Transaction): TransactionState =
        when (
        client(Request(POST, "/transactions")
            .with(Protocol.transaction of newTransaction)).status) {
            ACCEPTED -> Accepted
            CONFLICT -> Rejected
            else -> Rejected
        }

    override fun transactions(): Set<Transaction>
        = Protocol.transactions(client(Request(GET, "/transactions")))
}