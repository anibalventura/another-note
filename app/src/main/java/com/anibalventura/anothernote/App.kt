package com.anibalventura.anothernote

import android.app.Application
import android.content.res.Resources

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        resourses = resources
    }

    companion object {
        var instance: App? = null
            private set
        var resourses: Resources? = null
            private set
    }
}