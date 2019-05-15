package de.p72b.locator.util

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Log
import de.p72b.locator.Locator

class LibLocatorContextProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        val context = context
        if (context != null) {
           Locator.setContext(context)
        } else {
            Log.e(TAG, "Context injection to common failed. Context is null! Check LibLocatorContextProvider registration in the Manifest!")
        }
        return false
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    companion object {
        private val TAG = LibLocatorContextProvider::class.java.simpleName
    }
}
