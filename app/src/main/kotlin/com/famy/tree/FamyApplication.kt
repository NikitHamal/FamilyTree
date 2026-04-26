package com.famy.tree

import android.app.Application
import com.famy.tree.crash.CrashCatcher

class FamyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashCatcher.install(this)
    }
}
