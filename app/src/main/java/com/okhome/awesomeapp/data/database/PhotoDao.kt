package com.okhome.awesomeapp.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.okhome.awesomeapp.module.database.PhotosEntity

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(list: List<PhotosEntity>)

    @Query("SELECT * FROM ${PhotosEntity.TABLE_NAME}")
    fun getPhotos(): PagingSource<Int, PhotosEntity>

    @Query("SELECT * FROM ${PhotosEntity.TABLE_NAME} WHERE id = :id")
    suspend fun getPhotoById(id: Int): PhotosEntity?
}