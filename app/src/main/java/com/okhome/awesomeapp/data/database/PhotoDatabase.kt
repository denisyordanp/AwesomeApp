package com.okhome.awesomeapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.okhome.awesomeapp.module.database.PageEntity
import com.okhome.awesomeapp.module.database.PhotosEntity

@Database(
    entities = [PhotosEntity::class, PageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PhotoDatabase : RoomDatabase(){

    abstract fun photoDao(): PhotoDao
    abstract fun pageDao(): PageDao
}