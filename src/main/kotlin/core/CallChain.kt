package core

import core.model.*
import core.utils.compWith

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
        mapCompositions.add(mapCalls[i].expression.compWith(mapCompositions.last()))
    }
    var filterExpr: Bool? = null
    var currMap: Int? = null
    for (i in calls) {
        if (i is FilterCall) {
            val func = currMap?.let { i.condition.compWith(mapCompositions[it]) } ?: i.condition
            if (func !is Bool) TODO("TypeError")
            filterExpr = filterExpr?.let { And(it, func) } ?: func
        } else currMap = currMap?.inc() ?: 0
    }
    return CallChain(
        listOf(
            FilterCall(filterExpr ?: TRUE),
            MapCall(mapCompositions.lastOrNull() ?: Element)
        )
    )
}