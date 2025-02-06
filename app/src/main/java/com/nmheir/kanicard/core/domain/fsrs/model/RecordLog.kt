package com.nmheir.kanicard.core.domain.fsrs.model

class RecordLog(
    private val items: Map<Rating, RecordLogItem>
) {
    operator fun get(rating: Rating) = items[rating]
}