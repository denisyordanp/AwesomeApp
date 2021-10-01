package com.okhome.awesomeapp.data.database

import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.okhome.awesomeapp.DatabaseTestModule
import com.okhome.awesomeapp.module.database.PhotosEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidTest
@SmallTest
class PhotoDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    @Named(DatabaseTestModule.TEST_DATABASE)
    lateinit var database: PhotoDatabase
    private lateinit var photoDao: PhotoDao

    @Before
    fun setup() {
        hiltRule.inject()
        photoDao = database.photoDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun getPhotoByIdReturnPhotoEntity() = runBlocking {
        val photo = PhotosEntity(1, "", "", "", "", "", null, null)
        photoDao.insertPhotos(listOf(photo))

        val photoEntity = photoDao.getPhotoById(1)
        assertThat(photoEntity).isEqualTo(photo)
    }
}