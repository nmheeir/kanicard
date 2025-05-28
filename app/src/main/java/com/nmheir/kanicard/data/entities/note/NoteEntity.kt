package com.nmheir.kanicard.data.entities.note

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nmheir.kanicard.data.entities.card.TemplateEntity
import com.nmheir.kanicard.data.entities.deck.DeckEntity
import com.nmheir.kanicard.ui.viewmodels.FieldValue
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.LocalDateTime

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = TemplateEntity::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["dId"],
            onDelete = ForeignKey.CASCADE   // Nếu xoá deck thì xoá luôn note
        )
    ],
    indices = [
        Index("templateId")
    ]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dId: Long,
    val templateId: Long,
    val fieldJson: String,
    val createdTime: LocalDateTime,
    val modifiedTime: LocalDateTime
)

/**
 * Xây dựng JSON string cho fieldJson dựa trên danh sách fieldDefs và map các giá trị nhập vào.
 *
 * @param values: map từ tên field (tương ứng def.name) sang giá trị người dùng nhập
 * @return chuỗi JSON, ví dụ: {"name":"Alice","description":"She's very beautiful"}
 */
fun buildFieldJson(values: Map<String, String>): String {
    val jsonObject = buildJsonObject {
        for ((key, value) in values) {
            put(key, JsonPrimitive(value))
        }
    }
    return Json.encodeToString(JsonObject.serializer(), jsonObject)
}

fun buildFieldJson(values: List<FieldValue>): String {
    val jsonObject = buildJsonObject {
        values.forEach {
            put(it.fieldName, JsonPrimitive(it.value.text.toString()))
        }
    }
    return Json.encodeToString(JsonObject.serializer(), jsonObject)
}

fun parseFieldJson(fieldJson: String): Map<String, String> {
    val jsonObject = Json.decodeFromString<JsonObject>(fieldJson)
    return jsonObject.mapValues { it.value.jsonPrimitive.content }
}