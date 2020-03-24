package core.model

sealed class Call

data class MapCall(val expression: Expression) : Call()

data class FilterCall(val expression: Expression) : Call()

data class CallChain(val calls: List<Call>)