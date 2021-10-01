package com.okhome.awesomeapp.data.repository

import androidx.paging.*
import com.okhome.awesomeapp.data.database.PhotoDatabase
import com.okhome.awesomeapp.data.remote.ApiService
import com.okhome.awesomeapp.module.database.PhotosEntity
import com.okhome.awesomeapp.module.local.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ExperimentalPagingApi
class PhotosRepository @Inject constructor(
    private val database: PhotoDatabase,
    private val service: ApiService
) {

    suspend fun getPhotoById(id: Long): PhotosEntity? {
        return database.photoDao().getPhotoById(id)
    }

    fun getPhotosStream(): Flow<PagingData<Photo>> {

        val pagingSourceFactory = { database.photoDao().getPhotos() }

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = PhotosRemoteMediator(
                service,
                database
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.toLocal()
    }

    private fun Flow<PagingData<PhotosEntity>>.toLocal(): Flow<PagingData<Photo>> {
        return this.map { paging ->
            paging.map {
                it.generateToLocal()
            }
        }
    }

    companion object {
        const val PAGE_SIZE = 30
    }
}