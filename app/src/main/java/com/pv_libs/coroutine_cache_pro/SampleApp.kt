package com.pv_libs.coroutine_cache_pro

import android.app.Application
import timber.log.Timber

class SampleApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}