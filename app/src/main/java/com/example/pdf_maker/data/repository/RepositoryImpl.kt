package com.example.pdf_maker.data.repository

import android.app.Activity
import android.content.IntentSender
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.pdf_maker.domain.repository.Repository
import com.example.pdf_maker.utils.RepoResponse
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
  private val gmsDocumentScannerOptions: GmsDocumentScannerOptions,
) : Repository {

  override fun getScanIntent(
    activity: Activity,
  ): RepoResponse<IntentSender> {
    return try {
      val scanner = GmsDocumentScanning.getClient(gmsDocumentScannerOptions)
      val intentSender = scanner.getStartScanIntent(activity).result
      RepoResponse.Success(intentSender)
    } catch (e: Exception) {
      RepoResponse.Error(e)
    }
  }

  override suspend fun convertToPdf(
    uris: List<Bitmap>,
    fileName : String
  ): RepoResponse<Boolean> {
    return try {
      val downloadsDir = Environment
        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
      val pdfFile = File(downloadsDir, "$fileName.pdf")

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
      RepoResponse.Success(true)
    } catch (e: Exception) {
      RepoResponse.Error(e)
    }
  }

}