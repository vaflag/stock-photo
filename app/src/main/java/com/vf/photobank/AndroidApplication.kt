package com.vf.photobank

import android.app.Application
import com.vf.photobank.di.networkModule
import com.vf.photobank.di.servicesModule
import com.vf.photobank.di.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AndroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AndroidApplication)
            modules(
                viewModelsModule,
                servicesModule,
                networkModule
            )
        }
    }
}
