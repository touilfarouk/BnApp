package com.farouktouil.farouktouil.core.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_41_42 = object : Migration(41, 42) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE document_entity ADD COLUMN localFilePath TEXT")
        database.execSQL("ALTER TABLE document_entity ADD COLUMN fileSize INTEGER")
        database.execSQL("ALTER TABLE document_entity ADD COLUMN lastUpdated INTEGER NOT NULL DEFAULT 0")

        database.execSQL(
            "DELETE FROM document_entity WHERE rowid NOT IN (" +
                "SELECT MIN(rowid) FROM document_entity GROUP BY consultationId, fileUrl" +
                ")"
        )

        database.execSQL(
            "UPDATE document_entity SET lastUpdated = strftime('%s','now') * 1000 " +
                "WHERE lastUpdated = 0"
        )

        database.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS index_document_entity_consultationId_fileUrl " +
                "ON document_entity (consultationId, fileUrl)"
        )
    }
}
