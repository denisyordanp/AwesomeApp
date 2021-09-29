package com.okhome.awesomeapp.data.repository

import com.okhome.awesomeapp.data.remote.ApiService
import com.okhome.awesomeapp.module.remote.ResponsePhotoData

class MockService : ApiService {

    override suspend fun requestPhotos(page: Int, itemsPerPage: Int): ResponsePhotoData {
        return ResponsePhotoData(
            "2",
            1,
            10,
            listOf(),
            1
        )
    }
}