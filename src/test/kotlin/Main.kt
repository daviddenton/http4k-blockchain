import org.http4k.blockchain.Transaction
import org.http4k.blockchain.Wallet
import org.http4k.blockchain.node.BlockchainNodeServer
import org.http4k.blockchain.node.RemoteNode
import org.http4k.blockchain.registry.NodeRegistryServer
import org.http4k.blockchain.registry.RemoteNodeRegistry
import org.http4k.core.Uri
import java.util.*

fun main(args: Array<String>) {
    val CHAIN_WALLET = Wallet(UUID.fromString("3070562a-4c95-47d0-9994-a3aaaf44b8b8"))

    val registry = NodeRegistryServer(8000).start()
    val node1 = BlockchainNodeServer(9000, 8000, CHAIN_WALLET).start()
    val node2 = BlockchainNodeServer(10000, 8000, CHAIN_WALLET).start()

    val registryClient = RemoteNodeRegistry(Uri.of("http://localhost:8000"))
    val node1Client = RemoteNode(Uri.of("http://localhost:9000"))
    val node2Client = RemoteNode(Uri.of("http://localhost:10000"))

    println(registryClient.nodes())
    println(node1Client.chain())
    println(node1Client.mine())
    println(node1Client.chain())
    println(node2Client.newTransaction(Transaction(Wallet(UUID.randomUUID()), Wallet(UUID.randomUUID()), 432)))
    println(node1Client.mine())
    println(node2Client.chain())

    node1.stop()
    node2.stop()
    registry.stop()
}
