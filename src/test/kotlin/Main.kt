import org.http4k.blockchain.BlockchainApp
import org.http4k.blockchain.Transaction
import org.http4k.blockchain.Wallet
import org.http4k.client.ApacheClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.with
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import java.util.*

fun main(args: Array<String>) {
    val server = BlockchainApp(Wallet(UUID.randomUUID())).asServer(SunHttp(8000)).start()

    val client = ApacheClient()

    fun getChain() = client(Request(Method.GET, "http://localhost:8000/chain"))
    fun mine() = client(Request(Method.GET, "http://localhost:8000/mine"))
    fun transaction() = client(Request(Method.POST, "http://localhost:8000/transactions/new").with(BlockchainApp.Contract.transaction of Transaction(Wallet(UUID.randomUUID()), Wallet(UUID.randomUUID()), 123)))

    println(transaction())
    println(mine())
    println(transaction())
    println(getChain())

    server.stop()
}
