package org.http4k.blockchain

import org.http4k.core.Body
import org.http4k.core.Uri
import org.http4k.format.Jackson.auto

object Protocol {
    val chain = Body.auto<List<Block>>().toLens()
    val block = Body.auto<Block>().toLens()
    val nodeList = Body.auto<List<String>>().map({ it.map(Uri.Companion::of) }, { it.map(Uri::toString) }).toLens()
    val transaction = Body.auto<Transaction>().toLens()
    val transactions = Body.auto<Set<Transaction>>().toLens()

}