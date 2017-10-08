package org.http4k.blockchain.node

import org.http4k.blockchain.Protocol
import org.http4k.blockchain.TransactionState.Accepted
import org.http4k.blockchain.TransactionState.Rejected
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.ACCEPTED
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.NOT_MODIFIED
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

fun NodeApi(node: LocalNode): RoutingHttpHandler = routes(
    "/chain" bind GET to {
        Response(Status.OK).with(Protocol.chain of node.chain())
    },
    "/mine" bind GET to {
        Response(CREATED).with(Protocol.block of node.mineBlock())
    },
    "/resolve" bind GET to {
        val status = if (node.resolveConflicts()) CREATED else NOT_MODIFIED
        Response(status).with(Protocol.chain of node.chain())
    },
    "/transactions" bind routes(
        POST to { req: Request ->
            when (node.newTransaction(Protocol.transaction(req))) {
                Accepted -> Response(ACCEPTED)
                Rejected -> Response(CONFLICT)
            }
        },
        GET to { _: Request -> Response(OK).with(Protocol.transactions of node.transactions()) })
)