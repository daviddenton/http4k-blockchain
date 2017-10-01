package org.http4k.blockchain

import org.http4k.client.ApacheClient
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.format.Jackson.auto

class RemoteBlockchainNode(val address: Uri) : BlockchainNode {
    private val client = ApacheClient()

    private val chain = Body.auto<List<Block>>().toLens()

    override fun chain(): List<Block> =
        chain.extract(client(Request(GET, address.path("/chain"))))
}