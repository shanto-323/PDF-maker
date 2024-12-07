package com.example.pdf_maker.domain.repository

import android.app.Activity
import android.content.IntentSender
import android.graphics.Bitmap
import com.example.pdf_maker.utils.RepoResponse

interface Repository {
  fun getScanIntent(activity: Activity) : RepoResponse<IntentSender>

   suspend fun convertToPdf(bitmaps: List<Bitmap>,fileName : String = "converted_document") : RepoResponse<Boolean>
}