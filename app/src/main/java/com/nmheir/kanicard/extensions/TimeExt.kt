package com.nmheir.kanicard.extensions

import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

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

fun OffsetDateTime.timeUntilDue(): String {
    val now = OffsetDateTime.now()
    if (this.isBefore(now)) return "< 1m"  // đã qua rồi, coi như < 1 phút

    val duration = Duration.between(now, this)
    val totalMinutes = duration.toMinutes()

    // 1) Nếu < 1 phút
    if (totalMinutes < 1) {
        return "< 1m"
    }

    // 2) Nếu < 60 phút
    if (totalMinutes < 60) {
        val mins = totalMinutes  // đã là số nguyên
        return "< ${mins}m"
    }

    // 3) Nếu < 24 giờ → hiển thị giờ thập phân
    val totalHours = duration.toHours()
    if (totalHours < 24) {
        val hours = totalHours
        val minutesRem = totalMinutes % 60
        val decimal = hours + minutesRem / 60.0
        // Giữ 1 chữ số sau dấu thập phân
        return "${"%.1f".format(decimal)}h"
    }

    // 4) Nếu < 30 ngày → hiển thị ngày thập phân
    val totalDays = duration.toDays()
    if (totalDays < 30) {
        val days = totalDays
        val hoursRem = duration.minusDays(days).toHours()
        val decimal = days + hoursRem / 24.0
        return "${"%.1f".format(decimal)}d"
    }

    // 5) Nếu < 12 tháng → hiển thị tháng thập phân (gần đúng, 1 tháng = 30 ngày)
    if (totalDays < 365) {
        val months = totalDays / 30
        val daysRem = totalDays % 30
        val decimal = months + daysRem / 30.0
        return "${"%.1f".format(decimal)}M"
    }

    // 6) Trên 1 năm → hiển thị năm thập phân (1 năm = 365 ngày)
    val years = totalDays / 365
    val daysRem = totalDays % 365
    val decimal = years + daysRem / 365.0
    return "${"%.1f".format(decimal)}y"
}

private val format1 = DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a", Locale.ENGLISH)

fun OffsetDateTime.format3(): String {
    return this.format(format1)
}
