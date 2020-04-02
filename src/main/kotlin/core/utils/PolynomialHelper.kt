package core.utils

import java.math.BigInteger
import kotlin.math.max

class Polynomial(a: BigInteger, b: Int) {
    var coeffs: MutableList<BigInteger> = MutableList(b + 1) { 0.toBigInteger() }.run {
        this[b] = a
        this
    }

    var deg = degree()
        private set

    private fun degree(): Int {
        var d = -1
        for (i in coeffs.indices) if (coeffs[i] !== 0.toBigInteger()) d = i
        return d
    }

    operator fun plus(b: Polynomial): Polynomial {
        val a = this
        val c = Polynomial(0.toBigInteger(), max(a.deg, b.deg))
        for (i in 0..a.deg) {
            c.coeffs[i] += a.coeffs[i]
        }
        for (i in 0..b.deg) {
            c.coeffs[i] += b.coeffs[i]
        }
        c.deg = c.degree()
        return c
    }

    operator fun minus(b: Polynomial): Polynomial {
        val a = this
        val c = Polynomial(0.toBigInteger(), max(a.deg, b.deg))
        for (i in 0..a.deg) c.coeffs[i] += a.coeffs[i]
        for (i in 0..b.deg) c.coeffs[i] -= b.coeffs[i]
        c.deg = c.degree()
        return c
    }

    operator fun times(b: Polynomial): Polynomial {
        val a = this
        val c = Polynomial(0.toBigInteger(), a.deg + b.deg)
        for (i in 0..a.deg) {
            for (j in 0..b.deg) {
                c.coeffs[i + j] += a.coeffs[i] * b.coeffs[j]
            }
        }
        c.deg = c.degree()
        return c
    }
}