package com.android.home_automation

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        Log.e("🔥 Firebase", "Firebase initialized ✅")

    }
}
