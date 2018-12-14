package de.p72b.locator.demo

import android.location.Location
import android.view.View
import com.google.android.gms.location.LocationRequest
import de.p72b.locator.location.ILastLocationListener
import de.p72b.locator.location.ILocationUpdatesListener
import de.p72b.locator.location.LocationManager

class MainPresenter(
    private val locationManager: LocationManager,
    private val mainActivity: MainActivity
) {

    private var isListeningForLocationUpdates = false
    private val locationUpdatesListener = object : ILocationUpdatesListener {
        override fun onLocationChanged(location: Location) {
            mainActivity.updateLocation(location)
        }

        override fun onLocationChangedError(code: Int, message: String?) {
            isListeningForLocationUpdates = false
            mainActivity.setLocationUpdatesState(isListeningForLocationUpdates)
            mainActivity.showSnackbar("($code) $message ")
        }
    }

    init {
        mainActivity.setLocationUpdatesState(isListeningForLocationUpdates)
    }

    fun onClick(view: View?) {
        when (view?.id) {
            R.id.vButtonLoud -> locateMePressed()
            R.id.vButtonSilent -> locateMePressed(false, false)
            R.id.vButtonLocationUpdates -> locationUpdatesPressed()
            R.id.vButtonLocationRequestDefault -> setRequestSettings(LocationRequest.PRIORITY_HIGH_ACCURACY, 5000, 10000)
            R.id.vButtonLocationRequestHigh -> setRequestSettings(LocationRequest.PRIORITY_HIGH_ACCURACY, 4000, 8000)
            R.id.vButtonLocationRequestLow -> setRequestSettings(LocationRequest.PRIORITY_LOW_POWER, 20000, 40000)
        }
    }

    fun onResume() {
        if (isListeningForLocationUpdates) {
            locationManager.subscribeToLocationChanges(locationUpdatesListener)
        }
    }

    fun onStop() {
        if (isListeningForLocationUpdates) {
            locationManager.unSubscribeToLocationChanges(locationUpdatesListener)
        }
    }

    private fun setRequestSettings(priority: Int, fastestInterval: Long, interval: Long) {
        val request = LocationRequest()
        request.priority = priority
        request.fastestInterval = fastestInterval
        request.interval = interval
        locationManager.setLocationRequest(request)
    }

    private fun locateMePressed(shouldRequestPermission: Boolean = true, shouldRequestSettingsChange: Boolean = true) {
        locationManager.getLastLocation(object : ILastLocationListener {
            override fun onSuccess(location: Location?) {
                if (location != null) {
                    mainActivity.updateLocation(location)
                } else {
                    mainActivity.showSnackbar("Location is null")
                }
            }

            override fun onError(code: Int, message: String?) {
                mainActivity.showSnackbar("($code) $message ")
            }
        }, shouldRequestPermission, shouldRequestSettingsChange)
    }

    private fun locationUpdatesPressed() {
        if (isListeningForLocationUpdates) {
            isListeningForLocationUpdates = false
            locationManager.unSubscribeToLocationChanges(locationUpdatesListener)
        } else {
            isListeningForLocationUpdates = true
            locationManager.subscribeToLocationChanges(locationUpdatesListener)
        }
        mainActivity.setLocationUpdatesState(isListeningForLocationUpdates)
    }
}