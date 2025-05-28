package com.nmheir.kanicard.data.dto.card

import com.nmheir.kanicard.data.entities.note.parseFieldJson
import com.nmheir.kanicard.data.enums.State
import timber.log.Timber
import java.time.LocalDateTime

data class CardBrowseDto(
    val nid: Long,                      //Note id
    val tId: Long,                      //Template Id
    val dName: String,                  //Deck name
    val typeName: String,               //NoteType name
    val templateName: String,           //Template name
    val qfmt: String,                   //Question format from template
    val afmt: String,                   //Answer format from template
    val field: String,                  //field
    val lapse: Long,                    //Lapse count
    val state: State,                   //Card State
    val reviews: Int,                   //Review count
    val due: LocalDateTime,            //Due date
    val createdTime: LocalDateTime,    //Created date
    val modifiedTime: LocalDateTime    //Modified date
)

fun extractPlaceholder(format: String): List<String> {
    // Regex: tìm các cặp {{ key }}
    val regex = "\\{\\{\\s*([^}]+?)\\s*\\}\\}".toRegex()

    return regex.findAll(format)
        .map { it.groupValues[1] }  // lấy phần trong dấu {{…}}
        .toList()
}

/**
 * Lấy các giá trị trong fieldMap theo thứ tự keys, chỉ include khi value không blank,
 * rồi nối thành 1 chuỗi, ngăn cách bởi ", ".
 *
 * @param keys     Danh sách tên trường cần lấy (ví dụ ["front","hint"])
 * @param fieldMap Map<String, String> đã parse từ JSON
 * @return         Chuỗi nối các value hợp lệ, hoặc "" nếu không có trường nào.
 */
fun joinFieldValues(keys: List<String>, fieldMap: Map<String, String>): String {
    return keys
        .mapNotNull { key ->
            fieldMap[key]
                ?.takeIf { it.isNotBlank() }    // chỉ lấy khi không blank
        }
        .joinToString(separator = ", ")
}

fun CardBrowseDto.toCardBrowseData(): CardBrowseData {
    val qstKey = extractPlaceholder(this.qfmt)
    val ansKey = extractPlaceholder(this.afmt)
    val fieldJson = parseFieldJson(this.field)
    val qst = joinFieldValues(qstKey, fieldJson)
    val ans = joinFieldValues(ansKey, fieldJson)

    return CardBrowseData(
        nid = this.nid,
        tId = this.tId,
        dName = this.dName,
        typeName = this.typeName,
        templateName = this.templateName,
        qst = qst,
        ans = ans,
        lapse = this.lapse,
        state = this.state,
        reviews = this.reviews,
        due = this.due,
        createdTime = this.createdTime,
        modifiedTime = this.modifiedTime
    )
}