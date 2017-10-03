package org.http4k.blockchain.node

import org.http4k.blockchain.Wallet
import org.http4k.blockchain.registry.NodeRegistryApi
import org.http4k.core.HttpHandler
import org.http4k.routing.routes

object BlockchainNodeServerApi {
    operator fun invoke(node: LocalNode, nodeWallet: Wallet): HttpHandler = routes(
        NodeApi(node, nodeWallet),
        NodeRegistryApi(node)
    )
}
