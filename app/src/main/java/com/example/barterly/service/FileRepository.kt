package com.example.barterly.service

import com.example.barterly.utils.prepareFilePart
import okhttp3.ResponseBody
import okhttp3.MultipartBody
import retrofit2.Response
import java.io.File

class FileRepository(private val fileService: FileService) {

    suspend fun uploadFile(folderName: String, file: File): Response<ResponseBody> {
        val filePart = prepareFilePart("image", file)
        return fileService.uploadFile(folderName, filePart)
    }

    suspend fun updateFile(folderName: String, filename: String, file: File): Response<ResponseBody> {
        val filePart = prepareFilePart("image", file)
        return fileService.updateFile(folderName, filename, filePart)
    }

    suspend fun deleteFile(folderName: String, filename: String): Response<ResponseBody> {
        return fileService.deleteFile(folderName, filename)
    }

    suspend fun getFile(folderName: String, filename: String): Response<ResponseBody> {
        return fileService.getFile(folderName, filename)
    }
}
