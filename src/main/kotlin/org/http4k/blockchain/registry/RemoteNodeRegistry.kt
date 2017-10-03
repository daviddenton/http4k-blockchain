package org.http4k.blockchain.registry

import org.http4k.blockchain.Protocol.nodeList
import org.http4k.blockchain.Stack
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.with

class RemoteNodeRegistry(address: Uri) : NodeRegistry {
    private val client = Stack.client(address)

    override fun register(vararg addresses: Uri) = nodeList.extract(client(Request(POST, "/nodes")
        .with(nodeList of addresses.toList())
    ))

    override fun deregister(vararg addresses: Uri): List<Uri> = nodeList.extract(client(Request(DELETE, "/nodes")
        .with(nodeList of addresses.toList())
    ))

    override fun nodes() = nodeList.extract(client(Request(GET, "/nodes")))
}