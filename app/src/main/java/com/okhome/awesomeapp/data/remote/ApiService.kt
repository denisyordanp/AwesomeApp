package com.okhome.awesomeapp.data.remote

import com.okhome.awesomeapp.module.remote.ResponsePhotoData
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("v1/curated")
    suspend fun requestPhotos(
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): ResponsePhotoData
}