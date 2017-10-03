package org.http4k.blockchain.registry

import org.http4k.core.Uri

interface NodeRegistry {
    fun deregister(vararg addresses: Uri): List<Uri>
    fun register(vararg addresses: Uri): List<Uri>
    fun nodes(): List<Uri>
}