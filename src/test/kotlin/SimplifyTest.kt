import core.model.*
import core.utils.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigInteger

class SimplifyTest {
    @Test
    fun `as polynomial`() {
        assertEquals(
            listOf(0.toBigInteger(), 1.toBigInteger()),
            Element.asPolynomial.coeffs.toList()
        )
        assertEquals(
            listOf(0.toBigInteger(), 0.toBigInteger(), 1.toBigInteger()),
            Mult(
                Element,
                Element
            ).asPolynomial.coeffs.toList()
        )
        assertEquals(
            Polynomial(
                listOf(
                    -10000000,
                    -1000000,
                    300000,
                    30000,
                    -3000,
                    -300,
                    10,
                    1
                ).map { it.toBigInteger() } as MutableList<BigInteger>),
            (parseExpression("(((((((element+10)*(element+10))*(element+10))*(element+10))*(element-10))*(element-10))*(element-10))") as Num).asPolynomial
        )
        assertEquals(
            Polynomial(listOf(0, 20).map { it.toBigInteger() } as MutableList<BigInteger>),
            Mult(
                Element,
                ConstantExpression("20")
            ).asPolynomial
        )
    }

    @Test
    fun `test simplify`() {
        assertEquals(
            FALSE,
            Gt(
                ConstantExpression("5"),
                ConstantExpression("7")
            ).simplify()
        )

        assertEquals(
            TRUE,
            Lt(
                ConstantExpression("5"),
                ConstantExpression("7")
            ).simplify()
        )

        assertEquals(
            FALSE,
            Or(
                Lt(
                    ConstantExpression("5"),
                    ConstantExpression("5")
                ),
                Gt(
                    ConstantExpression("5"),
                    ConstantExpression("5")
                )
            ).simplify()
        )

        assertEquals(
            FALSE,
            And(
                Lt(
                    ConstantExpression("5"),
                    ConstantExpression("5")
                ),
                Gt(
                    ConstantExpression("5"),
                    ConstantExpression("5")
                )
            ).simplify()
        )
        assertEquals(
            FALSE,
            Or(
                Lt(
                    ConstantExpression("5"),
                    ConstantExpression("5")
                ),
                Gt(
                    ConstantExpression("5"),
                    ConstantExpression("5")
                )
            ).simplify()
        )
        assertEquals(
            GtPoly(
                Polynomial(listOf(5, 1).map { it.toBigInteger() } as MutableList<BigInteger>)
            ),
            Or(
                Gt(
                    Plus(
                        Element,
                        ConstantExpression("5")
                    ),
                    ConstantExpression("0")
                ),
                Gt(
                    ConstantExpression("-5"),
                    ConstantExpression("0")
                )
            ).simplify()
        )
        assertEquals(
            FALSE,
            And(
                GtPoly(Polynomial(listOf(1, 2).map { it.toBigInteger() } as MutableList<BigInteger>)),
                GtPoly(Polynomial(listOf(-1, -2).map { it.toBigInteger() } as MutableList<BigInteger>))
            ).simplify()
        )
        assertEquals(
            GtPoly(Polynomial(listOf(1, 2).map { it.toBigInteger() } as MutableList<BigInteger>)),
            And(
                GtPoly(Polynomial(listOf(1, 2).map { it.toBigInteger() } as MutableList<BigInteger>)),
                GtPoly(Polynomial(listOf(1, 2).map { it.toBigInteger() } as MutableList<BigInteger>))
            ).simplify()
        )
        assertEquals(
            EqPoly(Polynomial(listOf(1, 2).map { it.toBigInteger() } as MutableList<BigInteger>)),
            And(
                EqPoly(Polynomial(listOf(1, 2).map { it.toBigInteger() } as MutableList<BigInteger>)),
                EqPoly(Polynomial(listOf(-1, -2).map { it.toBigInteger() } as MutableList<BigInteger>))
            ).simplify()
        )
        assertEquals(
            And(
                EqPoly(Polynomial(listOf(-12, 1).map { it.toBigInteger() } as MutableList<BigInteger>)),
                EqPoly(Polynomial(listOf(-18, 0, 1).map { it.toBigInteger() } as MutableList<BigInteger>))
            ),
            And(
                Eq(Element, ConstantExpression("12")),
                Eq(Mult(Element, Element), ConstantExpression("18"))
            ).simplify()
        )
        assertEquals(
            Or(
                EqPoly(Polynomial(listOf(-12, 1).map { it.toBigInteger() } as MutableList<BigInteger>)),
                EqPoly(Polynomial(listOf(-18, 0, 1).map { it.toBigInteger() } as MutableList<BigInteger>))
            ),
            Or(
                Eq(Element, ConstantExpression("12")),
                Eq(Mult(Element, Element), ConstantExpression("18"))
            ).simplify()
        )
        assertEquals(
            GtPoly(Polynomial(listOf(-12, 1).map { it.toBigInteger() } as MutableList<BigInteger>)),
            GtPoly(Polynomial(listOf(-12, 1).map { it.toBigInteger() } as MutableList<BigInteger>)).simplify()
        )
    }
}