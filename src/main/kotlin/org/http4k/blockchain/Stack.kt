package org.http4k.blockchain

import org.http4k.client.ApacheClient
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetHostFrom
import org.http4k.filter.ResponseFilters
import org.http4k.filter.ServerFilters


object Stack {
    private fun Audit(address: Uri) = ResponseFilters.ReportLatency { request, response, duration ->
        println("':${address.port}' '${request.method}' '${request.uri}' '${response.status}' '${duration.toMillis()}ms'")
    }

    fun server(address: Uri, app: HttpHandler) =
        Audit(address)
            .then(ServerFilters.CatchAll())
            .then(ServerFilters.CatchLensFailure)
            .then(app)

    fun client(address: Uri) = SetHostFrom(address)
        .then(Audit(address))
        .then(ApacheClient())
}