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
import com.example.pdf_maker.presentation.mainscreen.state.State
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
  private val gmsDocumentScannerOptions: GmsDocumentScannerOptions,
  private val context: Context
) : ViewModel() {

  private val _scanState = MutableStateFlow(State())
  val scanState: StateFlow<State> = _scanState

  fun addImage(imageUri: Uri) {
    val bitmap = getBitmapFromUri(context, imageUri)
    _scanState.value = _scanState.value.copy(uris = scanState.value.uris + bitmap!!)
  }

  fun getScanIntent(
    activity: Activity,
    onFailure: (Exception) -> Unit,
    onSuccess: (IntentSender) -> Unit
  ) {
    val scanner = GmsDocumentScanning.getClient(gmsDocumentScannerOptions)
    scanner.getStartScanIntent(activity)
      .addOnSuccessListener { intentSender ->
        onSuccess(intentSender)
      }
      .addOnFailureListener { exception ->
        onFailure(exception)
      }
  }

  fun handleScanResult(result: ActivityResult) {
    if (result.resultCode == Activity.RESULT_OK) {
      val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
      val uri = scanResult?.pages?.mapNotNull { it.imageUri } ?: emptyList()
      val bitmap = getBitmapFromUri(context, uri[0])
      _scanState.value = _scanState.value.copy(uris = scanState.value.uris + bitmap!!)

    } else {
      Log.d("TAG2", "Error")
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


  fun convertUrisToPdf(
    uris: List<Bitmap>,
    onComplete: (String) -> Unit,
    onError: (String) -> Unit
  ) {
    viewModelScope.launch {
      try {
        // Create a file in the Downloads directory
        val downloadsDir =
          Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val pdfFile = File(downloadsDir, "converted_document.pdf")

        val pdfDocument = PdfDocument()

        uris.forEachIndexed { index, bitmap ->
          if (bitmap != null) {
            val pageInfo =
              PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, index + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            pdfDocument.finishPage(page)
          }
        }

        FileOutputStream(pdfFile).use { fos ->
          pdfDocument.writeTo(fos)
        }

        pdfDocument.close()

        // Notify completion
        onComplete("PDF saved to ${pdfFile.absolutePath}")
      } catch (e: Exception) {
        onError("Failed to save PDF: ${e.localizedMessage}")
      }
    }
  }
}


