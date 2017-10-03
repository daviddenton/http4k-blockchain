package org.http4k.blockchain.node

import org.http4k.blockchain.registry.NodeRegistryApi
import org.http4k.core.HttpHandler
import org.http4k.routing.routes

object BlockchainNodeServerApi {
    operator fun invoke(node: LocalNode): HttpHandler = routes(
        NodeApi(node),
        NodeRegistryApi(node)
    )
}
