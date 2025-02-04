package com.nmheir.kanicard.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nmheir.kanicard.data.entities.AccountSessionEntity
import com.nmheir.kanicard.data.entities.DownloadedDeckEntity
import com.nmheir.kanicard.data.entities.SearchHistoryEntity
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
}