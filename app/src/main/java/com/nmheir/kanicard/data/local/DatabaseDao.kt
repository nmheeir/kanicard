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
import com.nmheir.kanicard.data.dto.card.CardBrowseDto
import com.nmheir.kanicard.data.dto.deck.DeckData
import com.nmheir.kanicard.data.dto.deck.DeckWidgetData
import com.nmheir.kanicard.data.entities.SearchHistoryEntity
import com.nmheir.kanicard.data.entities.card.TemplateEntity
import com.nmheir.kanicard.data.entities.deck.CollectionEntity
import com.nmheir.kanicard.data.entities.deck.DeckEntity
import com.nmheir.kanicard.data.entities.fsrs.FsrsCardEntity
import com.nmheir.kanicard.data.entities.fsrs.ReviewLogEntity
import com.nmheir.kanicard.data.entities.note.FieldEntity
import com.nmheir.kanicard.data.entities.note.NoteEntity
import com.nmheir.kanicard.data.entities.note.NoteTypeEntity
import com.nmheir.kanicard.data.relations.CollectionWithDecks
import com.nmheir.kanicard.data.relations.DeckWithNotesAndTemplates
import com.nmheir.kanicard.data.relations.NoteAndTemplate
import com.nmheir.kanicard.data.relations.NoteTypeWithFieldDefs
import com.nmheir.kanicard.data.relations.NoteTypeWithTemplates
import com.nmheir.kanicard.extensions.toSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface DatabaseDao {

    /*Insert*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(searchHistoryEntity: SearchHistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fsrsCard: FsrsCardEntity)

    @Insert
    suspend fun insert(deck: DeckEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cardTemplate: TemplateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reviewLog: ReviewLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(noteType: NoteTypeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fieldDef: FieldEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(collection: CollectionEntity)

    /*Insert list*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(fsrsCards: List<FsrsCardEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFields(fields: List<FieldEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplates(templates: List<TemplateEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)


    /*Update*/
    @Update
    suspend fun update(fsrsCard: FsrsCardEntity)

    @Update
    suspend fun update(deck: DeckEntity)

    @Update
    suspend fun update(note: NoteEntity)

    @Update
    suspend fun update(cardTemplate: TemplateEntity)

    @Update
    suspend fun update(reviewLog: ReviewLogEntity)

    @Update
    suspend fun update(noteType: NoteTypeEntity)

    @Query(
        """
        UPDATE decks
        SET
            name = COALESCE(:name, name),
            description = COALESCE(:description, description)
        WHERE id = :id
        """
    )
    suspend fun updateDeck(
        id: Long,
        name: String? = null,
        description: String? = null
    )

    /*Delete*/

    @Delete
    fun delete(searchHistoryEntity: SearchHistoryEntity)

    @Delete
    fun delete(deck: DeckEntity)

    @Delete
    fun delete(note: NoteEntity)

    @Delete
    fun delete(cardTemplate: TemplateEntity)

    @Delete
    fun delete(reviewLog: ReviewLogEntity)

    @Delete
    fun delete(noteType: NoteTypeEntity)

    @Delete
    fun delete(fieldDef: FieldEntity)

    @Query("DELETE FROM notes WHERE id = :noteId")
    fun deleteNote(noteId: Long)

    @Query("DELETE FROM decks WHERE id = :id")
    suspend fun deleteDeck(id: Long)

    @Query("DELETE FROM templates WHERE id = :id")
    suspend fun deleteTemplate(id: Long)

    /*Get*/

    @Query(
        """
        SELECT * FROM fsrs_card WHERE dId = :deckId
    """
    )
    fun getFsrsCardByDeckId(deckId: Long): Flow<List<FsrsCardEntity>?>

    @Query("SELECT * FROM review_logs")
    fun getReviewLogs(): Flow<List<ReviewLogEntity>>

    @Query("SELECT * FROM note_types")
    fun getNoteTypes(): Flow<List<NoteTypeEntity>?>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getNoteByNoteId(noteId: Long): Flow<NoteEntity?>

    @Query("SELECT * FROM fields WHERE ntId = :noteTypeId")
    fun getFieldDefByNoteTypeId(noteTypeId: Long): Flow<List<FieldEntity>?>

    @Query("SELECT * FROM templates WHERE ntId = :noteTypeId")
    fun getCardTemplateByNoteTypeId(noteTypeId: Long): Flow<List<TemplateEntity>?>


    @Query(
        """
    SELECT * FROM fsrs_card 
    WHERE dId = :deckId 
      AND date(due) <= date('now', 'localtime')
    ORDER BY due ASC 
    """
    )
    fun getDueCardsToday(deckId: Long): Flow<List<FsrsCardEntity>?>

    //This is for study case
    @Query("SELECT * FROM notes WHERE id IN (:nIds)")
    fun getNoteAndTemplates(nIds: List<Long>): Flow<List<NoteAndTemplate>>

    @Query("SELECT * FROM fields WHERE ntId = :noteTypeId")
    fun getFieldDefs(noteTypeId: Long): Flow<List<FieldEntity>?>

    @Query("SELECT * FROM templates WHERE ntId = :noteTypeId")
    fun getCardTemplate(noteTypeId: Long): Flow<TemplateEntity?>

    /*-------------------------------------------------------------------------*/
    /*Deck*/
    @Query(
        """
            SELECT * FROM decks 
            WHERE (:id IS NULL OR id = :id)
              AND (:name IS NULL OR name = :name)
            LIMIT 1
        """
    )
    suspend fun deck(id: Long?, name: String?): DeckEntity?

    @Query("SELECT * FROM decks")
    fun allDecks(): Flow<List<DeckEntity>?>

    //This is for Deck Detail Screen
    @Transaction
    @Query("SELECT * FROM decks WHERE id = :deckId")
    fun getDeckWithNoteAndTemplate(deckId: Long): Flow<DeckWithNotesAndTemplates>

    @Query(
        """
        SELECT
          d.id               AS deckId,
          d.name             AS name,
          
          -- Tổng số thẻ ở trạng thái REVIEW
          SUM(CASE WHEN c.state = 'Review' THEN 1 ELSE 0 END)   AS reviewCount,
          
          -- Tổng số thẻ đang học (LEARNING)
          SUM(CASE WHEN c.state = 'Learning' THEN 1 ELSE 0 END) AS learnCount,
          
          -- Tổng số thẻ mới (NEW)
          SUM(CASE WHEN c.state = 'New' THEN 1 ELSE 0 END)      AS newCount,
          
          -- Tổng số thẻ đến hạn trong ngày hôm nay (00:00 → 23:59)
          SUM(
            CASE
              WHEN date(c.due) <= date('now', 'localtime')
              THEN 1
              ELSE 0
            END
          )                                                      AS dueToday,
          
          -- Ngày giờ review cuối cùng của cả deck
          MAX(c.lastReview)                                      AS lastReview
        FROM decks d
        LEFT JOIN fsrs_card c
          ON c.dId = d.id
        GROUP BY d.id, d.name
        ORDER BY d.name
    """
    )
    fun getAllDeckWidgetData(): Flow<List<DeckWidgetData>>

    /*End Deck*/
    /*-------------------------------------------------------------------------*/

    /*--------------------------------*/
    /*Collection*/
    @Query("SELECT * FROM collections")
    fun getCollections(): Flow<List<CollectionEntity>>

    @Transaction
    @Query("SELECT * FROM collections")
    fun getAllCollectionsWithDecks(): Flow<List<CollectionWithDecks>>

    /*End Collection*/
    /*-------------------------------------------------------------------------*/

    /*-------------------------------------------------------------------------*/
    /*Note Type*/
    @Query("SELECT * FROM note_types")
    fun getAllNoteTypes(): Flow<List<NoteTypeEntity>?>

    @Transaction
    @Query("SELECT * FROM note_types WHERE id = :id")
    fun getNoteTypesWithTemplates(id: Long): Flow<NoteTypeWithTemplates?>

    @Transaction
    @Query("SELECT * FROM note_types WHERE id = :id")
    fun getNoteTypeWithFieldDef(id: Long): Flow<NoteTypeWithFieldDefs?>

    /*End Note Type*/
    /*-------------------------------------------------------------------------*/

    /*-------------------------------------------------------------------------*/
    /*Note*/

    @Query(
        """
        SELECT
            d.id,
            d.collectionId AS cId,
            d.name,
            d.description,
            d.createdTime,
            d.modifiedTime,
            d.flags,
            COUNT(n.id)    AS noteCount
        FROM decks AS d
        LEFT JOIN notes AS n
            ON d.id = n.dId
        WHERE d.id = :dId
        GROUP BY d.id
    """
    )
    fun getDecksWithNoteCount(dId: Long): Flow<DeckData>

    @Transaction
    @Query("SELECT * FROM notes WHERE dId = :dId LIMIT :limit")
    fun getNoteAndTemplateByDeckId(dId: Long, limit: Int = 100): Flow<List<NoteAndTemplate>>

    @Transaction
    @Query("SELECT * FROM notes WHERE id IN (:nIds)")
    fun getNoteAndTemplateByNoteIds(nIds: List<Long>) : Flow<List<NoteAndTemplate>>

    /*End Note*/
    /*-------------------------------------------------------------------------*/

    /*-------------------------------------------------------------------------*/
    /*Card*/

    @Query(
        """
            SELECT
              n.id                       AS nid,
              t.id                           AS tId,
              d.name                         AS dName,
              nt.name                        AS typeName,
              t.name                         AS templateName,
              t.qstFt                        AS qfmt,
              t.ansFt                        AS afmt,
              n.fieldJson                    AS field,
              f.lapses                       AS lapse,
              f.state                        AS state,
              COALESCE(r.review_count, 0)    AS reviews,
              f.due                          AS due,
              n.createdTime                  AS createdTime,
              n.modifiedTime                 AS modifiedTime
            FROM notes AS n
            INNER JOIN decks AS d
              ON n.dId = d.id
            INNER JOIN templates AS t
              ON n.templateId = t.id
            INNER JOIN note_types AS nt
              ON t.ntId = nt.id
            INNER JOIN fsrs_card AS f
              ON f.nId = n.id
            LEFT JOIN (
              /* Đếm số lần review cho mỗi card */
              SELECT
                nId,
                COUNT(*) AS review_count
              FROM review_logs
              GROUP BY nId
            ) AS r
              ON r.nId = f.nId
            WHERE d.id = :dId
            ;
        """
    )
    // TODO: I don't know how to name function
    fun getBrowseCards(dId: Long): Flow<List<CardBrowseDto>>
    /*End Card*/
    /*-------------------------------------------------------------------------*/


    @RawQuery
    fun raw(supportSQLiteQuery: SupportSQLiteQuery): Int

    fun checkpoint() {
        raw("PRAGMA wal_checkpoint(FULL)".toSQLiteQuery())
    }
}