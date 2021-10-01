package com.okhome.awesomeapp.data.repository

import androidx.paging.*
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.okhome.awesomeapp.DatabaseTestModule
import com.okhome.awesomeapp.data.database.PhotoDatabase
import com.okhome.awesomeapp.module.database.PhotosEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class PhotosRemoteMediatorTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var mockService: MockService
    private lateinit var mediator: PhotosRemoteMediator

    @Inject
    @Named(DatabaseTestModule.TEST_DATABASE)
    lateinit var database: PhotoDatabase

    @Before
    fun setup() {
        hiltRule.inject()
        mockService = MockService()

        mediator = PhotosRemoteMediator(
            mockService,
            database
        )
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun currentPageReturnNotNullAnd1WhenFirstLoadData() = runBlocking {
        val pagingState = PagingState<Int, PhotosEntity>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        mediator.load(LoadType.REFRESH, pagingState)
        val currentPage = database.remoteKeysDao().getCurrentPage()
        assertThat(currentPage).isNotNull()
        assertThat(currentPage?.currentPage).isEqualTo(1)
    }

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runBlocking {
        val pagingState = PagingState<Int, PhotosEntity>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertThat(result is RemoteMediator.MediatorResult.Success).isTrue()
        assertThat((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached).isFalse()
    }

    @Test
    fun refreshLoadSuccessAndEndOfPaginationWhenNoMoreData() = runBlocking {
        mockService.shouldReturnData(false)
        val pagingState = PagingState<Int, PhotosEntity>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertThat(result is RemoteMediator.MediatorResult.Success).isTrue()
        assertThat((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached).isTrue()
    }

    @Test
    fun refreshLoadReturnsErrorResultWhenErrorOccurs() = runBlocking {
        mockService.shouldReturnError(true)
        val pagingState = PagingState<Int, PhotosEntity>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertThat(result is RemoteMediator.MediatorResult.Error).isTrue()
    }
}