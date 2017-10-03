package org.http4k.blockchain.node

import org.http4k.blockchain.Protocol
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

fun NodeApi(node: LocalNode): RoutingHttpHandler = routes(
    "/chain" bind Method.GET to {
        Response(Status.OK).with(Protocol.chain of node.chain())
    },
    "/mine" bind Method.GET to {
        Response(Status.CREATED).with(Protocol.block of node.mineBlock())
    },
    "/resolve" bind Method.GET to {
        val status = if (node.resolveConflicts()) Status.CREATED else Status.NOT_MODIFIED
        Response(status).with(Protocol.chain of node.chain())
    },
    "/transactions" bind routes(
        Method.POST to { _: Request -> Response(Status.CREATED) },
        Method.GET to { _: Request -> Response(Status.OK).with(Protocol.nodeList of node.nodes().toList()) })
)