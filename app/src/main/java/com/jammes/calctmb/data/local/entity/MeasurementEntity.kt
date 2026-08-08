package com.jammes.calctmb.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "measurements")
data class MeasurementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "name")
    val name: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    /** Guardado como texto (`MALE`/`FEMALE`) para o banco continuar legível. */
    @ColumnInfo(name = "sex")
    val sex: String,
    @ColumnInfo(name = "age")
    val age: Int,
    @ColumnInfo(name = "weight_kg")
    val weightKg: Double,
    @ColumnInfo(name = "height_cm")
    val heightCm: Double
)
