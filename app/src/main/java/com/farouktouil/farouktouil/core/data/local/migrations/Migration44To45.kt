package com.farouktouil.farouktouil.core.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_44_45 = object : Migration(44, 45) {
    override fun migrate(database: SupportSQLiteDatabase) {
        val existingTablesCursor = database.query(
            "SELECT name FROM sqlite_master WHERE type='table' AND name='DelivererEntity'"
        )
        existingTablesCursor.use { cursor ->
            if (cursor.moveToFirst()) {
                database.execSQL("ALTER TABLE DelivererEntity RENAME TO structures")
            }
        }

        database.execSQL(
            """
                CREATE TABLE IF NOT EXISTS products_new (
                    productId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    label TEXT NOT NULL,
                    pricePerAmount REAL NOT NULL,
                    quantity INTEGER NOT NULL,
                    minQuantity INTEGER NOT NULL,
                    maxQuantity INTEGER NOT NULL,
                    belongsToDeliverer INTEGER NOT NULL,
                    assignedPersonnelId INTEGER,
                    barcode TEXT NOT NULL,
                    FOREIGN KEY(belongsToDeliverer) REFERENCES structures(delivererId) ON UPDATE NO ACTION ON DELETE CASCADE,
                    FOREIGN KEY(assignedPersonnelId) REFERENCES personnel(id) ON UPDATE NO ACTION ON DELETE SET NULL
                )
            """.trimIndent()
        )

        database.execSQL(
            """
                INSERT INTO products_new (
                    productId,
                    name,
                    label,
                    pricePerAmount,
                    quantity,
                    minQuantity,
                    maxQuantity,
                    belongsToDeliverer,
                    assignedPersonnelId,
                    barcode
                )
                SELECT
                    productId,
                    name,
                    label,
                    pricePerAmount,
                    quantity,
                    minQuantity,
                    maxQuantity,
                    belongsToDeliverer,
                    NULL,
                    barcode
                FROM products
            """.trimIndent()
        )

        database.execSQL("DROP TABLE products")
        database.execSQL("ALTER TABLE products_new RENAME TO products")

        database.execSQL("CREATE INDEX IF NOT EXISTS index_products_belongsToDeliverer ON products(belongsToDeliverer)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_products_assignedPersonnelId ON products(assignedPersonnelId)")
    }
}
