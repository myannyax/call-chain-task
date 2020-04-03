package core.model

import core.utils.Polynomial

sealed class Call

data class MapCall(val expression: Expression) : Call() {
    override fun toString(): String {
        return "map{$expression}"
    }
}

data class MapCallPoly(val poly: Polynomial) : Call() {
    override fun toString(): String {
        return "map{$poly}"
    }
}

data class FilterCall(val condition: Bool) : Call() {
    override fun toString(): String {
        return "filter{$condition}"
    }
}