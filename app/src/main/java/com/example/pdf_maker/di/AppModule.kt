package com.example.pdf_maker.di

import android.content.Context
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  @Provides
  @Singleton
  fun provideGmsDocumentScannerOptions(): GmsDocumentScannerOptions {
    return GmsDocumentScannerOptions.Builder()
      .setGalleryImportAllowed(false)
      .setPageLimit(1)
      .setResultFormats(
        GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
      )
      .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
      .build()
  }

  @Provides
  @Singleton
  fun proVideContext(@ApplicationContext context: Context): Context {
    return context
  }
}