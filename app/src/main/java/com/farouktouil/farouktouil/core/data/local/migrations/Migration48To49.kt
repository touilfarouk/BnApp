package com.farouktouil.farouktouil.core.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_48_49 = object : Migration(48, 49) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE OrderEntity ADD COLUMN personnelName TEXT"
        )
        database.execSQL(
            "ALTER TABLE OrderEntity ADD COLUMN productsSummary TEXT NOT NULL DEFAULT ''"
        )
    }
}
