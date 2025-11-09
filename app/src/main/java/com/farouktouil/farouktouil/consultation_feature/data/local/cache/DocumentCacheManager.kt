package com.farouktouil.farouktouil.consultation_feature.data.local.cache

import android.content.Context
import android.net.Uri
import android.util.Log
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.DocumentEntity
import com.farouktouil.farouktouil.consultation_feature.data.remote.dto.DocumentDto
import com.farouktouil.farouktouil.core.di.ConsultationApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentCacheManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @ConsultationApi private val okHttpClient: OkHttpClient
) {

    companion object {
        private const val TAG = "DocumentCacheManager"
        private const val DIRECTORY_NAME = "consultation_documents"
        private const val READ_TIMEOUT_SECONDS = 60L
        private const val CONNECT_TIMEOUT_SECONDS = 30L
    }

    private val storageDir: File by lazy {
        File(context.filesDir, DIRECTORY_NAME).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    private val client: OkHttpClient by lazy {
        okHttpClient.newBuilder()
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    suspend fun prepareDocuments(
        consultationId: Int,
        documents: List<DocumentDto>,
        existingDocuments: List<DocumentEntity>
    ): List<DocumentEntity> {
        if (documents.isEmpty()) return emptyList()

        val existingMap = existingDocuments.associateBy { it.fileUrl }
        return documents.map { documentDto ->
            val existing = existingMap[documentDto.fileUrl]
            val cachedFile = existing?.localFilePath?.let { path ->
                val file = File(path)
                if (file.exists()) file else null
            }

            val resolvedFile = cachedFile ?: downloadDocument(documentDto, consultationId)
            val fileSize = resolvedFile?.takeIf { it.exists() }?.length()

            DocumentEntity(
                id = existing?.id ?: 0,
                consultationId = consultationId,
                year = documentDto.year,
                fileName = documentDto.fileName,
                fileUrl = documentDto.fileUrl,
                localFilePath = resolvedFile?.absolutePath,
                fileSize = fileSize,
                lastUpdated = System.currentTimeMillis()
            )
        }
    }

    private suspend fun downloadDocument(documentDto: DocumentDto, consultationId: Int): File? {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url(documentDto.fileUrl)
                .header("Accept", "application/pdf")
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.w(TAG, "Download failed for ${documentDto.fileUrl}: ${response.code}")
                        return@withContext null
                    }

                    val body = response.body ?: return@withContext null
                    val fileName = buildFileName(consultationId, documentDto)
                    val destination = File(storageDir, fileName)

                    destination.outputStream().use { output ->
                        body.byteStream().use { input ->
                            input.copyTo(output)
                        }
                    }

                    destination
                }
            } catch (io: IOException) {
                Log.w(TAG, "Download error for ${documentDto.fileUrl}: ${io.message}", io)
                null
            }
        }
    }

    private fun buildFileName(consultationId: Int, documentDto: DocumentDto): String {
        val baseName = sanitizeFileName(
            documentDto.fileName.ifBlank {
                Uri.parse(documentDto.fileUrl).lastPathSegment ?: "document_$consultationId"
            }
        )
        return "${consultationId}_$baseName"
    }

    private fun sanitizeFileName(raw: String): String {
        return raw.replace(Regex("[^a-zA-Z0-9._-]"), "_")
    }
}
