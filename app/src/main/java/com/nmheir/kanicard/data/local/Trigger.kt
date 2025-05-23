package com.nmheir.kanicard.data.local

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nmheir.kanicard.data.entities.option.defaultDeckOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class RoomCallBack @Inject constructor(
    private val dao: Provider<DatabaseDao>
) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        CoroutineScope(Dispatchers.IO).launch {
            dao.get().insert(defaultDeckOption)
        }

        // Tạo trigger: sau khi xoá một FsrsCard, xoá luôn Note có noteId = OLD.nId
        db.execSQL(
            """
              CREATE TRIGGER IF NOT EXISTS trg_delete_note_after_card_delete
              AFTER DELETE ON fsrs_card
              FOR EACH ROW
              BEGIN
                DELETE FROM notes WHERE id = OLD.nId;
              END;
            """.trimIndent()
        )
    }

}

