package core.utils

import core.model.*

fun parseCallChain(callChain: String): CallChain {
    val lst = callChain.split("%>%")
    return CallChain(lst.map { parseCall(it) })
}

fun parseCall(call: String): Call {
    return when {
        call.startsWith("map{") && call.endsWith("}") -> {
            MapCall(parseExpression(call.substringAfter("map{").dropLast(1)) ?: TODO("Error"))
        }
        call.startsWith("filter{") && call.endsWith("}") -> {
            FilterCall(parseExpression(call.substringAfter("filter{").dropLast(1)) ?: TODO("Error"))
        }
        else -> {
            TODO("Error")
        }
    }
}

fun parseExpression(expr: String): Expression? {
    if (expr == "element") return Element
    return parseConstantExpression(expr) ?: parseBinaryExpression(expr)
}

fun parseConstantExpression(cExpr: String): ConstantExpression? {
    val regex = """-?\d+""".toRegex()
    return if (regex.matches(cExpr)) ConstantExpression(cExpr)
    else null
}

fun parseBinaryExpression(binExpr: String): BinaryExpression? {
    val regexCompl = """\(\((.*)\)([-<>=&|*+])\((.*)\)\)""".toRegex()
    val regex = """\((.*)([-<>=&|*+])(.*)\)""".toRegex()

    val matchResult = regexCompl.find(binExpr) ?: regex.find(binExpr)
    return if (matchResult != null) {
        val (l, op, r) = matchResult.destructured
        val lExpr = parseExpression(l) ?: return null
        val rExpr = parseExpression(r) ?: return null
        BinaryExpression(lExpr, op.toOp(), rExpr)
    } else null
}