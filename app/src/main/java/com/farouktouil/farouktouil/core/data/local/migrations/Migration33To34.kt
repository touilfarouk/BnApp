package com.farouktouil.farouktouil.core.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 33 to 34.
 * Adds consultation_remote_keys table for pagination support in the consultation feature.
 */
val MIGRATION_33_34 = object : Migration(33, 34) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create consultation_remote_keys table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `consultation_remote_keys` (
                `id` TEXT NOT NULL, 
                `prevKey` INTEGER, 
                `nextKey` INTEGER, 
                `query` TEXT, 
                PRIMARY KEY(`id`)
            )
        """.trimIndent())
        
        // Create index for better query performance
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_consultation_remote_keys_id` ON `consultation_remote_keys` (`id`)"
        )
    }
}
