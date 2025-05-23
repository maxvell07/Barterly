package com.example.barterly.di

import android.app.Application
import com.example.barterly.presentation.viewmodel.FirebaseViewModel
import com.example.barterly.data.source.service.FileRepository
import com.example.barterly.data.source.service.RetrofitClient

class BarterlyApp : Application() {
    // Инициализируем репозиторий один раз для всего приложения
    val fileRepository: FileRepository by lazy {
        FileRepository(RetrofitClient.fileService)
    }

    // Инициализируем ViewModel с репозиторием
    val firebaseViewModel: FirebaseViewModel by lazy {
        FirebaseViewModel(fileRepository)
    }
}