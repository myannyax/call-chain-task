import core.CallChain
import core.model.*
import core.utils.Polynomial
import core.utils.parseExpression
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigInteger

class ToStringTest {
    @Test
    fun `test toString`() {
        assertEquals(Plus(ConstantExpression("1"), ConstantExpression("2")).toString(), "(1+2)")
        assertEquals(Mult(ConstantExpression("1"), ConstantExpression("2")).toString(), "(1*2)")
        assertEquals(Minus(ConstantExpression("1"), ConstantExpression("2")).toString(), "(1-2)")
        assertEquals(Gt(ConstantExpression("1"), ConstantExpression("2")).toString(), "(1>2)")
        assertEquals(Lt(ConstantExpression("1"), ConstantExpression("2")).toString(), "(1<2)")
        assertEquals(Eq(ConstantExpression("1"), ConstantExpression("2")).toString(), "(1=2)")
        assertEquals(And(TRUE, TRUE).toString(), "((1=1)&(1=1))")
        assertEquals(Or(TRUE, TRUE).toString(), "((1=1)|(1=1))")

        assertEquals(
            MapCall(
                Plus(
                    Mult(Element, Element),
                    Plus(Mult(Element, ConstantExpression("20")), ConstantExpression("100"))
                )
            ).toString(), "map{((element*element)+((element*20)+100))}"
        )
        assertEquals(
            FilterCall(Gt(Plus(Element, ConstantExpression("10")), ConstantExpression("10"))).toString(),
            "filter{((element+10)>10)}"
        )
        assertEquals(
            CallChain(
                listOf(
                    MapCall(Plus(Element, ConstantExpression("10"))),
                    FilterCall(Gt(Element, ConstantExpression("10"))),
                    MapCall(Mult(Element, Element))
                )
            ).toString(), "map{(element+10)}%>%filter{(element>10)}%>%map{(element*element)}"
        )

    }

    @Test
    fun `test toString poly`() {
        assertEquals(
            "(((((((-10000000+(-1000000*element))+(300000*(element*element)))+(30000*(element*(element*element))))+(-3000*(element*(element*(element*element)))))+(-300*(element*(element*(element*(element*element))))))+(10*(element*(element*(element*(element*(element*element)))))))+(element*(element*(element*(element*(element*(element*element)))))))",
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
                ).map { it.toBigInteger() } as MutableList<BigInteger>).toString()
        )
        assertDoesNotThrow {
            parseExpression(Polynomial(
                listOf(
                    -10000000,
                    -1000000,
                    300000,
                    30000,
                    -3000,
                    -300,
                    10,
                    1
                ).map { it.toBigInteger() } as MutableList<BigInteger>).toString())
        }
    }
}