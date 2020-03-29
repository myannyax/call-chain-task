package client

import core.reordered
import core.utils.parseCallChain

fun main() {
    val str = readLine()
    if (str != null) {
        try {
            val callChain = parseCallChain(str)
            println(callChain.reordered())
        } catch (e: Exception) {
            println(e.message)
        }
    }
}