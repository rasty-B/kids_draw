package com.brett.coloringkids.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.brett.coloringkids.data.ExportRepository
import com.brett.coloringkids.data.ImageRepository
import com.brett.coloringkids.domain.model.Tool
import com.brett.coloringkids.ui.views.DrawingView
import com.brett.coloringkids.util.BitmapUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DrawingViewModel(application: Application) : AndroidViewModel(application) {

    private val imageRepository = ImageRepository(application)
    private val exportRepository = ExportRepository(application)

    private val _brushColor = MutableStateFlow(Color.BLACK)
    val brushColor: StateFlow<Int> = _brushColor

    private val _brushWidth = MutableStateFlow(8f)
    val brushWidth: StateFlow<Float> = _brushWidth

    private val _tool = MutableStateFlow(Tool.PEN)
    val tool: StateFlow<Tool> = _tool

    private val _backgroundBitmap = MutableStateFlow<Bitmap?>(null)
    val backgroundBitmap: StateFlow<Bitmap?> = _backgroundBitmap

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _exportStatus = MutableStateFlow<ExportStatus>(ExportStatus.Idle)
    val exportStatus: StateFlow<ExportStatus> = _exportStatus

    fun setColor(color: Int) {
        _brushColor.value = color
    }

    fun setWidth(width: Float) {
        _brushWidth.value = width
    }

    fun setTool(tool: Tool) {
        _tool.value = tool
    }

    fun importImage(uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    imageRepository.decodeFromUri(uri)
                }
                _backgroundBitmap.value = bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle error - could expose via StateFlow if needed
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun exportMerged(drawingView: DrawingView) {
        viewModelScope.launch {
            _exportStatus.value = ExportStatus.Exporting
            try {
                val merged = withContext(Dispatchers.IO) {
                    BitmapUtils.flatten(drawingView)
                }
                val uri = exportRepository.savePngToPictures(merged)
                _exportStatus.value = if (uri != null) {
                    ExportStatus.Success(uri)
                } else {
                    ExportStatus.Error("Failed to save image")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _exportStatus.value = ExportStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun clearExportStatus() {
        _exportStatus.value = ExportStatus.Idle
    }

    sealed class ExportStatus {
        object Idle : ExportStatus()
        object Exporting : ExportStatus()
        data class Success(val uri: Uri) : ExportStatus()
        data class Error(val message: String) : ExportStatus()
    }
}
