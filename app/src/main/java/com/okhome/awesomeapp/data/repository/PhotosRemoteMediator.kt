package com.okhome.awesomeapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.okhome.awesomeapp.data.database.PhotoDatabase
import com.okhome.awesomeapp.data.remote.ApiService
import com.okhome.awesomeapp.module.database.PageEntity
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

        val pageEntity = database.pageDao().getCurrentPage()
        val page = when (loadType) {
            LoadType.REFRESH -> {
                // if current page is null then load default starting page
                pageEntity?.currentPage ?: PEXELS_STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                // If current page is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if current page becomes non-null.
                // If current page is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for prepend.
                val prevPage = pageEntity?.currentPage?.minus(1)
                    ?: return MediatorResult.Success(endOfPaginationReached = pageEntity?.isPrevPageAvailable == false)
                prevPage
            }
            LoadType.APPEND -> {
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with endOfPaginationReached = false because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for append.
                val nextPage = pageEntity?.currentPage?.plus(1)
                    ?: return MediatorResult.Success(endOfPaginationReached = pageEntity?.isNextPageAvailable == false)
                nextPage
            }
        }

        try {
            val apiResponse = service.requestPhotos(page, state.config.pageSize)

            val photos = apiResponse.photos
            val endOfPaginationReached = photos.isEmpty()

            database.withTransaction {

                val currentPage = PageEntity(
                    isPrevPageAvailable = page != PEXELS_STARTING_PAGE_INDEX,
                    currentPage = page,
                    isNextPageAvailable = !endOfPaginationReached
                )
                database.pageDao().insertCurrentPage(currentPage)
                database.photoDao().insertPhotos(photos.generatesPhotosEntity())
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private fun List<ResponsePhoto>.generatesPhotosEntity(): List<PhotosEntity> {
        return this.map { it.generatesToEntity() }
    }
}