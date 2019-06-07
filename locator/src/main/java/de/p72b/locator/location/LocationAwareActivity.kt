package de.p72b.locator.location

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle

@SuppressLint("Registered")
open class LocationAwareActivity : Activity() {
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
