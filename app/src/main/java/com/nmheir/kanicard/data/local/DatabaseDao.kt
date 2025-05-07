package com.nmheir.kanicard.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.nmheir.kanicard.data.dto.deck.DeckWidgetData
import com.nmheir.kanicard.data.entities.SearchHistoryEntity
import com.nmheir.kanicard.data.entities.card.CardTemplateEntity
import com.nmheir.kanicard.data.entities.deck.DeckEntity
import com.nmheir.kanicard.data.entities.fsrs.FsrsCardEntity
import com.nmheir.kanicard.data.entities.fsrs.ReviewLogEntity
import com.nmheir.kanicard.data.entities.note.FieldDefEntity
import com.nmheir.kanicard.data.entities.note.NoteEntity
import com.nmheir.kanicard.data.entities.note.NoteTypeEntity
import com.nmheir.kanicard.data.relations.DeckWithNotesAndTemplates
import com.nmheir.kanicard.data.relations.NoteAndTemplate
import com.nmheir.kanicard.extensions.toSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface DatabaseDao {

    /*Insert*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(searchHistoryEntity: SearchHistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(fsrsCard: FsrsCardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(deck: DeckEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: NoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cardTemplate: CardTemplateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(reviewLog: ReviewLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(noteType: NoteTypeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(fieldDef: FieldDefEntity)

    /*Insert list*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserts(fsrsCards: List<FsrsCardEntity>)


    /*Update*/
    @Update
    fun update(fsrsCard: FsrsCardEntity)

    @Update
    fun update(deck: DeckEntity)

    @Update
    fun update(note: NoteEntity)

    @Update
    fun update(cardTemplate: CardTemplateEntity)

    @Update
    fun update(reviewLog: ReviewLogEntity)

    @Update
    fun update(noteType: NoteTypeEntity)

    /*Delete*/

    @Delete
    fun delete(searchHistoryEntity: SearchHistoryEntity)

    @Delete
    fun delete(deck: DeckEntity)

    @Delete
    fun delete(note: NoteEntity)

    @Delete
    fun delete(cardTemplate: CardTemplateEntity)

    @Delete
    fun delete(reviewLog: ReviewLogEntity)

    @Delete
    fun delete(noteType: NoteTypeEntity)

    @Delete
    fun delete(fieldDef: FieldDefEntity)

    @Query("DELETE FROM notes WHERE noteId = :noteId")
    fun deleteNote(noteId: Long)

    /*Get*/

    @Query(
        """
        SELECT * FROM fsrs_card WHERE deckId = :deckId
    """
    )
    fun getFsrsCardByDeckId(deckId: Long): Flow<List<FsrsCardEntity>?>

    @Query("SELECT * FROM review_log")
    fun getReviewLogs(): Flow<List<ReviewLogEntity>>

    @Query("SELECT * FROM note_types")
    fun getNoteTypes(): Flow<List<NoteTypeEntity>?>

    @Query("SELECT * FROM notes WHERE noteId = :noteId")
    fun getNoteByNoteId(noteId: Long): Flow<NoteEntity?>

    @Query("SELECT * FROM field_defs WHERE noteTypeId = :noteTypeId")
    fun getFieldDefByNoteTypeId(noteTypeId: Long): Flow<List<FieldDefEntity>?>

    @Query("SELECT * FROM card_templates WHERE noteTypeId = :noteTypeId")
    fun getCardTemplateByNoteTypeId(noteTypeId: Long): Flow<CardTemplateEntity?>

    @Query(
        """
    SELECT * FROM fsrs_card 
    WHERE deckId = :deckId 
      AND date(due) = date('now', 'localtime')
    ORDER BY due ASC 
    """
    )
    fun getDueCardsToday(deckId: Long): Flow<List<FsrsCardEntity>?>

    @Query(
        """
        SELECT
          d.id               AS deckId,
          d.name             AS name,
          
          -- Tổng số thẻ ở trạng thái REVIEW
          SUM(CASE WHEN c.state = 'Review' THEN 1 ELSE 0 END)   AS reviewCount,
          
          -- Tổng số thẻ đang học (LEARNING)
          SUM(CASE WHEN c.state = 'Learing' THEN 1 ELSE 0 END) AS learnCount,
          
          -- Tổng số thẻ mới (NEW)
          SUM(CASE WHEN c.state = 'New' THEN 1 ELSE 0 END)      AS newCount,
          
          -- Tổng số thẻ đến hạn trong ngày hôm nay (00:00 → 23:59)
          SUM(
            CASE
              WHEN date(c.due) = date('now', 'localtime')
              THEN 1
              ELSE 0
            END
          )                                                      AS dueToday,
          
          -- Ngày giờ review cuối cùng của cả deck
          MAX(c.lastReview)                                      AS lastReview
        FROM decks d
        LEFT JOIN fsrs_card c
          ON c.deckId = d.id
        GROUP BY d.id, d.name
        ORDER BY d.name
    """
    )
    fun getAllDeckWidgetData(): Flow<List<DeckWidgetData>>


    //This is for Deck Detail Screen
    @Transaction
    @Query("SELECT * FROM decks WHERE id = :deckId")
    fun getDeckWithNoteAndTemplate(deckId: Long): Flow<DeckWithNotesAndTemplates>

    //This is for study case
    @Query("SELECT * FROM notes WHERE noteId IN (:nIds)")
    fun getNoteAndTemplates(nIds: List<Long>): Flow<List<NoteAndTemplate>>

    @Query("SELECT * FROM field_defs WHERE noteTypeId = :noteTypeId")
    fun getFieldDefs(noteTypeId: Long): Flow<List<FieldDefEntity>?>

    @Query("SELECT * FROM card_templates WHERE noteTypeId = :noteTypeId")
    fun getCardTemplate(noteTypeId: Long): Flow<CardTemplateEntity?>

    @RawQuery
    fun raw(supportSQLiteQuery: SupportSQLiteQuery): Int

    fun checkpoint() {
        raw("PRAGMA wal_checkpoint(FULL)".toSQLiteQuery())
    }
}