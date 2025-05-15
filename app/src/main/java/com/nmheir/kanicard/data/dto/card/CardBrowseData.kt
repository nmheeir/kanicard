package com.nmheir.kanicard.data.dto.card

import com.nmheir.kanicard.data.enums.State
import java.time.OffsetDateTime

data class CardBrowseData(
    val nid: Long,               //Note id
    val tId: Long,              //Template Id
    val dName: String,          //Deck name
    val typeName: String,       //NoteType name
    val templateName: String,   //Template name
    val qst: String,            //Field in question
    val ans: String,            //Field in answer
    val lapse: Long,            //Lapse count
    val state: State,           //Card State
    val reviews: Int,           //Review count
    val due: OffsetDateTime,    //Due date
    val createdTime: OffsetDateTime,    //Created date
    val modifiedTime: OffsetDateTime    //Modified date
)
