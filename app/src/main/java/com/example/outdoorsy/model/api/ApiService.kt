package com.example.outdoorsy.data.api  // ✅ Correct package

import com.example.outdoorsy.model.PostModel
import com.google.android.gms.common.api.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Multipart
import retrofit2.http.Part
import okhttp3.MultipartBody

interface ApiService {
    @GET("posts")
    suspend fun getPosts(): List<PostModel>

    @Multipart
    @POST("upload")
    suspend fun uploadImage(@Part image: MultipartBody.Part): Unit
//    suspend fun uploadImage(@Part image: MultipartBody.Part): Response<Unit>

}
