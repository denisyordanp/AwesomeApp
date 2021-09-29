package com.okhome.awesomeapp.module.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = PhotosEntity.TABLE_NAME)
data class PhotosEntity(
    @PrimaryKey
    val id: Int,
    val avgColor: String,
    val photographer: String,
    val photographerUrl: String,
    val srcDefaultSize: String,
    val srcLargeSize: String,
) {
    companion object {
        const val TABLE_NAME = "photos"
    }
}
