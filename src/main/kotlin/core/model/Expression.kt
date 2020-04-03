package core.model

import core.utils.Polynomial

interface Expression

interface Bool : Expression {
    fun simplify(): Expression
}

interface Num : Expression {
    fun toPolynomial(): Polynomial
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
}

data class Gt(override val l: Num, override val r: Num) : BinaryOperator(l, r, ">"), Bool {
    override fun toString(): String {
        return super.toString()
    }

    override fun simplify(): Bool {
        return GtPoly(l, r).simplify()
    }
}

data class GtPoly(val poly: Polynomial) : Bool {
    constructor(l: Num, r: Num) : this(Minus(l, r).toPolynomial())

    override fun simplify(): Bool {
        return when (poly.deg) {
            -1 -> FALSE // 0 > 0 == FALSE
            0 -> return if (poly.coeffs[0] > 0.toBigInteger()) TRUE else FALSE // const > 0
            else -> return this
        }
    }

    override fun toString(): String {
        return "($poly>0)"
    }
}

data class Lt(override val l: Num, override val r: Num) : BinaryOperator(l, r, "<"), Bool {
    override fun toString(): String {
        return super.toString()
    }

    override fun simplify(): Bool {
        return GtPoly(r, l).simplify()
    }
}

data class Eq(override val l: Num, override val r: Num) : BinaryOperator(l, r, "="), Bool {
    override fun toString(): String {
        return super.toString()
    }

    override fun simplify(): Bool {
        return EqPoly(l, r).simplify()
    }
}

data class EqPoly(val poly: Polynomial) : Bool {
    constructor(l: Num, r: Num) : this(Minus(l, r).toPolynomial())

    override fun simplify(): Bool {
        return when (poly.deg) {
            -1 -> TRUE // 0 = 0 == TRUE
            0 -> return if (poly.coeffs[0] == 0.toBigInteger()) TRUE else FALSE // const = 0
            else -> return this
        }
    }

    override fun toString(): String {
        return "($poly=0)"
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
        else if (sL is GtPoly && sR is GtPoly) {
            return when {
                sL.poly.negEq(sR.poly) -> FALSE // x > 0 & -x > 0 == FALSE
                sL.poly.eq(sR.poly) -> return sL // x > 0 & x > 0 == x < 0
                else -> And(sL, sR)
            }
        } else if (sL is EqPoly && sR is EqPoly) {
            return if (sL.poly.negEq(sR.poly) || sL.poly.eq(sR.poly)) sL // x = 0 & -x = 0 == x = 0         x = 0 & x = 0 == x = 0
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
        else if (sL is GtPoly && sR is GtPoly) {
            return when {
                sL.poly.negEq(sR.poly) -> TRUE // x > 0 | -x > 0 == TRUE ++ x can't be 0 since sL and sR are simplified
                sL.poly.eq(sR.poly) -> return sL // x > 0 | x > 0 == x > 0
                else -> Or(sL, sR)
            }
        } else if (sL is EqPoly && sR is EqPoly) {
            return if (sL.poly.negEq(sR.poly) || sL.poly.eq(sR.poly)) sL // x = 0 | -x = 0 == x = 0         x = 0 | x = 0 == x = 0
            else Or(sL, sR)
        } else Or(sL, sR)
    }
}

