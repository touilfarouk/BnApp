package com.farouktouil.farouktouil.core.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_47_48 = object : Migration(47, 48) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS product_accessories (
                productId INTEGER NOT NULL PRIMARY KEY,
                hasMouse INTEGER NOT NULL DEFAULT 0,
                hasKeyboard INTEGER NOT NULL DEFAULT 0,
                hasUps INTEGER NOT NULL DEFAULT 0,
                hasChair INTEGER NOT NULL DEFAULT 0,
                hasDesk INTEGER NOT NULL DEFAULT 0,
                hasPrinter INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(productId) REFERENCES products(productId) ON DELETE CASCADE
            )
            """.trimIndent()
        )
    }
}
