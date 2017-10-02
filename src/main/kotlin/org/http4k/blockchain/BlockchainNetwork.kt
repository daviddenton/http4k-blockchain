package org.http4k.blockchain

import org.http4k.core.Uri

interface BlockchainNetwork {
    fun register(vararg addresses: Uri): Iterable<Uri>
    fun nodes(): Iterable<Uri>
}