import core.model.*
import core.utils.parseBinaryOperation
import core.utils.parseConstantExpression
import core.utils.parseExpression
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ParseTests {
    @Test
    fun `parse constant`() {
        assertEquals(ConstantExpression("-2"), parseConstantExpression("-2"))
        assertEquals(null, parseConstantExpression("--2"))
        assertEquals(ConstantExpression("0000223123123"), parseConstantExpression("0000223123123"))
        assertEquals(null, parseConstantExpression("1234567b"))
        assertEquals(ConstantExpression("-1234567"), parseConstantExpression("-1234567"))
    }

    @Test
    fun `parse binary expression`() {
        assertEquals(Plus(ConstantExpression("1"), ConstantExpression("2")), parseBinaryOperation("(1+2)"))
        assertEquals(Mult(ConstantExpression("1"), ConstantExpression("2")), parseBinaryOperation("(1*2)"))
        assertEquals(Minus(ConstantExpression("1"), ConstantExpression("2")), parseBinaryOperation("(1-2)"))
        assertEquals(Gt(ConstantExpression("1"), ConstantExpression("2")), parseBinaryOperation("(1>2)"))
        assertEquals(Lt(ConstantExpression("1"), ConstantExpression("2")), parseBinaryOperation("(1<2)"))
        assertEquals(Eq(ConstantExpression("1"), ConstantExpression("2")), parseBinaryOperation("(1=2)"))
        assertEquals(And(TRUE, TRUE), parseBinaryOperation("((1=1)&(1=1))"))
        assertEquals(Or(TRUE, TRUE), parseBinaryOperation("((1=1)|(1=1))"))
        assertEquals(null, parseBinaryOperation("3+1"))
        assertEquals(
            Plus(ConstantExpression("3"), ConstantExpression("1")),
            parseBinaryOperation("(3+1)")
        )
        assertEquals(null, parseBinaryOperation("(3 + 1)"))
        assertEquals(
            Plus(Mult(ConstantExpression("2"), ConstantExpression("-1")), ConstantExpression("1000")),
            parseBinaryOperation("((2*-1)+1000)")
        )
        assertEquals(
            null,
            parseBinaryOperation("(2$1000)")
        )
        assertEquals(null, parseBinaryOperation("((3+4)-+3)"))
        assertEquals(Plus(ConstantExpression("-3"), ConstantExpression("-3")), parseBinaryOperation("(-3+-3)"))
        assertEquals(null, parseBinaryOperation("((3)&(2=1))"))
        //TODO
        assertThrows<NotImplementedError> { parseBinaryOperation("(3&(2=1))") }
    }

    @Test
    fun `parse expression`() {
        assertEquals(Element, parseExpression("element"))
        assertEquals(Plus(Element, ConstantExpression("1")), parseExpression("(element+1)"))
        assertEquals(Plus(ConstantExpression("1"), Element), parseExpression("(1+element)"))
        assertEquals(Plus(Element, Element), parseExpression("(element+element)"))
        assertEquals(null, parseExpression("((element)+element)"))
        assertEquals(Plus(Mult(Element, ConstantExpression("3")), Element), parseExpression("((element*3)+element)"))
    }

    @Test
    fun `parse map call`() {

    }
}