package com.okhome.awesomeapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.okhome.awesomeapp.module.database.PhotosEntity

@Database(
    entities = [PhotosEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PhotoDatabase : RoomDatabase(){

    abstract fun photoDao(): PhotoDao
}