package com.farouktouil.farouktouil.core.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_39_40 = object : Migration(39, 40) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create document_entity table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS document_entity (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                consultationId INTEGER NOT NULL, 
                year TEXT NOT NULL, 
                fileName TEXT NOT NULL, 
                fileUrl TEXT NOT NULL,
                FOREIGN KEY(consultationId) REFERENCES appel_consultation(cle_appel_consultation) ON DELETE CASCADE
            )
        """.trimIndent())
        
        // Create index on consultationId for better query performance
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_document_entity_consultationId 
            ON document_entity (consultationId)
        """.trimIndent())
    }
}
