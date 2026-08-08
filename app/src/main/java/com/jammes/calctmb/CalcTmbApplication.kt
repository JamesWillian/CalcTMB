package com.jammes.calctmb

import android.app.Application
import com.jammes.calctmb.di.dataModule
import com.jammes.calctmb.di.domainModule
import com.jammes.calctmb.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class CalcTmbApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@CalcTmbApplication)
            modules(dataModule, domainModule, presentationModule)
        }
    }
}
