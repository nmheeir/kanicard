package com.nmheir.kanicard.ui.screen.statistics.model

import com.nmheir.kanicard.data.enums.Rating


data class AnswerButtonChartData(
    val barData: Map<AnswerButtonChartCardType, Map<Rating, Int>> = emptyMap()
) {
    fun toRatingSeries() : Map<Rating, List<Float>> {
        val types = AnswerButtonChartCardType.entries
        val ratings = Rating.entries

        return ratings.associateWith { rating ->
            types.map { type ->
                barData[type]?.get(rating)?.toFloat() ?: 0f
            }
        }
    }
}

enum class AnswerButtonChartCardType {
    Learning, Young, Mature
}

enum class AnswerButtonChartState(val title: String) {
    ONE_MONTH("1 month"), THREE_MONTHS("3 months"), ONE_YEAR("1 year")
}