package com.farouktouil.farouktouil.core.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_43_44 = object : Migration(43, 44) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
                CREATE TABLE IF NOT EXISTS news_articles (
                    id INTEGER NOT NULL PRIMARY KEY,
                    rubrique TEXT NOT NULL,
                    title TEXT NOT NULL,
                    title_ar TEXT NOT NULL,
                    published_date TEXT NOT NULL,
                    picture_url TEXT,
                    last_updated INTEGER NOT NULL
                )
            """.trimIndent()
        )

        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_news_articles_title ON news_articles(title)"
        )

        database.execSQL(
            """
                CREATE TABLE IF NOT EXISTS news_remote_keys (
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    newsId INTEGER NOT NULL,
                    prevKey INTEGER,
                    nextKey INTEGER,
                    query TEXT
                )
            """.trimIndent()
        )

        database.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS index_news_remote_keys_newsId ON news_remote_keys(newsId)"
        )
    }
}
