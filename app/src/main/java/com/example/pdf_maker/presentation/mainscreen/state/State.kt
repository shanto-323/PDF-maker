package com.example.pdf_maker.presentation.mainscreen.state

import android.graphics.Bitmap
import android.net.Uri

data class State(
  val bitmaps: List<Bitmap> = emptyList(),
  val pdfUri : Uri? = null
)