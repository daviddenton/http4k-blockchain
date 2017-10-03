package org.http4k.blockchain

import io.github.konfigur8.ConfigurationTemplate
import io.github.konfigur8.Property
import java.util.*

object Settings {

    val NODE_PORT = Property.int("NODE_PORT")
    val REGISTRY_PORT = Property.int("REGISTRY_PORT")
    val CHAIN_WALLET = Property("CHAIN_WALLET", { Wallet(UUID.fromString(it)) }, { it.id.toString() })

    val defaults = ConfigurationTemplate()
        .withProp(REGISTRY_PORT, 8000)
        .withProp(CHAIN_WALLET, Wallet(UUID.randomUUID()))
        .withProp(NODE_PORT, -1)
}