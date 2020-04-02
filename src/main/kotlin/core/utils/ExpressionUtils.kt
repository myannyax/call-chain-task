package core.utils

import core.ASTException
import core.ParseException
import core.TypeException
import core.model.*

fun safeCreate(op: String, l: Expression, r: Expression): BinaryOperator {
    return when (op) {
        "+" -> {
            if (l is Num && r is Num) {
                Plus(l, r)
            } else throw TypeException()
        }
        "-" -> {
            if (l is Num && r is Num) {
                Minus(l, r)
            } else throw TypeException()
        }
        "*" -> {
            if (l is Num && r is Num) {
                Mult(l, r)
            } else throw TypeException()
        }
        ">" -> {
            if (l is Num && r is Num) {
                Gt(l, r)
            } else throw TypeException()
        }
        "<" -> {
            if (l is Num && r is Num) {
                Lt(l, r)
            } else throw TypeException()
        }
        "=" -> {
            if (l is Num && r is Num) {
                Eq(l, r)
            } else throw TypeException()
        }
        "&" -> {
            if (l is Bool && r is Bool) {
                And(l, r)
            } else throw TypeException()
        }
        "|" -> {
            if (l is Bool && r is Bool) {
                Or(l, r)
            } else throw TypeException()
        }
        else -> throw ParseException()
    }
}

fun Expression.composition(expr: Expression): Expression {
    return when (this) {
        is Element -> expr
        is ConstantExpression -> this
        is BinaryOperator -> {
            val lc = l.composition(expr)
            val rc = r.composition(expr)
            when (this) {
                is Plus -> safeCreate("+", lc, rc)
                is Minus -> safeCreate("-", lc, rc)
                is Mult -> safeCreate("*", lc, rc)
                is Gt -> safeCreate(">", lc, rc)
                is Lt -> safeCreate("<", lc, rc)
                is Eq -> safeCreate("=", lc, rc)
                is And -> safeCreate("&", lc, rc)
                is Or -> safeCreate("|", lc, rc)
                else -> throw ASTException()
            }
        }
        else -> throw ASTException()
    }
}

fun Polynomial.toExpression(): Num {
    if (deg == -1) return ConstantExpression("0")
    var result: Num? = if (coeffs[0] == 0.toBigInteger()) null else ConstantExpression(coeffs[0].toString())
    var elemDeg: Num = Element
    for (i in 1 until coeffs.size) {
        if (coeffs[i] != 0.toBigInteger()) {
            val expr =
                if (coeffs[i] == 1.toBigInteger()) elemDeg else Mult(elemDeg, ConstantExpression(coeffs[i].toString()))
            result = if (result == null) expr
            else Plus(result, expr)
        }
        elemDeg = Mult(Element, elemDeg)
    }
    return result ?: ConstantExpression("0")
}