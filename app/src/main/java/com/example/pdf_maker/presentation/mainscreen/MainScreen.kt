package com.example.pdf_maker.presentation.mainscreen

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pdf_maker.presentation.mainscreen.state.Event
import com.example.pdf_maker.utils.RepoResponse


@Composable
fun MainScreen(
  modifier: Modifier = Modifier,
  viewModel: MainScreenViewModel = hiltViewModel()
) {
  val context = LocalContext.current
  val state by viewModel.scanState.collectAsState()
  val scanResult by viewModel.scanResult.collectAsState()
  val savePdf by viewModel.savePdf.collectAsState()

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
      uri?.let {
        viewModel.onEvent(Event.PdfUriChanged(pdfUri = uri))
      }
    }
  )

  LaunchedEffect(state.pdfUri) {
    Log.d("TAG2", "WORKING")
    viewModel.onEvent(Event.GetImage)
  }

  //Google Scanner
  val scannerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartIntentSenderForResult()
  ) { result ->
    viewModel.handleScanResult(result)
  }
  LaunchedEffect(scanResult) {
    when (scanResult) {
      RepoResponse.Loading -> {
        Log.d("TAG2", "LOADING : Scanning")
      }

      is RepoResponse.Success -> {
        val intentSender = (scanResult as RepoResponse.Success).data
        scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
      }

      is RepoResponse.Error -> {
        val exception = (scanResult as RepoResponse.Error).exception
        Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
      }
    }
  }
  LaunchedEffect(savePdf) {
    when (savePdf) {
      RepoResponse.Loading -> {
        Log.d("TAG2", "LOADING : saving")
      }

      is RepoResponse.Success -> {
        Toast.makeText(context, "Pdf Saved", Toast.LENGTH_SHORT).show()
      }

      is RepoResponse.Error -> {
        val exception = (savePdf as RepoResponse.Error).exception
        Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
      }
    }
  }




  Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    LazyColumn {
      items(state.bitmaps.size) { image ->
        EveryPage(uri = state.bitmaps[image])
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
      }
      item {
        Button(
          onClick = {
            viewModel.repoGetScannerIntent(
              activity = context as Activity
            )
          }
        ) {
          Text("picture")
        }
      }
      item {
        Button(
          onClick = {
            viewModel.onEvent(Event.Download)
          }
        ) {
          Text("Save")
        }
        Spacer(modifier = Modifier.padding(30.dp))
      }
    }
  }
}