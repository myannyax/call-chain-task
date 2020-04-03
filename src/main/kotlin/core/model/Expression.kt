package core.model

import core.utils.Polynomial
import core.utils.toExpression

interface Expression {
    fun simplify(): Expression
}

interface Bool : Expression

interface Num : Expression {
    fun toPolynomial(): Polynomial

    override fun simplify(): Num {
        return toPolynomial().toExpression()
    }
}

val TRUE = Eq(ConstantExpression("1"), ConstantExpression("1"))

val FALSE = Eq(ConstantExpression("1"), ConstantExpression("0"))

data class ConstantExpression(val number: String) : Num {
    override fun toString(): String {
        return number
    }

    override fun toPolynomial() = Polynomial(number.toBigInteger(), 0)
}

object Element : Num {
    override fun toString(): String {
        return "element"
    }

    override fun toPolynomial() = Polynomial(1.toBigInteger(), 1)
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

    override fun toPolynomial(): Polynomial {
        val lPoly = l.toPolynomial()
        val rPoly = r.toPolynomial()
        return lPoly + rPoly
    }

    override fun simplify(): Num {
        return super<Num>.simplify()
    }
}

data class Minus(override val l: Num, override val r: Num) : BinaryOperator(l, r, "-"), Num {
    override fun toString(): String {
        return super.toString()
    }

    override fun toPolynomial(): Polynomial {
        val lPoly = l.toPolynomial()
        val rPoly = r.toPolynomial()
        return lPoly - rPoly
    }

    override fun simplify(): Num {
        return super<Num>.simplify()
    }
}

data class Mult(override val l: Num, override val r: Num) : BinaryOperator(l, r, "*"), Num {
    override fun toString(): String {
        return super.toString()
    }

    override fun toPolynomial(): Polynomial {
        val lPoly = l.toPolynomial()
        val rPoly = r.toPolynomial()
        return lPoly * rPoly
    }

    override fun simplify(): Num {
        return super<Num>.simplify()
    }
}

data class Gt(override val l: Num, override val r: Num) : BinaryOperator(l, r, ">"), Bool {
    val leftPoly: Polynomial by lazy { l.toPolynomial() }

    override fun toString(): String {
        return super.toString()
    }

    override fun simplify(): Bool {
        val s = Gt(Minus(l, r).simplify(), ConstantExpression("0"))
        return when (s.leftPoly.deg) {
            -1 -> FALSE // 0 > 0 == FALSE
            0 -> return if (s.leftPoly.coeffs[0] > 0.toBigInteger()) TRUE else FALSE // const > 0
            else -> return s
        }
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
    val leftPoly: Polynomial by lazy { l.toPolynomial() }

    override fun toString(): String {
        return super.toString()
    }

    override fun simplify(): Bool {
        val s = Eq(Minus(l, r).simplify(), ConstantExpression("0"))
        return when (s.leftPoly.deg) {
            -1 -> TRUE // 0 = 0 == TRUE
            0 -> return if (s.leftPoly.coeffs[0] == 0.toBigInteger()) TRUE else FALSE // const = 0
            else -> return s
        }
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
            return when {
                sL.leftPoly.negEq(sR.leftPoly) -> FALSE // x > 0 & -x > 0 == FALSE
                sL.leftPoly.eq(sR.leftPoly) -> return sL // x > 0 & x > 0 == x < 0
                else -> And(sL, sR)
            }
        } else if (sL is Eq && sR is Eq) {
            return if (sL.leftPoly.negEq(sR.leftPoly) || sL.leftPoly.eq(sR.leftPoly)) sL // x = 0 & -x = 0 == x = 0         x = 0 & x = 0 == x = 0
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
            return when {
                sL.leftPoly.negEq(sR.leftPoly) -> TRUE // x > 0 | -x > 0 == TRUE ++ x can't be 0 since sL and sR are simplified
                sL.leftPoly.eq(sR.leftPoly) -> return sL // x > 0 | x > 0 == x > 0
                else -> Or(sL, sR)
            }
        } else if (sL is Eq && sR is Eq) {
            return if (sL.leftPoly.negEq(sR.leftPoly) || sL.leftPoly.eq(sR.leftPoly)) sL // x = 0 | -x = 0 == x = 0         x = 0 | x = 0 == x = 0
            else Or(sL, sR)
        } else Or(sL, sR)
    }
}

