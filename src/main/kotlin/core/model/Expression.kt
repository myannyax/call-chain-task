package core.model

interface Expression

interface Bool : Expression

interface Num : Expression

val TRUE = Eq(ConstantExpression("1"), ConstantExpression("1"))

val FALSE = Eq(ConstantExpression("1"), ConstantExpression("0"))

data class ConstantExpression(val number: String) : Num {
    override fun toString(): String {
        return number
    }
}

object Element : Num {
    override fun toString(): String {
        return "element"
    }
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
}

data class Minus(override val l: Num, override val r: Num) : BinaryOperator(l, r, "-"), Num {
    override fun toString(): String {
        return super.toString()
    }
}

data class Mult(override val l: Num, override val r: Num) : BinaryOperator(l, r, "*"), Num {
    override fun toString(): String {
        return super.toString()
    }
}

data class Gt(override val l: Num, override val r: Num) : BinaryOperator(l, r, ">"), Bool {
    override fun toString(): String {
        return super.toString()
    }
}

data class Lt(override val l: Num, override val r: Num) : BinaryOperator(l, r, "<"), Bool {
    override fun toString(): String {
        return super.toString()
    }
}

data class Eq(override val l: Num, override val r: Num) : BinaryOperator(l, r, "="), Bool {
    override fun toString(): String {
        return super.toString()
    }
}

data class And(override val l: Bool, override val r: Bool) : BinaryOperator(l, r, "&"), Bool {
    override fun toString(): String {
        return super.toString()
    }
}

data class Or(override val l: Bool, override val r: Bool) : BinaryOperator(l, r, "|"), Bool {
    override fun toString(): String {
        return super.toString()
    }
}

