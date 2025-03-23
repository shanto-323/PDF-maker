package com.example.pdf_maker.presentation.mainscreen

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage


@Composable
fun EveryPage(
  uri: Bitmap,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(1f / 1.414f)
      .background(Color.White)
      .border(1.dp, Color.Black)
      .padding(16.dp)
      .clickable(onClick = {
        onClick()
      })
  ) {
    AsyncImage(
      model = uri,
      contentDescription = "image",
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.Center),
      contentScale = ContentScale.Fit
    )
  }
}