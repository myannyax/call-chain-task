package core.model

import core.utils.Polynomial
import core.utils.toExpression

interface Expression {
    fun simplify(): Expression
}

interface Bool : Expression

interface Num : Expression {
    val asPolynomial: Polynomial
}

val TRUE = Eq(ConstantExpression("1"), ConstantExpression("1"))

val FALSE = Eq(ConstantExpression("1"), ConstantExpression("0"))

data class ConstantExpression(val number: String) : Num {
    override fun toString(): String {
        return number
    }

    override val asPolynomial = Polynomial(number.toBigInteger(), 0)

    override fun simplify(): Num {
        return asPolynomial.toExpression()
    }
}

object Element : Num {
    override fun toString(): String {
        return "element"
    }

    override val asPolynomial = Polynomial(1.toBigInteger(), 1)

    override fun simplify(): Num {
        return asPolynomial.toExpression()
    }
}

open class BinaryOperator(open val l: Expression, open val r: Expression, private val name: String) : Expression {
    override fun toString(): String {
        return "($l$name$r)"
    }

    override fun simplify(): Expression {
        return this
    }
}

data class Plus(override val l: Num, override val r: Num) : BinaryOperator(l, r, "+"), Num {
    override fun toString(): String {
        return super.toString()
    }

    override val asPolynomial = run {
        val lPoly = l.asPolynomial
        val rPoly = r.asPolynomial
        lPoly + rPoly
    }

    override fun simplify(): Num {
        return asPolynomial.toExpression()
    }
}

data class Minus(override val l: Num, override val r: Num) : BinaryOperator(l, r, "-"), Num {
    override fun toString(): String {
        return super.toString()
    }

    override val asPolynomial = run {
        val lPoly = l.asPolynomial
        val rPoly = r.asPolynomial
        lPoly - rPoly
    }

    override fun simplify(): Num {
        return asPolynomial.toExpression()
    }
}

data class Mult(override val l: Num, override val r: Num) : BinaryOperator(l, r, "*"), Num {
    override fun toString(): String {
        return super.toString()
    }

    override val asPolynomial = run {
        val lPoly = l.asPolynomial
        val rPoly = r.asPolynomial
        lPoly * rPoly
    }

    override fun simplify(): Num {
        return asPolynomial.toExpression()
    }
}

data class Gt(override val l: Num, override val r: Num) : BinaryOperator(l, r, ">"), Bool {
    override fun toString(): String {
        return super.toString()
    }

    override fun simplify(): Bool {
        val left = Minus(l, r).asPolynomial
        if (left.deg == -1) return FALSE
        if (left.deg == 0) return if (left.coeffs[0] > 0.toBigInteger()) TRUE else FALSE
        return Gt(left.toExpression(), ConstantExpression("0"))
    }
}

data class Lt(override val l: Num, override val r: Num) : BinaryOperator(l, r, "<"), Bool {
    override fun toString(): String {
        return super.toString()
    }

    override fun simplify(): Bool {
        return Gt(r, l).simplify()
    }
}

data class Eq(override val l: Num, override val r: Num) : BinaryOperator(l, r, "="), Bool {
    override fun toString(): String {
        return super.toString()
    }

    override fun simplify(): Bool {
        val left = Minus(r, l).asPolynomial
        return if (left.deg == -1) TRUE else this
    }
}

data class And(override val l: Bool, override val r: Bool) : BinaryOperator(l, r, "&"), Bool {
    override fun toString(): String {
        return super.toString()
    }

    override fun simplify(): Bool {
        val sL = l.simplify() as Bool
        val sR = r.simplify() as Bool
        return if (sL == FALSE || sR == FALSE) FALSE
        else if (sL == TRUE) sR
        else if (sR == TRUE) sL
        else if (sL == sR) sR
        else if (sL is Gt && sR is Gt) {
            val left = Plus(sL.r, sR.r).asPolynomial
            return if (left.deg == -1) FALSE // x < 0 & -x < 0 == FALSE
            else And(sL, sR)
        } else And(sL, sR)
    }
}

data class Or(override val l: Bool, override val r: Bool) : BinaryOperator(l, r, "|"), Bool {
    override fun toString(): String {
        return super.toString()
    }

    override fun simplify(): Bool {
        val sL = l.simplify() as Bool
        val sR = r.simplify() as Bool
        return if (sL == FALSE) sR
        else if (sR == FALSE) sL
        else if (sL == TRUE || sR == TRUE) TRUE
        else if (sL == sR) sR
        else if (sL is Gt && sR is Gt) {
            val left = Plus(sL.r, sR.r).asPolynomial
            return if (left.deg == -1) TRUE // x < 0 | -x < 0 == TRUE ++ x can't be 0 since sL and sR are simplified
            else Or(sL, sR)
        } else Or(sL, sR)
    }
}

