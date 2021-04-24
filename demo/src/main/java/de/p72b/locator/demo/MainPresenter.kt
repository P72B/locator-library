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

    private var listeningForLocationUpdatesState = ListeningState.STOPPED
    private val locationUpdatesListener = object : ILocationUpdatesListener {
        override fun onLocationChanged(location: Location) {
            if (listeningForLocationUpdatesState == ListeningState.PAUSED) {
                listeningForLocationUpdatesState = ListeningState.RUNNING
                mainActivity.setLocationUpdatesState(listeningForLocationUpdatesState.action)
            }
            mainActivity.updateLocation(location)
        }

        override fun onLocationChangedError(code: Int, message: String?) {
            mainActivity.showSnackbar("($code) $message ")
            listeningForLocationUpdatesState = ListeningState.STOPPED
            if (LocationManager.ERROR_PROVIDERS_DISABLED == code) {
                listeningForLocationUpdatesState = ListeningState.PAUSED
            }
            mainActivity.setLocationUpdatesState(listeningForLocationUpdatesState.action)
        }
    }

    init {
        mainActivity.setLocationUpdatesState(listeningForLocationUpdatesState.action)
    }

    fun onClick(view: View?) {
        when (view?.id) {
            R.id.vButtonStatus -> statusPressed()
            R.id.vButtonLoud -> locateMePressed()
            R.id.vButtonSilent -> locateMePressed(false, false)
            R.id.vButtonLocationUpdates -> locationUpdatesPressed()
            R.id.vButtonLocationRequestDefault -> setRequestSettings(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                5000,
                10000
            )
            R.id.vButtonLocationRequestHigh -> setRequestSettings(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                4000,
                8000
            )
            R.id.vButtonLocationRequestLow -> setRequestSettings(
                LocationRequest.PRIORITY_LOW_POWER,
                20000,
                40000
            )
        }
    }

    fun onResume() {
        if (listeningForLocationUpdatesState != ListeningState.STOPPED) {
            locationManager.subscribeToLocationChanges(locationUpdatesListener)
        }
    }

    fun onPause() {
        if (listeningForLocationUpdatesState != ListeningState.STOPPED) {
            locationManager.unSubscribeToLocationChanges(locationUpdatesListener)
        }
    }

    private fun setRequestSettings(priority: Int, fastestInterval: Long, interval: Long) {
        locationManager.setLocationRequest(LocationRequest.create().apply {
            this.priority = priority
            this.fastestInterval = fastestInterval
            this.interval = interval
        })
    }

    private fun statusPressed() {
        locationManager.isLocationAvailable(object : ILastLocationListener {
            override fun onSuccess(location: Location?) {
                mainActivity.showSnackbar("Location could be fetched")
            }

            override fun onError(code: Int, message: String?) {
                mainActivity.showSnackbar("($code) $message ")
            }
        })
    }

    private fun locateMePressed(
        shouldRequestPermission: Boolean = true,
        shouldRequestSettingsChange: Boolean = true
    ) {
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
        when (listeningForLocationUpdatesState) {
            ListeningState.STOPPED -> {
                listeningForLocationUpdatesState = ListeningState.RUNNING
                locationManager.subscribeToLocationChanges(locationUpdatesListener)
            }
            ListeningState.PAUSED,
            ListeningState.RUNNING -> {
                listeningForLocationUpdatesState = ListeningState.STOPPED
                locationManager.unSubscribeToLocationChanges(locationUpdatesListener)
            }
        }
        mainActivity.setLocationUpdatesState(listeningForLocationUpdatesState.action)
    }
}

enum class ListeningState(val action: String) {
    STOPPED("Start"),
    PAUSED("Stop paused listener"),
    RUNNING("Stop")
}