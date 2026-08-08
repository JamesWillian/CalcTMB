package com.jammes.calctmb.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jammes.calctmb.data.local.dao.MeasurementDao
import com.jammes.calctmb.data.local.entity.MeasurementEntity

@Database(
    entities = [MeasurementEntity::class],
    version = 1,
    exportSchema = true
)
abstract class CalcTmbDatabase : RoomDatabase() {

    abstract fun measurementDao(): MeasurementDao

    companion object {
        private const val DATABASE_NAME = "calc_tmb.db"

        fun build(context: Context): CalcTmbDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                CalcTmbDatabase::class.java,
                DATABASE_NAME
            ).build()
    }
}
