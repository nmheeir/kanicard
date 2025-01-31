package com.nmheir.kanicard.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nmheir.kanicard.data.entities.AccountSession
import com.nmheir.kanicard.data.entities.DeckEntity
import com.nmheir.kanicard.data.entities.SearchHistory
import com.nmheir.kanicard.data.entities.User
import com.nmheir.kanicard.data.entities.UserProfile

@Dao
interface DatabaseDao {

    /*Insert*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(deck: DeckEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(searchHistory: SearchHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(accountSession: AccountSession)


    /*Delete*/
    @Delete
    fun delete(accountSession: AccountSession)

    /*Get*/
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserProfile(userId: String): UserProfile
}