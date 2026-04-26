package com.famy.tree.crash

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Process
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

object CrashCatcher {
    private const val PREFS = "famy_crash"
    private const val KEY_LAST_CRASH = "last_crash"

    fun install(application: Application) {
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            val stackTrace = StringWriter().also { writer ->
                throwable.printStackTrace(PrintWriter(writer))
            }.toString()
            application.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_LAST_CRASH, stackTrace)
                .apply()

            val intent = Intent(application, CrashReportActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .putExtra(KEY_LAST_CRASH, stackTrace)
            runCatching { application.startActivity(intent) }
            Thread.sleep(350)
            Process.killProcess(Process.myPid())
            exitProcess(10)
        }
    }

    fun readLastCrash(context: Context): String {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_LAST_CRASH, "")
            .orEmpty()
    }
}
