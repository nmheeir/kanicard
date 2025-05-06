package com.nmheir.kanicard.data.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.nmheir.kanicard.data.entities.deck.DeckEntity
import com.nmheir.kanicard.data.entities.fsrs.FsrsCardEntity
import com.nmheir.kanicard.data.entities.note.NoteEntity

data class DeckWithNotesAndTemplates(
    @Embedded
    val deck: DeckEntity,

    @Relation(
        entity = NoteEntity::class,
        parentColumn = "id",      // khóa chính trên DeckEntity
        entityColumn = "noteId",  // khóa chính trên NoteEntity
        associateBy = Junction(
            value = FsrsCardEntity::class,
            parentColumn = "deckId",  // khoá ngoại trong fsrs_card trỏ về deck
            entityColumn = "nId"      // khoá ngoại trong fsrs_card trỏ về note
        )
    )
    val notes: List<NoteAndTemplate>
)