package com.farouktouil.farouktouil.core.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_40_41 = object : Migration(40, 41) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 1. Rename the old table to a temporary name
        database.execSQL("ALTER TABLE appel_consultation RENAME TO appel_consultation_old")
        
        // 2. Create the new table with the updated schema
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS appel_consultation (
                id INTEGER PRIMARY KEY NOT NULL,
                title TEXT NOT NULL,
                depositDate TEXT NOT NULL,
                dayOfWeek TEXT NOT NULL,
                tenderNumber INTEGER NOT NULL,
                lastUpdated INTEGER NOT NULL
            )
        """.trimIndent())
        
        // 3. Copy data from the old table to the new table
        database.execSQL("""
            INSERT INTO appel_consultation (id, title, depositDate, dayOfWeek, tenderNumber, lastUpdated)
            SELECT 
                cle_appel_consultation as id,
                nom_appel_consultation as title,
                date_depot as depositDate,
                jour_semaine as dayOfWeek,
                num_ao as tenderNumber,
                lastUpdated
            FROM appel_consultation_old
        """.trimIndent())
        
        // 4. Drop the old table
        database.execSQL("DROP TABLE appel_consultation_old")
        
        // 5. Recreate indices
        database.execSQL("CREATE INDEX IF NOT EXISTS index_appel_consultation_title ON appel_consultation (title)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_appel_consultation_depositDate ON appel_consultation (depositDate)")
        
        // 6. Update the foreign key in document_entity
        database.execSQL("DROP INDEX IF EXISTS index_document_entity_consultationId")
        database.execSQL("""
            CREATE TABLE document_entity_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                consultationId INTEGER NOT NULL, 
                year TEXT NOT NULL, 
                fileName TEXT NOT NULL, 
                fileUrl TEXT NOT NULL,
                FOREIGN KEY(consultationId) REFERENCES appel_consultation(id) ON DELETE CASCADE
            )
        """.trimIndent())
        
        database.execSQL("""
            INSERT INTO document_entity_new (id, consultationId, year, fileName, fileUrl)
            SELECT id, consultationId, year, fileName, fileUrl FROM document_entity
        """.trimIndent())
        
        database.execSQL("DROP TABLE document_entity")
        database.execSQL("ALTER TABLE document_entity_new RENAME TO document_entity")
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_document_entity_consultationId 
            ON document_entity (consultationId)
        """.trimIndent())
    }
}
