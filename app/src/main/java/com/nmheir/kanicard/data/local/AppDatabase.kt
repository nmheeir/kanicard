package com.nmheir.kanicard.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.nmheir.kanicard.data.converters.Converters
import com.nmheir.kanicard.data.entities.SearchHistoryEntity
import com.nmheir.kanicard.data.entities.card.CardTemplateEntity
import com.nmheir.kanicard.data.entities.deck.DeckConfigEntity
import com.nmheir.kanicard.data.entities.deck.DeckEntity
import com.nmheir.kanicard.data.entities.fsrs.FsrsCardEntity
import com.nmheir.kanicard.data.entities.fsrs.ReviewLogEntity
import com.nmheir.kanicard.data.entities.note.FieldDefEntity
import com.nmheir.kanicard.data.entities.note.NoteEntity
import com.nmheir.kanicard.data.entities.note.NoteTypeEntity

class KaniDatabase(
    private val delegate: InternalDatabase
) : DatabaseDao by delegate.dao {
    val openHelper: SupportSQLiteOpenHelper
        get() = delegate.openHelper

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
        SearchHistoryEntity::class,
        FsrsCardEntity::class,
        ReviewLogEntity::class,
        DeckEntity::class,
        NoteEntity::class,
        NoteTypeEntity::class,
        CardTemplateEntity::class,
        FieldDefEntity::class,
        DeckConfigEntity::class
    ],
    version = 2,
    exportSchema = true,
    autoMigrations = [
    ]
)
@TypeConverters(Converters::class)
abstract class InternalDatabase : RoomDatabase() {
    abstract val dao: DatabaseDao

    companion object {
        const val DB_NAME = "kanicard.db"

        fun newInstance(context: Context) =
            KaniDatabase(
                delegate = Room.databaseBuilder(context, InternalDatabase::class.java, DB_NAME)
                    .addCallback(roomTrigger)
                    .fallbackToDestructiveMigration()
                    .build()
            )
    }
}