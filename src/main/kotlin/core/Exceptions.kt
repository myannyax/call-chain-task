package core

class TypeException : Exception {

    constructor(message: String = "TYPE ERROR") : super(message)

    constructor(cause: Throwable) : super(cause)

    constructor(message: String, cause: Throwable) : super(message, cause)
}

class ParseException : Exception {

    constructor(message: String = "SYNTAX ERROR") : super(message)

    constructor(cause: Throwable) : super(cause)

    constructor(message: String, cause: Throwable) : super(message, cause)
}

class ASTException : Exception {
    constructor(message: String = "") : super(message)

    constructor(cause: Throwable) : super(cause)

    constructor(message: String, cause: Throwable) : super(message, cause)
}