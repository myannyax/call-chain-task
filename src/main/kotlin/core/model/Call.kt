package core.model

sealed class Call

data class MapCall(val expression: Expression) : Call() {
    override fun toString(): String {
        return "map{$expression}"
    }
}

data class FilterCall(val condition: Bool) : Call() {
    override fun toString(): String {
        return "filter{$condition}"
    }
}