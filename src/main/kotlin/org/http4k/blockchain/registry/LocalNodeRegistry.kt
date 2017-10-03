package org.http4k.blockchain.registry

import org.http4k.blockchain.node.Node
import org.http4k.blockchain.node.RemoteNode
import org.http4k.core.Uri

class LocalNodeRegistry : NodeRegistry {
    override fun deregister(vararg addresses: Uri): List<Uri> {
        addresses.map(::RemoteNode).forEach { nodes.remove(it) }
        return nodes.map(Node::address)
    }

    private val nodes = mutableSetOf<Node>()

    override fun register(vararg addresses: Uri): List<Uri> {
        addresses.map(::RemoteNode).forEach { nodes.add(it) }
        return nodes.map(Node::address)
    }

    override fun nodes() = nodes.map(Node::address)
}