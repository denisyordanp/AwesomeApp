package com.okhome.awesomeapp.module.local

data class Photo(
    val id: Int,
    val avgColor: String,
    val photographer: String,
    val photographerUrl: String,
    val srcDefaultSize: String,
    val srcLargeSize: String
)
