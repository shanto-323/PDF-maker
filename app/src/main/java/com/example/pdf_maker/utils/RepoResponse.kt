package com.example.pdf_maker.utils

sealed class RepoResponse<out T> {
    data object Loading : RepoResponse<Nothing>()
    data class Success<out T>(val data: T) : RepoResponse<T>()
    data class Error(val exception: Exception) : RepoResponse<Nothing>()
}