package com.nmheir.kanicard.domain.mapper

interface Mapper<T, V> {
    fun to(value: T): V
    fun from(value: V): T
}