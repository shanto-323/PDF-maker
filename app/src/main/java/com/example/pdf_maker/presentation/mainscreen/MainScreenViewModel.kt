package com.example.pdf_maker.presentation.mainscreen

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pdf_maker.domain.repository.Repository
import com.example.pdf_maker.presentation.mainscreen.state.Event
import com.example.pdf_maker.presentation.mainscreen.state.State
import com.example.pdf_maker.utils.RepoResponse
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
  private val context: Context,
  private val repository: Repository
) : ViewModel() {

  private val _scanState = MutableStateFlow(State())
  val scanState: StateFlow<State> = _scanState

  fun onEvent(event: Event) {
    when (event) {
      Event.Download -> {
        savePdf()
      }

      Event.GetImage -> {
        addImage()
      }

      Event.SaveDraft -> {}
      is Event.PdfUriChanged -> {
        _scanState.value = _scanState.value.copy(
          pdfUri = event.pdfUri
        )
      }
    }
  }

  private fun addImage() {
    val bitmap = scanState.value.pdfUri?.let { getBitmapFromUri(context, it) }
    bitmap?.let {
      _scanState.value = _scanState.value.copy(bitmaps = scanState.value.bitmaps + it)
    }
  }

  fun removeImage(bitmap: Bitmap) {
    _scanState.value = _scanState.value.copy(
      bitmaps = scanState.value.bitmaps.filter { it != bitmap }
    )
  }

  private val _scanResult = MutableStateFlow<RepoResponse<IntentSender>>(RepoResponse.Loading)
  val scanResult: StateFlow<RepoResponse<IntentSender>> = _scanResult

  fun repoGetScannerIntent(activity: Activity) {
    _scanResult.value = RepoResponse.Loading
    _scanResult.value = repository.getScanIntent(activity = activity)
  }

  fun handleScanResult(result: ActivityResult) {
    if (result.resultCode == Activity.RESULT_OK) {
      val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
      val uri = scanResult?.pages?.mapNotNull { it.imageUri } ?: emptyList()
      val bitmap = getBitmapFromUri(context, uri[0])
      bitmap?.let {
        _scanState.value = _scanState.value.copy(bitmaps = scanState.value.bitmaps + bitmap)
      }
    }
  }

  private val _savePdf = MutableStateFlow<RepoResponse<Boolean>>(RepoResponse.Loading)
  val savePdf: StateFlow<RepoResponse<Boolean>> = _savePdf

  private fun savePdf() {
    _savePdf.value = RepoResponse.Loading
    val bitmaps = scanState.value.bitmaps
    if (bitmaps.isNotEmpty()) {
      viewModelScope.launch {
        _savePdf.value = repository.convertToPdf(scanState.value.bitmaps)
      }
    } else {
      _savePdf.value = RepoResponse.Error(Exception("file is empty"))
    }
  }


  private fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return try {
      val inputStream = context.contentResolver.openInputStream(uri)
      BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
      null
    }
  }
}


