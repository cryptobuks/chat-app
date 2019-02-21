package com.wellin5.chatapp.Controller

import android.app.Application
import com.wellin5.chatapp.Utilities.SharedPrefs

class App: Application() {

    companion object {
        lateinit var prefs: SharedPrefs
    }

    override fun onCreate() {
        prefs= SharedPrefs(applicationContext)
        super.onCreate()
    }
}