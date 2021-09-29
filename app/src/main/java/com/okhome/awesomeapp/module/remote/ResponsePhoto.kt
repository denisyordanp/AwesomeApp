package com.okhome.awesomeapp.module.remote


import com.google.gson.annotations.SerializedName

data class ResponsePhoto(
    @SerializedName("avg_color")
    val avgColor: String,
    @SerializedName("height")
    val height: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("photographer")
    val photographer: String,
    @SerializedName("photographer_id")
    val photographerId: Int,
    @SerializedName("photographer_url")
    val photographerUrl: String,
    @SerializedName("src")
    val sources: Sources,
    @SerializedName("url")
    val url: String,
    @SerializedName("width")
    val width: Int
)