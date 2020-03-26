package core.utils

import core.CallChain
import core.model.*

fun parseCallChain(callChain: String): CallChain {
    val lst = callChain.split("%>%")
    return CallChain(lst.map { parseCall(it) })
}

fun parseCall(call: String): Call {
    return when {
        call.startsWith("map{") && call.endsWith("}") -> {
            MapCall(parseExpression(call.substringAfter("map{").dropLast(1)) ?: TODO("ParseError"))
        }
        call.startsWith("filter{") && call.endsWith("}") -> {
            val condition = parseExpression(call.substringAfter("filter{").dropLast(1)) ?: TODO("ParseError")
            if (condition !is Bool) TODO("TypeError")
            FilterCall(condition)
        }
        else -> {
            TODO("ParseError")
        }
    }
}

fun parseExpression(expr: String): Expression? {
    if (expr == "element") return Element
    return parseConstantExpression(expr) ?: parseBinaryOperation(expr)
}

fun parseConstantExpression(cExpr: String): ConstantExpression? {
    val regex = """-?\d+""".toRegex()
    return if (regex.matches(cExpr)) ConstantExpression(cExpr)
    else null
}

fun parseBinaryOperation(binExpr: String): BinaryOperator? {
    val ops = "-<>=&|*+"
    val constExpr = { sg: String -> "$sg[0-9]*" }

    val leftElem = """\(element([$ops])(.*)\)""".toRegex()
    var matchResult = leftElem.matchEntire(binExpr)
    if (matchResult != null) {
        val (op, r) = matchResult.destructured
        val rExpr = parseExpression(r) ?: return null
        return safeCreate(op, Element, rExpr)
    }
    val rightElem = """\((.*)([$ops])element\)""".toRegex()
    matchResult = rightElem.matchEntire(binExpr)
    if (matchResult != null) {
        val (l, op) = matchResult.destructured
        val lExpr = parseExpression(l) ?: return null
        return safeCreate(op, lExpr, Element)
    }

    val twoOperations = """\((\(.*\))([$ops])(\(.*\))\)""".toRegex()
    matchResult = twoOperations.matchEntire(binExpr)
    if (matchResult != null) {
        val (l, op, r) = matchResult.destructured
        val lExpr = parseExpression(l) ?: return null
        val rExpr = parseExpression(r) ?: return null
        return safeCreate(op, lExpr, rExpr)
    }

    val rightConstant = { sg: String -> """\((\(.*\))([$ops])(${constExpr(sg)})\)""".toRegex() }
    matchResult = rightConstant("-").matchEntire(binExpr) ?: rightConstant("").matchEntire(binExpr)
    if (matchResult != null) {
        val (l, op, r) = matchResult.destructured
        val lExpr = parseExpression(l) ?: return null
        return safeCreate(op, lExpr, ConstantExpression(r))
    }

    val leftConstant = { sg: String -> """\((${constExpr(sg)})([$ops])(\(.*\))\)""".toRegex() }
    matchResult = leftConstant("-").matchEntire(binExpr) ?: leftConstant("").matchEntire(binExpr)
    if (matchResult != null) {
        val (l, op, r) = matchResult.destructured
        val rExpr = parseExpression(r) ?: return null
        return safeCreate(op, ConstantExpression(l), rExpr)
    }

    val twoConstants =
        { sgL: String, sgR: String -> """\((${constExpr(sgL)})([$ops])(${constExpr(sgR)})\)""".toRegex() }
    matchResult =
        twoConstants("-", "-").matchEntire(binExpr) ?: twoConstants("", "-").matchEntire(binExpr) ?:
                twoConstants("-", "").matchEntire(binExpr) ?: twoConstants("", "").matchEntire(binExpr)
    return if (matchResult != null) {
        val (l, op, r) = matchResult.destructured
        val lExpr = parseExpression(l) ?: return null
        val rExpr = parseExpression(r) ?: return null
        safeCreate(op, lExpr, rExpr)
    } else null
}