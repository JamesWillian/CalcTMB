package com.jammes.calctmb.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jammes.calctmb.data.local.entity.MeasurementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {

    @Query("SELECT * FROM measurements ORDER BY created_at DESC, id DESC")
    fun observeAll(): Flow<List<MeasurementEntity>>

    @Insert
    suspend fun insert(measurement: MeasurementEntity): Long

    @Query("DELETE FROM measurements WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE measurements SET name = :name WHERE id = :id")
    suspend fun updateName(id: Long, name: String?)
}
