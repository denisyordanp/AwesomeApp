package com.okhome.awesomeapp.module.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = PageEntity.TABLE_NAME)
data class PageEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    val isPrevPageAvailable: Boolean,
    val currentPage: Int,
    val isNextPageAvailable: Boolean
) {
    companion object {
        const val TABLE_NAME = "page"
    }
}
