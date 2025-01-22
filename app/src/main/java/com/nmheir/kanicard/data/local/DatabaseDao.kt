package com.nmheir.kanicard.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.nmheir.kanicard.data.entities.DeckEntity
import com.nmheir.kanicard.data.entities.SearchHistory
import com.nmheir.kanicard.data.entities.UserEntity

@Dao
interface DatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(deck: DeckEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(searchHistory: SearchHistory)
}