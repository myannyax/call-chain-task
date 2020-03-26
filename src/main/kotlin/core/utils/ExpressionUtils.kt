package core.utils

import core.model.*

fun safeCreate(op: String, l: Expression, r: Expression): BinaryOperator {
    return when (op) {
        "+" -> {
            if (l is Num && r is Num) {
                Plus(l, r)
            } else TODO("TypeError")
        }
        "-" -> {
            if (l is Num && r is Num) {
                Minus(l, r)
            } else TODO("TypeError")
        }
        "*" -> {
            if (l is Num && r is Num) {
                Mult(l, r)
            } else TODO("TypeError")
        }
        ">" -> {
            if (l is Num && r is Num) {
                Gt(l, r)
            } else TODO("TypeError")
        }
        "<" -> {
            if (l is Num && r is Num) {
                Lt(l, r)
            } else TODO("TypeError")
        }
        "=" -> {
            if (l is Num && r is Num) {
                Eq(l, r)
            } else TODO("TypeError")
        }
        "&" -> {
            if (l is Bool && r is Bool) {
                And(l, r)
            } else TODO("TypeError")
        }
        "|" -> {
            if (l is Bool && r is Bool) {
                Or(l, r)
            } else TODO("TypeError")
        }
        else -> TODO("Error")
    }
}

fun Expression.compWith(expr: Expression): Expression {
    return when (this) {
        is Element -> expr
        is ConstantExpression -> this
        is BinaryOperator -> {
            val lc = l.compWith(expr)
            val rc = r.compWith(expr)
            when (this) {
                is Plus -> safeCreate("+", lc, rc)
                is Minus -> safeCreate("-", lc, rc)
                is Mult -> safeCreate("*", lc, rc)
                is Gt -> safeCreate(">", lc, rc)
                is Lt -> safeCreate("<", lc, rc)
                is Eq -> safeCreate("=", lc, rc)
                is And -> safeCreate("&", lc, rc)
                is Or -> safeCreate("|", lc, rc)
            }
        }
        else -> TODO("Error")
    }
}