package org.http4k.blockchain

import org.http4k.core.Body
import org.http4k.core.Uri
import org.http4k.format.Jackson.auto

object Protocol {
    val chain = Body.auto<List<Block>>().toLens()
    val block = Body.auto<Block>().toLens()
    val nodeList = Body.auto<List<Uri>>().toLens()
    val transaction = Body.auto<Transaction>().toLens()

}