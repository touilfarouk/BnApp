package com.farouktouil.farouktouil.feature.pdfviewer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class PdfViewerViewModel @Inject constructor(
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<PdfViewerState>(PdfViewerState.Loading)
    val uiState: StateFlow<PdfViewerState> = _uiState

    fun downloadPdf(url: String) {
        viewModelScope.launch {
            try {
                val file = downloadFile(url)
                _uiState.value = PdfViewerState.Success(file)
            } catch (e: Exception) {
                _uiState.value = PdfViewerState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    private suspend fun downloadFile(url: String): File {
        val fileName = url.substring(url.lastIndexOf('/') + 1)
        val file = File(context.cacheDir, fileName)

        if (file.exists()) {
            return file
        }

        val urlConnection = URL(url).openConnection()
        urlConnection.connect()

        val inputStream = urlConnection.getInputStream()
        val outputStream = FileOutputStream(file)

        inputStream.use {
            it.copyTo(outputStream)
        }

        outputStream.close()
        return file
    }
}

sealed class PdfViewerState {
    object Loading : PdfViewerState()
    data class Success(val file: File) : PdfViewerState()
    data class Error(val message: String) : PdfViewerState()
}
