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
import com.okhome.awesomeapp.module.database.RemoteKeysEntity
import com.okhome.awesomeapp.module.remote.ResponsePhoto
import com.okhome.awesomeapp.module.remote.Sources
import retrofit2.HttpException
import java.io.IOException
import java.util.*

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
        val page = when (loadType) {
            LoadType.REFRESH -> {
                Log.d("DEBUGING", "refresh")
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: PEXELS_STARTING_PAGE_INDEX

            }
            LoadType.PREPEND -> {
                Log.d("DEBUGING", "prepend")
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for prepend.
                val prevKey = remoteKeys?.prevKey
                if (prevKey == null) {
                    Log.d("DEBUGING", "prevKey null")
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                prevKey
            }
            LoadType.APPEND -> {
                Log.d("DEBUGING", "append")
                val remoteKeys = getRemoteKeyForLastItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with endOfPaginationReached = false because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for append.
                val nextKeys = remoteKeys?.nextKey
                if (nextKeys == null) {
                    Log.d("DEBUGING", "nextKey null")
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                nextKeys
            }
        }

        try {
            val apiResponse = service.requestPhotos(page, state.config.pageSize)

            val photos = apiResponse.photos

            Log.d("DEBUGING", "page $page")
//            val photos = mockData()
            val endOfPaginationReached = photos.isEmpty()

            database.withTransaction {

                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().clearRemoteKeys()
                    database.photoDao().clearPhotos()
                }

                val prevKey = if (page == PEXELS_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = photos.map {
                    RemoteKeysEntity(
                        photoId = it.id,
                        prevKey, nextKey
                    )
                }
                database.remoteKeysDao().insertAll(keys)
                database.photoDao().insertPhotos(photos.generatesPhotosEntity())
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private val mockSource = Sources("", "", "", "", "", "", "", "")
    private fun mockData(): List<ResponsePhoto> {
        val list: MutableList<ResponsePhoto> = mutableListOf()
        repeat(30) {
            list.add(ResponsePhoto(
                "",
                1,
                (1L..9999L).random(),
                "",
                1,
                "",
                mockSource,
                "",
                1
            ))
        }
        return list
    }

    private fun List<ResponsePhoto>.generatesPhotosEntity(): List<PhotosEntity> {
        return this.map { it.generatesToEntity() }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, PhotosEntity>): RemoteKeysEntity? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { photo ->
            // Get the remote keys of the last item retrieved
            database.remoteKeysDao().remoteKeysId(photo.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, PhotosEntity>): RemoteKeysEntity? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { photo ->
                // Get the remote keys of the first items retrieved
                database.remoteKeysDao().remoteKeysId(photo.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, PhotosEntity>
    ): RemoteKeysEntity? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { photoId ->
                database.remoteKeysDao().remoteKeysId(photoId)
            }
        }
    }
}