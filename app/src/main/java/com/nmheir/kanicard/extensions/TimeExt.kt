package com.nmheir.kanicard.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.nmheir.kanicard.R
import timber.log.Timber
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun LocalDateTime.relativeTime(): String {
    val now = LocalDateTime.now()

    if (this.isAfter(now)) return "In the future"

    val seconds = ChronoUnit.SECONDS.between(this, now)
    if (seconds < 60) {
        return stringResource(R.string.info_recently)
    }

    val minutes = ChronoUnit.MINUTES.between(this, now)
    if (minutes < 60) {
        return pluralStringResource(R.plurals.relative_minute_ago, minutes.toInt(), minutes)
    }

    val hours = ChronoUnit.HOURS.between(this, now)
    if (hours < 24) {
        return pluralStringResource(R.plurals.relative_hours_ago, hours.toInt(), hours)
    }

    val days = ChronoUnit.DAYS.between(this, now)
    if (days < 30) {
        return pluralStringResource(R.plurals.relative_days_ago, days.toInt(), days)
    }

    val months = ChronoUnit.MONTHS.between(this, now)
    val showMonths = if (months < 1) 1 else months
    return pluralStringResource(R.plurals.relative_months_ago, showMonths.toInt(), showMonths)
}

fun LocalDateTime.timeUntilDue(): String {
    val now = LocalDateTime.now()
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
        Timber.d("Decimal: %s", decimal.toString())
        Timber.d("Minute rem: %s", minutesRem.toString())
        Timber.d("Hours: %s", hours.toString())

        val formattedDecimal = "%.1f".format(decimal)
        Timber.d("Formatted decimal: %s", formattedDecimal)

        return if (formattedDecimal == "24,0") {
            "1d"
        } else {
            "< ${formattedDecimal}h"
        }
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
private val localDateFormat = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy", Locale.ENGLISH)

fun LocalDateTime.format3(): String {
    return this.format(format1)
}

fun LocalDate.formatLongDate(): String =
    this.format(localDateFormat)

fun LocalDate.isSameDay(other: LocalDate): Boolean {
    return this.year == other.year &&
            this.monthValue == other.monthValue &&
            this.dayOfMonth == other.dayOfMonth
}

fun LocalDate.isToday(): Boolean =
    this == LocalDate.now()