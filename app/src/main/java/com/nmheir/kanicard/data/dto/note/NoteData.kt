package com.nmheir.kanicard.data.dto.note

data class NoteData(
    val id: Long,
    val dId: Long,
    val qFmt: String,   //HTML string
    val aFmt: String    //HTML string
)
