package client

import core.reordered
import core.utils.parseCallChain

fun main() {
    val str = readLine()
    if (str != null) {
        val callChain = parseCallChain(str)
        println(callChain.reordered())
    }
}