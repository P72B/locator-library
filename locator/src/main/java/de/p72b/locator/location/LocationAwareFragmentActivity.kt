package de.p72b.locator.location

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

@SuppressLint("Registered")
open class LocationAwareFragmentActivity : FragmentActivity() {
    private var activityImplementation = LocationAwareActivityImpl()

    val locationManager: LocationManager by lazy {
        activityImplementation.locationManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityImplementation.onCreate(this, savedInstanceState)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        activityImplementation.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        activityImplementation.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        activityImplementation.onStart(this)
    }

    override fun onStop() {
        activityImplementation.onStop(this)
        super.onStop()
    }
}
