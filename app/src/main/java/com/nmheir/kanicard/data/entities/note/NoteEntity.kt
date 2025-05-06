package com.nmheir.kanicard.data.entities.note

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nmheir.kanicard.data.dto.note.NoteDto
import com.nmheir.kanicard.data.entities.card.CardTemplateEntity
import com.nmheir.kanicard.data.entities.deck.DeckEntity
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.OffsetDateTime

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = CardTemplateEntity::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE   // Nếu xoá deck thì xoá luôn note
        )
    ],
    indices = [
        Index("templateId")
    ]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val noteId: Long = 0,
    val deckId: Long,
    val templateId: Long,
    val fieldJson: String,
    val createdTime: OffsetDateTime,
    val modifiedTime: OffsetDateTime
)

/**
 * Xây dựng JSON string cho fieldJson dựa trên danh sách fieldDefs và map các giá trị nhập vào.
 *
 * @param fieldDefs: danh sách FieldDefEntity (có name, ord,…)
 * @param values: map từ tên field (tương ứng def.name) sang giá trị người dùng nhập
 * @return chuỗi JSON, ví dụ: {"name":"Alice","description":"She's very beautiful"}
 */
fun buildFieldJson(
    fieldDefs: List<FieldDefEntity>,
    values: Map<String, String>
): String {
    val jsonObject = buildJsonObject {
        for (def in fieldDefs) {
            val raw = values[def.name] ?: ""
            put(def.name, JsonPrimitive(raw))
        }
    }
    return Json.encodeToString(JsonObject.serializer(), jsonObject)
}

fun parseFieldJson(fieldJson: String): Map<String, String> {
    val jsonObject = Json.decodeFromString<JsonObject>(fieldJson)
    return jsonObject.mapValues { it.value.jsonPrimitive.content }
}