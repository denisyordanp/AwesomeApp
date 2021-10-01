package com.okhome.awesomeapp.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.okhome.awesomeapp.data.database.PhotoDatabase
import com.okhome.awesomeapp.data.remote.ApiService
import com.okhome.awesomeapp.module.database.PhotosEntity
import com.okhome.awesomeapp.module.remote.ResponsePhoto
import retrofit2.HttpException
import java.io.IOException

private const val PEXELS_STARTING_PAGE_INDEX = 1

@ExperimentalPagingApi
class PhotosRemoteMediator(
    private val service: ApiService,
    private val database: PhotoDatabase
) : RemoteMediator<Int, PhotosEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PhotosEntity>
    ): MediatorResult {
        val page = when (val keyData = getKeyData(loadType, state)) {
            is MediatorResult.Success -> {
                Log.d("DEBUGING", "success $keyData")
                return keyData
            }
            else -> {
                Log.d("DEBUGING", "not $keyData")
                keyData as Int
            }
        }

        try {
            Log.d("DEBUGING", "page $page")
            val apiResponse = service.requestPhotos(page, state.config.pageSize)

            val photos = apiResponse.photos
            val endOfPaginationReached = photos.isEmpty()

            database.withTransaction {

                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    database.photoDao().clearPhotos()
                }

                val prevKey = if (page == PEXELS_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                database.photoDao().insertPhotos(photos.generatesPhotosEntity(prevKey, nextKey))
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getKeyData(loadType: LoadType, state: PagingState<Int, PhotosEntity>): Any {
        return when (loadType) {
            LoadType.REFRESH -> {
                val photo = getRemoteKeyClosestToCurrentPosition(state)
                photo?.nextKey?.minus(1) ?: PEXELS_STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val photo = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for prepend.
                val prevKey = photo?.prevKey
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = photo != null)
                }
                prevKey
            }
            LoadType.APPEND -> {
                val photo = getRemoteKeyForLastItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with endOfPaginationReached = false because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for append.
                val nextKeys = photo?.nextKey
                if (nextKeys == null) {
                    return MediatorResult.Success(endOfPaginationReached = photo != null)
                }
                nextKeys
            }
        }
    }

    private fun List<ResponsePhoto>.generatesPhotosEntity(
        prevKey: Int?,
        nextKey: Int?
    ): List<PhotosEntity> {
        return this.map { it.generatesToEntity(prevKey, nextKey) }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, PhotosEntity>): PhotosEntity? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, PhotosEntity>): PhotosEntity? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, PhotosEntity>
    ): PhotosEntity? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)
        }
    }
}