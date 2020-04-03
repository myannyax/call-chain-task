package core.utils

import java.math.BigInteger
import kotlin.math.max

data class Polynomial(val coeffs: MutableList<BigInteger>) {
    constructor(a: BigInteger, b: Int) : this(MutableList(b + 1) { 0.toBigInteger() }.run {
        this[b] = a
        this
    })

    var deg = degree()
        private set

    fun eq(b: Polynomial): Boolean {
        if (deg != b.deg) return false
        for (i in 0..deg) if (coeffs[i] != b.coeffs[i]) return false
        return true
    }

    fun negEq(b: Polynomial): Boolean {
        if (deg != b.deg) return false
        for (i in 0..deg) if (coeffs[i] != -b.coeffs[i]) return false
        return true
    }

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

    override fun toString(): String {
        if (deg == -1) return "0"
        return buildString {
            var elemStr = ""
            var cnt = 0
            var fst = true
            for (i in 0..deg) {
                if (coeffs[i] != 0.toBigInteger()) {
                    if (elemStr == "") append(coeffs[i])
                    else {
                        val expr = if (coeffs[i] == 1.toBigInteger()) elemStr else "(${coeffs[i]}*$elemStr)"
                        if (!fst) {
                            append("+$expr)")
                            cnt++
                        } else append(expr)
                    }
                    fst = false
                }
                elemStr = if (elemStr == "") "element" else "(element*$elemStr)"
            }
            insert(0, "(".repeat(cnt))
        }
    }
}