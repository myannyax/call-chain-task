import core.CallChain
import core.ParseException
import core.TypeException
import core.model.*
import core.reordered
import core.utils.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ParseTests {
    @Test
    fun `parse constant`() {
        assertEquals(ConstantExpression("-2"), parseConstantExpression("-2"))
        assertThrows<ParseException> { parseConstantExpression("--2") }
        assertEquals(ConstantExpression("0000223123123"), parseConstantExpression("0000223123123"))
        assertThrows<ParseException> { parseConstantExpression("1234567b") }
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
        assertThrows<ParseException> { parseBinaryOperation("3+1") }
        assertEquals(
            Plus(ConstantExpression("3"), ConstantExpression("1")),
            parseBinaryOperation("(3+1)")
        )
        assertThrows<ParseException> { parseBinaryOperation("(3 + 1)") }
        assertEquals(
            Plus(Mult(ConstantExpression("2"), ConstantExpression("-1")), ConstantExpression("1000")),
            parseBinaryOperation("((2*-1)+1000)")
        )
        assertThrows<ParseException> { parseBinaryOperation("(2$1000)") }
        assertThrows<ParseException> { parseBinaryOperation("((3+4)-+3)") }
        assertEquals(Plus(ConstantExpression("-3"), ConstantExpression("-3")), parseBinaryOperation("(-3+-3)"))
        assertThrows<ParseException> { parseBinaryOperation("((3)&(2=1))") }
        assertThrows<TypeException> { parseBinaryOperation("(3&(2=1))") }
    }

    @Test
    fun `parse expression`() {
        assertEquals(Element, parseExpression("element"))
        assertEquals(Plus(Element, ConstantExpression("1")), parseExpression("(element+1)"))
        assertEquals(Plus(ConstantExpression("1"), Element), parseExpression("(1+element)"))
        assertEquals(Plus(Element, Element), parseExpression("(element+element)"))
        assertThrows<ParseException> { parseExpression("((element)+element)") }
        assertEquals(Plus(Mult(Element, ConstantExpression("3")), Element), parseExpression("((element*3)+element)"))
    }

    @Test
    fun `parse call`() {
        assertEquals(MapCall(Plus(Element, ConstantExpression("10"))), parseCall("map{(element+10)}"))
        assertEquals(
            MapCall(
                Plus(
                    Mult(Element, Element),
                    Plus(Mult(Element, ConstantExpression("20")), ConstantExpression("100"))
                )
            ), parseCall("map{((element*element)+((element*20)+100))}")
        )

        assertEquals(FilterCall(Lt(Element, ConstantExpression("0"))), parseCall("filter{(element<0)}"))

        assertEquals(
            FilterCall(Gt(Plus(Element, ConstantExpression("10")), ConstantExpression("10"))),
            parseCall("filter{((element+10)>10)}")
        )

        assertThrows<TypeException> { parseCall("filter{(element+10)}") }
    }

    @Test
    fun `parse call chain`() {
        assertEquals(
            CallChain(
                listOf(
                    FilterCall(
                        Gt(Element, ConstantExpression("10"))
                    ),
                    FilterCall(
                        Lt(Element, ConstantExpression("20"))
                    )
                )
            ),
            parseCallChain("filter{(element>10)}%>%filter{(element<20)}")
        )

        assertEquals(
            CallChain(
                listOf(
                    MapCall(Plus(Element, ConstantExpression("10"))),
                    FilterCall(Gt(Element, ConstantExpression("10"))),
                    MapCall(Mult(Element, Element))
                )
            ),
            parseCallChain("map{(element+10)}%>%filter{(element>10)}%>%map{(element*element)}")
        )
        assertEquals(
            CallChain(
                listOf(
                    FilterCall(Gt(Element, ConstantExpression("0"))),
                    FilterCall(Lt(Element, ConstantExpression("0"))),
                    MapCall(Mult(Element, Element))
                )
            ), parseCallChain("filter{(element>0)}%>%filter{(element<0)}%>%map{(element*element)}")
        )
    }

    @Test
    fun `as polynomial`() {
        assertEquals(
            listOf(0.toBigInteger(), 1.toBigInteger()),
            Element.toPolynomial().coeffs.toList()
        )
        assertEquals(
            listOf(0.toBigInteger(), 0.toBigInteger(), 1.toBigInteger()),
            Mult(
                Element,
                Element
            ).toPolynomial().coeffs.toList()
        )
    }

    @Test
    fun `test simplify`() {
        assertEquals(
            parseExpression(
                "(((((((-10000000+(element*-1000000))+((element*element)*300000))+" +
                        "((element*(element*element))*30000))+((element*(element*(element*element)))*-3000))+" +
                        "((element*(element*(element*(element*element))))*-300))+((element*(element*(element*" +
                        "(element*(element*element)))))*10))+(element*(element*(element*(element*(element*(element*element)))))))"
            ),
            parseExpression("(((((((element+10)*(element+10))*(element+10))*(element+10))*(element-10))*(element-10))*(element-10))").simplify()
        )
        assertEquals(
            Mult(
                Element,
                ConstantExpression("20")
            ),
            Mult(
                Element,
                ConstantExpression("20")
            ).simplify()
        )
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
            Gt(
                Plus(
                    ConstantExpression("5"),
                    Element
                ),
                ConstantExpression("0")
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
    }

    @Test
    fun `test reordering`() {
        assertEquals(
            CallChain(
                listOf(
                    FilterCall(
                        Gt(
                            Element,
                            ConstantExpression("0")
                        )
                    ),
                    MapCall(
                        Plus(
                            Plus(
                                ConstantExpression("100"),
                                Mult(
                                    Element,
                                    ConstantExpression("20")
                                )
                            ),
                            Mult(
                                Element,
                                Element
                            )
                        )
                    )
                )
            ),
            CallChain(
                listOf(
                    MapCall(Plus(Element, ConstantExpression("10"))),
                    FilterCall(Gt(Element, ConstantExpression("10"))),
                    MapCall(Mult(Element, Element))
                )
            ).reordered()
        )
        assertEquals(
            CallChain(
                listOf(
                    FilterCall(
                        And(
                            Gt(
                                Plus(
                                    ConstantExpression("-10"),
                                    Element
                                ), ConstantExpression("0")
                            ),
                            Gt(
                                Plus(
                                    ConstantExpression("20"),
                                    Mult(
                                        Element,
                                        ConstantExpression("-1")
                                    )
                                ), ConstantExpression("0")
                            )
                        )
                    ),
                    MapCall(Element)
                )
            ),
            CallChain(
                listOf(
                    FilterCall(
                        Gt(Element, ConstantExpression("10"))
                    ),
                    FilterCall(
                        Lt(Element, ConstantExpression("20"))
                    )
                )
            ).reordered()
        )
        assertEquals(
            CallChain(
                listOf(
                    FilterCall(
                        FALSE
                    ),
                    MapCall(Mult(Element, Element))
                )
            ),
            CallChain(
                listOf(
                    FilterCall(Gt(Element, ConstantExpression("0"))),
                    FilterCall(Lt(Element, ConstantExpression("0"))),
                    MapCall(Mult(Element, Element))
                )
            ).reordered()
        )
    }

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
}