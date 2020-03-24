package core.model

sealed class Expression

data class ConstantExpression(val number: String) : Expression()

data class BinaryExpression(val l: Expression, val op: Operation, val r: Expression) : Expression()

object Element : Expression()

enum class Operation {
    Plus,
    Minus,
    Mult,
    Gt,
    Lt,
    Eq,
    And,
    Or
}

fun String.toOp(): Operation {
    return when(this) {
        "+" -> Operation.Plus
        "-" -> Operation.Minus
        "*" -> Operation.Mult
        ">" -> Operation.Gt
        "<" -> Operation.Lt
        "=" -> Operation.Eq
        "&" -> Operation.And
        "|" -> Operation.Or
        else -> TODO("Error")
    }
}