import io.github.konfigur8.Configuration
import io.github.konfigur8.ConfigurationTemplate
import org.http4k.blockchain.Settings
import org.http4k.blockchain.Transaction
import org.http4k.blockchain.Wallet
import org.http4k.blockchain.node.BlockchainNodeServer
import org.http4k.blockchain.node.RemoteNode
import org.http4k.blockchain.registry.NodeRegistryServer
import org.http4k.blockchain.registry.RemoteNodeRegistry
import org.http4k.core.Uri
import java.util.*

fun main(args: Array<String>) {
    val registry = NodeRegistryServer(Settings.defaults.reify()).start()
    val node1 = BlockchainNodeServer(Settings.defaults.withPort(9000)).start()
    val node2 = BlockchainNodeServer(Settings.defaults.withPort(10000)).start()

    val registryClient = RemoteNodeRegistry(Uri.of("http://localhost:8000"))
    val node1Client = RemoteNode(Uri.of("http://localhost:9000"))
    val node2Client = RemoteNode(Uri.of("http://localhost:10000"))

    println(registryClient.nodes())
//    println(node1Client.chain())
//    println(node1Client.mine())
//    println(node1Client.chain())
    println(node2Client.newTransaction(Transaction(Wallet(UUID.randomUUID()), Wallet(UUID.randomUUID()), 432)))
    println(node2Client.transactions())
//    println(node1Client.mine())
//    println(node2Client.chain())

    node1.stop()
    node2.stop()
    registry.stop()
}

private fun ConfigurationTemplate.withPort(port: Int): Configuration = this.withProp(Settings.NODE_PORT, port).reify()

