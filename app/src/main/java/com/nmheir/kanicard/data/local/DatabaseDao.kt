package com.nmheir.kanicard.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.nmheir.kanicard.data.dto.deck.DeckWidgetData
import com.nmheir.kanicard.data.entities.SearchHistoryEntity
import com.nmheir.kanicard.data.entities.card.CardTemplateEntity
import com.nmheir.kanicard.data.entities.fsrs.FsrsCardEntity
import com.nmheir.kanicard.data.entities.fsrs.ReviewLogEntity
import com.nmheir.kanicard.data.entities.note.FieldDefEntity
import com.nmheir.kanicard.data.entities.note.NoteEntity
import com.nmheir.kanicard.data.entities.note.NoteTypeEntity
import com.nmheir.kanicard.data.relations.DeckWithCards
import kotlinx.coroutines.flow.Flow

@Dao
interface DatabaseDao {

    /*Insert*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(searchHistoryEntity: SearchHistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(fsrsCard: FsrsCardEntity)

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
    @Upsert
    fun upsert(fsrsCard: FsrsCardEntity)

    /*Delete*/

    @Delete
    fun delete(searchHistoryEntity: SearchHistoryEntity)

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
    fun getDueCard(deckId: Long): Flow<List<FsrsCardEntity>?>

    @Query("""
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
    """)
    fun getAllDeckWidgetData(): Flow<List<DeckWidgetData>>

    
}