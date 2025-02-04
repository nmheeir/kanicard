package com.nmheir.kanicard.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nmheir.kanicard.data.entities.AccountSessionEntity
import com.nmheir.kanicard.data.entities.DeckEntity
import com.nmheir.kanicard.data.entities.ImportedDeckEntity
import com.nmheir.kanicard.data.entities.ProfileEntity
import com.nmheir.kanicard.data.entities.SearchHistoryEntity

class KaniDatabase(
    private val delegate: InternalDatabase
) : DatabaseDao by delegate.dao {

    fun query(block: KaniDatabase.() -> Unit) = with(delegate) {
        queryExecutor.execute {
            block(this@KaniDatabase)
        }
    }

    fun transaction(block: KaniDatabase.() -> Unit) = with(delegate) {
        transactionExecutor.execute {
            runInTransaction {
                block(this@KaniDatabase)
            }
        }
    }

    fun close() = delegate.close()
}

@Database(
    entities = [
        DeckEntity::class, SearchHistoryEntity::class, AccountSessionEntity::class, ProfileEntity::class,
        ImportedDeckEntity::class
    ],
    version = 4,
    exportSchema = true,
    autoMigrations = [

    ]
)
@TypeConverters(Converters::class)
abstract class InternalDatabase : RoomDatabase() {
    abstract val dao: DatabaseDao

    companion object {
        private const val DB_NAME = "kanicard.db"

        fun newInstance(context: Context) =
            KaniDatabase(
                delegate = Room.databaseBuilder(context, InternalDatabase::class.java, DB_NAME)
                    .build()
            )
    }
}