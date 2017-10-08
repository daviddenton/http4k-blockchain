package org.http4k.blockchain.registry

import org.http4k.blockchain.Protocol.nodeList
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.ACCEPTED
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.routing.bind
import org.http4k.routing.routes

fun NodeRegistryApi(nodeRegistry: NodeRegistry) = routes(
    "/nodes" bind routes(
        GET to { _: Request ->
            Response(OK).with(nodeList of nodeRegistry.nodes())
        },
        POST to { req: Request ->
            nodeList(req).map { nodeRegistry.register(it) }
            Response(ACCEPTED).with(nodeList of nodeRegistry.nodes())
        },
        DELETE to { req: Request ->
            nodeList(req).map { nodeRegistry.deregister(it) }
            Response(ACCEPTED).with(nodeList of nodeRegistry.nodes())
        }
    )
)