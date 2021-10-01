package com.okhome.awesomeapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.okhome.awesomeapp.module.database.RemoteKeysEntity

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys: List<RemoteKeysEntity>)

    @Query("SELECT * FROM ${RemoteKeysEntity.TABLE_NAME} WHERE photoId = :id")
    suspend fun remoteKeysId(id: Long): RemoteKeysEntity?

    @Query("DELETE FROM ${RemoteKeysEntity.TABLE_NAME}")
    suspend fun clearRemoteKeys()
}