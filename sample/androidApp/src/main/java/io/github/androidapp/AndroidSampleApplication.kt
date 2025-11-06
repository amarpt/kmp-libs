package io.github.androidapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.github.postapi.di.initDefaultKoin

@HiltAndroidApp
class AndroidSampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initDefaultKoin()
    }
}