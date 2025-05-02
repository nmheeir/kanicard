package com.nmheir.kanicard.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
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
    fun delete(searchHistoryEntity: SearchHistoryEntity)

    /*Get*/

    @Query(
        """
        SELECT * FROM fsrs_card WHERE deckId = :deckId
    """
    )
    fun getFsrsCardByDeckId(deckId: Long): Flow<List<FsrsCardEntity>?>

    @Query("SELECT * FROM review_log")
    fun getReviewLogs() : Flow<List<ReviewLogEntity>>
}