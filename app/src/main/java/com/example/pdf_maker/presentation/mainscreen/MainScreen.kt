package com.example.pdf_maker.presentation.mainscreen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter


// Working now sat everything  up and running

@Composable
fun MainScreen(
  modifier: Modifier = Modifier,
  viewModel: MainScreenViewModel = hiltViewModel()
) {
  val context = LocalContext.current
  var imageUri by remember { mutableStateOf<Uri?>(null) }
  val state by viewModel.scanState.collectAsState()

  // Check Permission
  val permissionState = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
    onResult = { isGranted ->
      if (isGranted) {

      } else {
        Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
      }
    }
  )
  LaunchedEffect(Unit) {
    val permission = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.READ_EXTERNAL_STORAGE
    )
    if (permission != PackageManager.PERMISSION_GRANTED) {
      permissionState.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
  }

  //Launch this for selecting local image
  val imagePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent(),
    onResult = { uri: Uri? ->
      viewModel.addImage(uri!!)
    }
  )

  //Google Scanner
  val scannerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartIntentSenderForResult()
  ) { result ->
    viewModel.handleScanResult(result)
  }

  Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    LazyColumn {
      items(state.uris.size) { image ->
        A4Page(uri = state.uris[image])
//        AsyncImage(model = state.uris[image], contentDescription = "image")
      }


      item {
        Text(text = "No image selected", modifier = Modifier.padding(16.dp))
        Button(
          onClick = {
            imagePickerLauncher.launch("image/*")
          }
        ) {
          Text("Select Image")
        }
        Button(
          onClick = {
            viewModel.getScanIntent(
              activity = context as Activity,
              onFailure = { exception ->
                Toast.makeText(context, exception.message.toString(), Toast.LENGTH_SHORT).show()
              },
              onSuccess = {
                scannerLauncher.launch(IntentSenderRequest.Builder(it).build())
              }
            )
          }
        ) {
          Text("picture")
        }
        Button(
          onClick = {
            if (viewModel.scanState.value.uris.isNotEmpty()) {
              viewModel.convertUrisToPdf(
                uris = viewModel.scanState.value.uris,
                onComplete = {
                  Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }, onError = {
                  Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
              )
            }else{
              Toast.makeText(context, "No images to convert", Toast.LENGTH_SHORT).show()
            }
          }
        ) {
          Text("Save")
        }
        Spacer(modifier = Modifier.padding(30.dp))
      }
    }

  }

}


@Composable
fun A4Page(uri: Bitmap) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(1f / 1.414f)
      .background(Color.White)
      .border(1.dp, Color.Black)
      .padding(16.dp)
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