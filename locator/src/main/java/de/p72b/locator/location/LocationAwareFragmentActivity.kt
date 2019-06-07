package de.p72b.locator.location

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity

@SuppressLint("Registered")
open class LocationAwareFragmentActivity : FragmentActivity() {
    private var activityImplementation = LocationAwareActivityImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityImplementation.onCreate(this, savedInstanceState)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        activityImplementation.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
