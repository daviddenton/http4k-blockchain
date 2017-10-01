package org.http4k.blockchain

import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.format.Jackson.auto

class RemoteBlockchainNode(val address: Uri) : BlockchainNode {
    private val client: HttpHandler = { Response(Status.OK) }

    private val chain = Body.auto<List<Block>>().toLens()

    override fun chain(): List<Block> =
        chain.extract(client(Request(Method.GET, address.path("/chain"))))
}