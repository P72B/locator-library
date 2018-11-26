package de.p72b.locator

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object Locator {

    @Volatile
    lateinit var appContext: Context

    fun setContext(context: Context) {
        appContext = context
    }
}
