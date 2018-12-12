package de.p72b.locator.location

import android.location.Location

interface ILastLocationListener {
    fun onSuccess(location: Location?)

    fun onError(code: Int, message: String?)
}