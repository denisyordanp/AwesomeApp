package com.okhome.awesomeapp.module.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = RemoteKeysEntity.TABLE_NAME)
data class RemoteKeysEntity(
    @PrimaryKey
    val photoId: Long,
    val prevKey: Int?,
    val nextKey: Int?
) {
    companion object {
        const val TABLE_NAME = "remote_keys"
    }
}
