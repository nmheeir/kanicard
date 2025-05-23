package com.nmheir.kanicard.data.converters

import androidx.room.TypeConverter
import com.nmheir.kanicard.data.enums.Rating
import com.nmheir.kanicard.data.enums.State
import java.time.OffsetDateTime

class Converters {
    @TypeConverter
    fun offsetDateTimeToString(value: OffsetDateTime): String {
        return value.toString()
    }

    @TypeConverter
    fun stringToOffsetDateTime(value: String): OffsetDateTime {
        return OffsetDateTime.parse(value)
    }

    @TypeConverter
    fun stateToString(value: State): String {
        return value.name
    }

    @TypeConverter
    fun stringToState(value: String): State {
        return value.let { State.valueOf(value) }
    }

    @TypeConverter
    fun ratingToString(value: Rating): String {
        return value.name
    }

    @TypeConverter
    fun stringToRating(value: String): Rating {
        return value.let { Rating.valueOf(value) }
    }

    @TypeConverter
    fun listDoubleToString(value: List<Double>): String {
        return value.joinToString(", ")
    }

    @TypeConverter
    fun stringToListDouble(value: String): List<Double> {
        return value.split(", ").map { it.toDouble() }
    }
}