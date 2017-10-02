
import org.http4k.blockchain.BlockchainNodeServer
import org.http4k.blockchain.RemoteBlockchainNode
import org.http4k.blockchain.Transaction
import org.http4k.blockchain.Wallet
import org.http4k.core.Uri
import java.util.*

fun main(args: Array<String>) {
    val node1 = BlockchainNodeServer(8000).start()
    val node2 = BlockchainNodeServer(8001).start()

    val node1Client = RemoteBlockchainNode(Uri.of("http://localhost:8000"))
    val node2Client = RemoteBlockchainNode(Uri.of("http://localhost:8001"))

    println(node1Client.chain())
    println(node1Client.mine())
    println(node1Client.chain())
    println(node2Client.newTransaction(Transaction(Wallet(UUID.randomUUID()), Wallet(UUID.randomUUID()), 432)))
    println(node1Client.mine())
    println(node2Client.chain())

    node1.stop()
    node2.stop()
}
