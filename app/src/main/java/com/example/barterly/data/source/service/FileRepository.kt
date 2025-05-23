package com.example.barterly.data.source.service

import com.example.barterly.util.prepareFilePart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File

class FileRepository(private val fileService: FileService) {

    suspend fun uploadFile(folderName: String, file: File): Response<ResponseBody> {
        val filePart = prepareFilePart("image", file)
        return fileService.uploadFile(folderName, filePart)
    }

//    suspend fun updateFile(folderName: String, filename: String, file: File): Response<ResponseBody> {
//        val filePart = prepareFilePart("image", file)
//        return fileService.updateFile(folderName, filename, filePart)
//    }
//
//    suspend fun deleteFile(folderName: String, filename: String): Response<ResponseBody> {
//        return fileService.deleteFile(folderName, filename)
//    }
//
//    suspend fun getFile(folderName: String, filename: String): Response<ResponseBody> {
//        return fileService.getFile(folderName, filename)
//    }

    suspend fun deleteAllFiles(folderName: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<ResponseBody> = RetrofitClient.fileService.deleteAllFilesInFolder(folderName)
                if (response.isSuccessful) {
                    response.body()?.string() ?: "Успешно, но без ответа"
                } else {
                    "Ошибка: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                "Ошибка сети: ${e.message}"
            }
        }
    }

}
