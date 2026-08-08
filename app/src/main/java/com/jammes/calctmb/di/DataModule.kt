package com.jammes.calctmb.di

import android.content.Context
import android.content.SharedPreferences
import com.jammes.calctmb.data.local.CalcTmbDatabase
import com.jammes.calctmb.data.local.dao.MeasurementDao
import com.jammes.calctmb.data.repository.IntroRepositoryImpl
import com.jammes.calctmb.data.repository.MeasurementRepositoryImpl
import com.jammes.calctmb.domain.repository.IntroRepository
import com.jammes.calctmb.domain.repository.MeasurementRepository
import com.jammes.calctmb.domain.util.TimeProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val PREFERENCES_NAME = "prefs"

val dataModule = module {

    single { CalcTmbDatabase.build(androidContext()) }

    single<MeasurementDao> { get<CalcTmbDatabase>().measurementDao() }

    single<SharedPreferences> {
        androidContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    single<TimeProvider> { TimeProvider { System.currentTimeMillis() } }

    single<MeasurementRepository> { MeasurementRepositoryImpl(get()) }

    single<IntroRepository> { IntroRepositoryImpl(get()) }
}
