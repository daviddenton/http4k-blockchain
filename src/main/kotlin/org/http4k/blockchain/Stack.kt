package org.http4k.blockchain

import org.http4k.client.ApacheClient
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetHostFrom
import org.http4k.filter.ResponseFilters
import org.http4k.filter.ServerFilters


object Stack {
    private fun Audit(port: Int) = ResponseFilters.ReportLatency { request, response, duration ->
        println("':$port' '${request.method}' '${request.uri}' '${response.status}' '${duration.toMillis()}ms'")
    }

    fun server(port: Int, app: HttpHandler) =
        Audit(port)
            .then(ServerFilters.CatchAll())
            .then(ServerFilters.CatchLensFailure)
            .then(app)

    fun client(address: Uri) = SetHostFrom(address)
        .then(Audit(address.port!!))
        .then(ApacheClient())
}