package com.okhome.awesomeapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.okhome.awesomeapp.module.database.PageEntity

@Dao
interface PageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentPage(page: PageEntity)

    @Query("SELECT * FROM ${PageEntity.TABLE_NAME} LIMIT 1")
    fun getCurrentPage(): PageEntity?
}