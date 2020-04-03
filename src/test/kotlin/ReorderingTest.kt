import core.CallChain
import core.model.*
import core.reordered
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ReorderingTest {
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
                                    ConstantExpression("20"),
                                    Element
                                )
                            ),
                            Mult(
                                Element,
                                Element
                            )
                        )
                    )
                )
            ).toString(),
            CallChain(
                listOf(
                    MapCall(Plus(Element, ConstantExpression("10"))),
                    FilterCall(Gt(Element, ConstantExpression("10"))),
                    MapCall(Mult(Element, Element))
                )
            ).reordered().toString()
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
                                        ConstantExpression("-1"),
                                        Element
                                    )
                                ), ConstantExpression("0")
                            )
                        )
                    ),
                    MapCall(Element)
                )
            ).toString(),
            CallChain(
                listOf(
                    FilterCall(
                        Gt(Element, ConstantExpression("10"))
                    ),
                    FilterCall(
                        Lt(Element, ConstantExpression("20"))
                    )
                )
            ).reordered().toString()
        )
        assertEquals(
            CallChain(
                listOf(
                    FilterCall(
                        FALSE
                    ),
                    MapCall(Mult(Element, Element))
                )
            ).toString(),
            CallChain(
                listOf(
                    FilterCall(Gt(Element, ConstantExpression("0"))),
                    FilterCall(Lt(Element, ConstantExpression("0"))),
                    MapCall(Mult(Element, Element))
                )
            ).reordered().toString()
        )
    }
}