package core

import core.model.*
import core.utils.composition

data class CallChain(val calls: List<Call>) {
    override fun toString(): String {
        return calls.joinToString("%>%")
    }
}

fun CallChain.reordered(): CallChain {
    val mapCalls = calls.filterIsInstance<MapCall>()
    val mapCompositions = mapCalls.firstOrNull()?.let {
        mutableListOf(it.expression)
    } ?: mutableListOf()
    for (i in 1 until mapCalls.size) {
        mapCompositions.add(mapCalls[i].expression.composition(mapCompositions.last()) as Num)
    }
    var filterExpr: Bool? = null
    var currMap: Int? = null
    for (i in calls) {
        if (i is FilterCall) {
            val func = currMap?.let { i.condition.composition(mapCompositions[it]) } ?: i.condition
            if (func !is Bool) throw TypeException()
            filterExpr = filterExpr?.let { And(it, func) } ?: func
        } else currMap = currMap?.inc() ?: 0
    }
    val mapCall: Expression? = mapCompositions.lastOrNull()
    return CallChain(
        listOf(
            FilterCall(filterExpr?.simplify() as Bool? ?: TRUE),
            when(mapCall) {
                is Num-> MapCallPoly(mapCall.asPolynomial)
                else -> MapCall(Element)
            }
        )
    )
}