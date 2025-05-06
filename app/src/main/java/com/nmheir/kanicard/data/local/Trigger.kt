package com.nmheir.kanicard.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

val roomTrigger = object : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        // Tạo trigger: sau khi xoá một FsrsCard, xoá luôn Note có noteId = OLD.nId
        db.execSQL(
            """
              CREATE TRIGGER IF NOT EXISTS trg_delete_note_after_card_delete
              AFTER DELETE ON fsrs_card
              FOR EACH ROW
              BEGIN
                DELETE FROM notes WHERE noteId = OLD.nId;
              END;
            """.trimIndent()
        )
    }
}