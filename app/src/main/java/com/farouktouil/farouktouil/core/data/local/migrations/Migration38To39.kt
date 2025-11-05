package com.farouktouil.farouktouil.core.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_38_39 = object : Migration(38, 39) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // The migration is handled by Room's default migration since we're just renaming fields
        // and not changing the table structure significantly
    }
}
