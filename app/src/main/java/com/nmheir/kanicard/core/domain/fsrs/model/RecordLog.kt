package com.nmheir.kanicard.core.domain.fsrs.model

import com.nmheir.kanicard.data.enums.Rating

class RecordLog(
    private val items: Map<Rating, RecordLogItem>
) {
    operator fun get(rating: Rating) = items[rating]
}