package com.okhome.awesomeapp.module.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.okhome.awesomeapp.module.local.Photo

@Entity(tableName = PhotosEntity.TABLE_NAME)
data class PhotosEntity(
    @PrimaryKey
    val id: Long,
    val avgColor: String,
    val photographer: String,
    val photographerUrl: String,
    val srcDefaultSize: String,
    val srcLargeSize: String
) {
    companion object {
        const val TABLE_NAME = "photos"
    }

    fun generateToLocal(): Photo {
        return Photo(
            id, avgColor, photographer, photographerUrl, srcDefaultSize, srcLargeSize
        )
    }
}
