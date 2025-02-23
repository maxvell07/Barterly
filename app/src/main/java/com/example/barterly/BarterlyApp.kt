package com.example.barterly

import android.app.Application
import com.example.barterly.service.FileRepository
import com.example.barterly.service.RetrofitClient
import com.example.barterly.viewmodel.FirebaseViewModel

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