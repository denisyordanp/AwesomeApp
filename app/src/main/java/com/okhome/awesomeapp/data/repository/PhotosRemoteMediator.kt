package com.okhome.awesomeapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.okhome.awesomeapp.data.database.PhotoDatabase
import com.okhome.awesomeapp.data.remote.ApiService
import com.okhome.awesomeapp.module.database.PhotosEntity

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
        return MediatorResult.Success(true)
    }

}