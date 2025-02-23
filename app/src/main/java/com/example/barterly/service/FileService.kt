package com.example.barterly.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface FileService {

    // Загрузка файла (POST /upload/{folderName})
    @Multipart
    @POST("upload/{folderName}")
    suspend fun uploadFile(
        @Path("folderName") folderName: String,
        @Part image: okhttp3.MultipartBody.Part
    ): Response<ResponseBody>

    // Редактирование файла (PUT /upload/{folderName}/{filename})
    @Multipart
    @PUT("upload/{folderName}/{filename}")
    suspend fun updateFile(
        @Path("folderName") folderName: String,
        @Path("filename") filename: String,
        @Part image: okhttp3.MultipartBody.Part
    ): Response<ResponseBody>

    // Удаление файла (DELETE /upload/{folderName}/{filename})
    @DELETE("upload/{folderName}/{filename}")
    suspend fun deleteFile(
        @Path("folderName") folderName: String,
        @Path("filename") filename: String
    ): Response<ResponseBody>

    // Получение файла (GET /images/{folderName}/{filename})
    @GET("images/{folderName}/{filename}")
    @Streaming
    suspend fun getFile(
        @Path("folderName") folderName: String,
        @Path("filename") filename: String
    ): Response<ResponseBody>
}