package de.p72b.locator.demo

import android.location.Location
import android.view.View
import de.p72b.locator.location.ILastLocationListener
import de.p72b.locator.location.LocationManager

class MainPresenter(
    private val locationManager: LocationManager,
    private val mainActivity: MainActivity
) {

    fun onClick(view: View?) {
        when (view?.id) {
            R.id.vButtonLoud -> locateMePressed()
            R.id.vButtonSilent -> locateMePressed(false, false)
        }
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
}