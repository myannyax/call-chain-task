package core.model

interface Expression

interface Bool : Expression

interface Num : Expression

val TRUE = Eq(ConstantExpression("1"), ConstantExpression("1"))

val FALSE = Eq(ConstantExpression("1"), ConstantExpression("0"))

data class ConstantExpression(val number: String) : Num {
    override fun toString(): String {
        return number
    }
}

object Element : Num {
    override fun toString(): String {
        return "element"
    }
}

sealed class BinaryOperator(val l: Expression, val r: Expression) : Expression

class Plus(l: Num, r: Num) : BinaryOperator(l, r), Num {
    override fun toString(): String {
        return "($l+$r)"
    }
}

class Minus(l: Num, r: Num) : BinaryOperator(l, r), Num {
    override fun toString(): String {
        return "($l-$r)"
    }
}

class Mult(l: Num, r: Num) : BinaryOperator(l, r), Num {
    override fun toString(): String {
        return "($l*$r)"
    }
}

class Gt(l: Num, r: Num) : BinaryOperator(l, r), Bool {
    override fun toString(): String {
        return "($l>$r)"
    }
}

class Lt(l: Num, r: Num) : BinaryOperator(l, r), Bool {
    override fun toString(): String {
        return "($l<$r)"
    }
}

class Eq(l: Num, r: Num) : BinaryOperator(l, r), Bool {
    override fun toString(): String {
        return "($l=$r)"
    }
}

class And(l: Bool, r: Bool) : BinaryOperator(l, r), Bool {
    override fun toString(): String {
        return "($l&$r)"
    }
}

class Or(l: Bool, r: Bool) : BinaryOperator(l, r), Bool {
    override fun toString(): String {
        return "($l|$r)"
    }
}

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