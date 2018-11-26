package de.p72b.locator.preferences

import android.content.Context
import android.content.SharedPreferences
import de.p72b.locator.Locator

object LocatorPreferences {
    private const val SHARED_PREFS_FILE = "locator"

    private val preferences: SharedPreferences = Locator.appContext.getSharedPreferences(
            SHARED_PREFS_FILE, Context.MODE_PRIVATE)

    fun writeToPreferences(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun readFomPreferences(key: String): Boolean {
        return preferences.getBoolean(key, false)
    }
}
