package de.p72b.locator.demo

import android.location.Location
import android.view.View
import de.p72b.locator.location.ILastLocationListener
import de.p72b.locator.location.IPermissionListener
import de.p72b.locator.location.ISettingsClientResultListener
import de.p72b.locator.location.LocationManager

class MainPresenter(
    private val locationManager: LocationManager,
    private val mainActivity: MainActivity
) {

    fun onClick(view: View?) {
        when (view?.id) {
            R.id.vButton -> locateMePressed()
        }
    }

    private fun locateMePressed() {
        locationManager.checkLocationPermission(object : IPermissionListener {
            override fun onGranted() {
                checkSettings()
            }

            override fun onDenied(doNotAskAgain: Boolean) {
                mainActivity.showSnackbar("No permission doNotAskAgain: $doNotAskAgain")
            }
        })
    }

    private fun checkSettings() {
        locationManager.deviceLocationSettingFulfilled(object : ISettingsClientResultListener {
            override fun onSuccess() {
                getLastLocation()
            }

            override fun onFailure(s: String) {
                mainActivity.showSnackbar(s)
            }
        }, true)
    }

    private fun getLastLocation() {
        locationManager.getLastLocation(object : ILastLocationListener {
            override fun onLastLocationFailure(message: String?) {
                mainActivity.showSnackbar(message)
            }

            override fun onLastLocationSuccess(location: Location?) {
                if (location != null) {
                    mainActivity.updateLocation(location)
                } else {
                    mainActivity.showSnackbar("Location is null")
                }
            }
        })
    }
}