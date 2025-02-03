package com.nmheir.kanicard.data.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import java.time.format.DateTimeFormatter
/*

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifierinternal
annotation class DateString
class MoshiLocalDateAdapter {
    @ToJson
    fun toJson(@DateString value: LocalDateTime): String {
        return FORMATTER.format(value)
    }

    @FromJson
    @DateString
    fun fromJson(value: String): LocalDateTime {
        return FORMATTER.parse(value, LocalDateTime.FROM)
    }

    companion object {
        private val FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }

}*/
