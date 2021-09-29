package com.okhome.awesomeapp.module.remote


import com.google.gson.annotations.SerializedName

data class ResponsePhotoData(
    @SerializedName("next_page")
    val nextPage: String,
    @SerializedName("page")
    val page: Int,
    @SerializedName("per_page")
    val perPage: Int,
    @SerializedName("photos")
    val photos: List<ResponsePhoto>,
    @SerializedName("total_results")
    val totalResults: Int
)