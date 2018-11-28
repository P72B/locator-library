package de.p72b.locator.location

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import de.p72b.locator.preferences.LocatorPreferences

open class LocationAwareAppCompatActivity : AppCompatActivity() {
    protected lateinit var locationManager: LocationManager
    private lateinit var settingsClientManager: SettingsClientManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsClientManager = SettingsClientManager(this)
        locationManager = LocationManager(this, settingsClientManager)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        for (permission in permissions) {
            LocatorPreferences.writeToPreferences(permission, true)
        }
        locationManager.notifyPermissionRequestResults(permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SettingsClientManager.REQUEST_CODE_SETTINGS) {
            settingsClientManager.onActivityResult(resultCode)
        }
    }
}
