package de.p72b.locator.location

import android.location.Location

interface ILastLocationListener {
    fun onLastLocationSuccess(location: Location)

    fun onLastLocationFailure(message: String?)
}