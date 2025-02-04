package com.nmheir.kanicard.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nmheir.kanicard.data.entities.AccountSessionEntity
import com.nmheir.kanicard.data.entities.DeckEntity
import com.nmheir.kanicard.data.entities.ImportedDeckEntity
import com.nmheir.kanicard.data.entities.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DatabaseDao {

    /*Insert*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(deck: DeckEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(searchHistoryEntity: SearchHistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(accountSessionEntity: AccountSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(importedDeck: ImportedDeckEntity)

    /*Delete*/
    @Delete
    fun delete(accountSessionEntity: AccountSessionEntity)

    @Delete
    fun delete(importedDeck: ImportedDeckEntity)

    @Delete
    fun delete(searchHistoryEntity: SearchHistoryEntity)

    /*Get*/
    @Query("SELECT * FROM imported_decks WHERE userId = :userId")
    fun getImportedDecks(userId: String): Flow<List<DeckEntity>>

    @Query(
        """
        SELECT * FROM IMPORTED_DECKS WHERE userId = :userId AND id = :deckId
    """
    )
    fun getImportedDeckByID(userId: String, deckId: Long): DeckEntity?
}