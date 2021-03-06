package com.francescoalessi.hacky

import android.app.Application
import com.francescoalessi.hacky.di.AndroidModule
import com.francescoalessi.hacky.di.AppComponent
import com.francescoalessi.hacky.di.DaggerAppComponent

class HackyApplication : Application()
{
    // Reference to the DI application graph that is used across the whole app
    val appComponent: AppComponent =
        DaggerAppComponent.builder().androidModule(AndroidModule(this)).build()
}