package com.okhome.awesomeapp

import android.content.Context
import androidx.room.Room
import com.okhome.awesomeapp.data.database.PhotoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@InstallIn(SingletonComponent::class)
@Module
object DatabaseTestModule {

    const val TEST_DATABASE = "test_database"

    @Provides
    @Named(TEST_DATABASE)
    fun provideAppDatabaseTest(@ApplicationContext context: Context): PhotoDatabase {
        return Room.inMemoryDatabaseBuilder(context, PhotoDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }
}