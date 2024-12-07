package com.example.pdf_maker.presentation.mainscreen.state

import android.net.Uri

sealed class Event {
  data class PdfUriChanged(val pdfUri: Uri) : Event()

  data object SaveDraft : Event()
  data object Download : Event()
  data object GetImage : Event()
}