package com.nmheir.kanicard.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.nmheir.kanicard.core.domain.fsrs.model.ReviewLog
import com.nmheir.kanicard.data.entities.AccountSessionEntity
import com.nmheir.kanicard.data.entities.DownloadedCardEntity
import com.nmheir.kanicard.data.entities.DownloadedDeckEntity
import com.nmheir.kanicard.data.entities.DownloadedDeckWithCards
import com.nmheir.kanicard.data.entities.SearchHistoryEntity
import com.nmheir.kanicard.data.entities.fsrs.FsrsCardEntity
import com.nmheir.kanicard.data.entities.fsrs.ReviewLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DatabaseDao {

    /*Insert*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(searchHistoryEntity: SearchHistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(accountSessionEntity: AccountSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(importedDeck: DownloadedDeckEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(fsrsCard: FsrsCardEntity)

    /*Insert list*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserts(fsrsCards: List<FsrsCardEntity>)


    /*Update*/
    @Upsert
    fun upsert(fsrsCard: FsrsCardEntity)

    @Upsert
    fun upsert(reviewLog: ReviewLogEntity)

    /*Delete*/

    @Delete
    fun delete(accountSessionEntity: AccountSessionEntity)

    @Delete
    fun delete(importedDeck: DownloadedDeckEntity)

    @Delete
    fun delete(searchHistoryEntity: SearchHistoryEntity)

    /*Get*/

    @Query("SELECT * FROM downloaded_decks WHERE userId = :userId")
    fun getDownloadedDecks(userId: String): Flow<List<DownloadedDeckEntity>>


    @Query(
        """
        SELECT * FROM downloaded_decks WHERE userId = :userId AND id = :deckId
    """
    )
    fun getImportedDeckByID(userId: String, deckId: Long): DownloadedDeckEntity?


    @Query(
        """
        SELECT * FROM fsrs_card WHERE deckId = :deckId
    """
    )
    fun getFsrsCardByDeckId(deckId: Long): Flow<List<FsrsCardEntity>?>

    @Query("SELECT * FROM downloaded_cards WHERE deckId = :deckId")
    fun getDownloadedCardByDeckId(deckId: Long): Flow<List<DownloadedCardEntity>?>

    /*Transaction*/
    @Transaction
    @Query(
        """
            SELECT * FROM downloaded_decks WHERE id = :deckId
    """
    )
    fun getDeckWithCards(deckId: Long): Flow<DownloadedDeckWithCards>

    @Query("SELECT * FROM review_log")
    fun getReviewLogs() : Flow<List<ReviewLogEntity>>
}