package org.http4k.blockchain.registry

import org.http4k.blockchain.Protocol.nodeList
import org.http4k.client.ApacheClient
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ClientFilters.SetHostFrom

class RemoteNodeRegistry(address: Uri) : NodeRegistry {
    private val client = SetHostFrom(address).then(ApacheClient())

    override fun register(vararg addresses: Uri) = nodeList.extract(client(Request(POST, "/nodes")
        .with(nodeList of addresses.toList())
    ))

    override fun deregister(vararg addresses: Uri): List<Uri> = nodeList.extract(client(Request(DELETE, "/nodes")
        .with(nodeList of addresses.toList())
    ))

    override fun nodes() = nodeList.extract(client(Request(GET, "/nodes")))
}