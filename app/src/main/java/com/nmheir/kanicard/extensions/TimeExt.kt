package com.nmheir.kanicard.extensions

import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

fun OffsetDateTime.relativeTime(): String {
    val now = OffsetDateTime.now()
    // Nếu trong quá khứ; ngược lại có thể trả về "in the future" hoặc tùy xử lý
    if (this.isAfter(now)) return "in the future"

    val hours = ChronoUnit.HOURS.between(this, now)
    if (hours < 24) {
        return "$hours hour${if (hours != 1L) "s" else ""} ago"
    }

    val days = ChronoUnit.DAYS.between(this, now)
    if (days < 30) {
        return "$days day${if (days != 1L) "s" else ""} ago"
    }

    val months = ChronoUnit.MONTHS.between(this, now)
    return when {
        months < 1 -> "1 month ago"                  // đã qua 30–59 ngày
        else -> "$months month${if (months != 1L) "s" else ""} ago"
    }
}