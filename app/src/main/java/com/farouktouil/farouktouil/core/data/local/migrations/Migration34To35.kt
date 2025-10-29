package com.farouktouil.farouktouil.core.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 34 to 35.
 * Handles schema changes and ensures all tables are properly created.
 */
val MIGRATION_34_35 = object : Migration(34, 35) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Recreate the consultation_remote_keys table to ensure it's in the correct state
        database.execSQL("DROP TABLE IF EXISTS `consultation_remote_keys`")
        
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
        
        // Create appel_consultation table if it doesn't exist
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `appel_consultation` (
                `id` INTEGER NOT NULL, 
                `nom_appel_consultation` TEXT, 
                `date_depot` TEXT, 
                `cle_appel_consultation` TEXT, 
                `lastUpdated` INTEGER NOT NULL, 
                PRIMARY KEY(`id`)
            )
        """.trimIndent())
        
        // Create index for appel_consultation
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_appel_consultation_cle` ON `appel_consultation` (`cle_appel_consultation`)"
        )
    }
}
