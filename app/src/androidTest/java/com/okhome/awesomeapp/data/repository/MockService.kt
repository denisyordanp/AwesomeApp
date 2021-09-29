package com.okhome.awesomeapp.data.repository

import com.okhome.awesomeapp.data.remote.ApiService
import com.okhome.awesomeapp.module.remote.ResponsePhoto
import com.okhome.awesomeapp.module.remote.ResponsePhotoData
import com.okhome.awesomeapp.module.remote.Sources
import java.net.UnknownHostException

class MockService : ApiService {

    private val mockSource = Sources("", "", "", "", "", "", "", "")
    private val mockData = listOf(
        ResponsePhoto(
            "",
            1,
            1,
            "",
            1,
            "",
            mockSource,
            "",
            1
        ),
        ResponsePhoto(
            "",
            1,
            2,
            "",
            1,
            "",
            mockSource,
            "",
            1
        )
    )

    private var shouldReturnData = true
    private var shouldReturnError = false

    fun shouldReturnData(state: Boolean) {
        shouldReturnData = state
    }

    fun shouldReturnError(state: Boolean) {
        shouldReturnError = state
    }

    override suspend fun requestPhotos(page: Int, itemsPerPage: Int): ResponsePhotoData {
        if (shouldReturnError) {
            throw UnknownHostException("test error")
        }

        return ResponsePhotoData(
            "2",
            1,
            10,
            if (shouldReturnData) mockData else listOf(),
            1
        )
    }
}