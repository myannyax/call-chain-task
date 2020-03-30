package core.utils

import core.CallChain
import core.ParseException
import core.TypeException
import core.model.*

fun parseCallChain(callChain: String): CallChain {
    val lst = callChain.split("%>%")
    return CallChain(lst.map { parseCall(it) })
}

fun parseCall(call: String): Call {
    return when {
        call.startsWith("map{") && call.endsWith("}") -> {
            MapCall(parseExpression(call.substringAfter("map{").dropLast(1)))
        }
        call.startsWith("filter{") && call.endsWith("}") -> {
            val condition = parseExpression(call.substringAfter("filter{").dropLast(1))
            if (condition !is Bool) throw TypeException()
            FilterCall(condition)
        }
        else -> {
            throw ParseException()
        }
    }
}

fun parseExpression(expr: String): Expression {
    if (expr == "element") return Element
    return try {
        parseConstantExpression(expr)
    } catch (e: ParseException) {
        parseBinaryOperation(expr)
    }
}

fun parseConstantExpression(cExpr: String): ConstantExpression {
    val regex = """-?\d+""".toRegex()
    return if (regex.matches(cExpr)) ConstantExpression(cExpr)
    else throw ParseException()
}

fun parseBinaryOperation(binExpr: String): BinaryOperator {
    val ops = """[-<>=&|*+]"""
    val constExpr = """-?\d+""".toRegex()

    val leftElem = """\(element($ops)(.*)\)""".toRegex()
    var matchResult = leftElem.matchEntire(binExpr)
    if (matchResult != null) {
        val (op, r) = matchResult.destructured
        val rExpr = parseExpression(r)
        return safeCreate(op, Element, rExpr)
    }
    val rightElem = """\((.*)($ops)element\)""".toRegex()
    matchResult = rightElem.matchEntire(binExpr)
    if (matchResult != null) {
        val (l, op) = matchResult.destructured
        val lExpr = parseExpression(l)
        return safeCreate(op, lExpr, Element)
    }

    val twoOperations = """\((\(.*\))($ops)(\(.*\))\)""".toRegex()
    matchResult = twoOperations.matchEntire(binExpr)
    if (matchResult != null) {
        val (l, op, r) = matchResult.destructured
        val lExpr = parseExpression(l)
        val rExpr = parseExpression(r)
        return safeCreate(op, lExpr, rExpr)
    }

    val rightConstant = """\((\(.*\))($ops)(${constExpr})\)""".toRegex()
    matchResult = rightConstant.matchEntire(binExpr)
    if (matchResult != null) {
        val (l, op, r) = matchResult.destructured
        val lExpr = parseExpression(l)
        return safeCreate(op, lExpr, ConstantExpression(r))
    }

    val leftConstant = """\((${constExpr})($ops)(\(.*\))\)""".toRegex()
    matchResult = leftConstant.matchEntire(binExpr)
    if (matchResult != null) {
        val (l, op, r) = matchResult.destructured
        val rExpr = parseExpression(r)
        return safeCreate(op, ConstantExpression(l), rExpr)
    }

    val twoConstants = """\((${constExpr})($ops)(${constExpr})\)""".toRegex()
    matchResult = twoConstants.matchEntire(binExpr)
    return if (matchResult != null) {
        val (l, op, r) = matchResult.destructured
        val lExpr = parseExpression(l)
        val rExpr = parseExpression(r)
        safeCreate(op, lExpr, rExpr)
    } else throw ParseException()
}