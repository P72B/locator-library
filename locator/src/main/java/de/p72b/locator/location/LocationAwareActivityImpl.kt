package de.p72b.locator.location

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import de.p72b.locator.preferences.LocatorPreferences

@Suppress("UNUSED_PARAMETER")
internal class LocationAwareActivityImpl {
    private lateinit var locationManager: LocationManager
    private lateinit var settingsClientManager: SettingsClientManager

    private val locationSwitchStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (android.location.LocationManager.PROVIDERS_CHANGED_ACTION != intent.action) {
                return
            }

            locationManager.deviceLocationSettingFulfilled(object : ISettingsClientResultListener {
                override fun onSuccess() {
                    locationManager.onProviderStateChanged(true)
                }

                override fun onFailure(code: Int, message: String) {
                    locationManager.onProviderStateChanged(false)
                }
            })
        }
    }

    fun onCreate(activity: Activity, savedInstanceState: Bundle?) {
        settingsClientManager = SettingsClientManager(activity)
        locationManager = LocationManager(activity, settingsClientManager)
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        for (permission in permissions) {
            LocatorPreferences.writeToPreferences(permission, true)
        }
        locationManager.notifyPermissionRequestResults(permissions, grantResults)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SettingsClientManager.REQUEST_CODE_SETTINGS) {
            settingsClientManager.onActivityResult(resultCode)
        }
    }

    fun onStart(activity: Activity) {
        activity.registerReceiver(
            locationSwitchStateReceiver, IntentFilter(android.location.LocationManager.PROVIDERS_CHANGED_ACTION)
        )
    }

    fun onStop(activity: Activity) {
        activity.unregisterReceiver(locationSwitchStateReceiver)
    }
}
