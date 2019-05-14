package de.p72b.locator.location

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker

import de.p72b.locator.preferences.LocatorPreferences
import java.util.concurrent.CopyOnWriteArrayList

class PermissionManager(private val activity: Activity) {

    companion object {
        private const val REQUEST_CODE_PERMISSION = 25
    }

    private val permissionRequestSubscribers = CopyOnWriteArrayList<IPermissionListener>()

    fun hasPermissionIfNotRequest(permission: String, listener: IPermissionListener? = null): Boolean {
        val permissionState = getPermissionStatus(permission)
        if (PermissionChecker.PERMISSION_GRANTED != permissionState) {
            requestPermission(permission, permissionState, REQUEST_CODE_PERMISSION, listener)
            return false
        }
        return true
    }

    fun hasPermission(permission: String): Boolean {
        return PermissionChecker.PERMISSION_GRANTED == getPermissionStatus(permission)
    }

    fun notifyPermissionListener(isGranted: Boolean) {
        if (permissionRequestSubscribers.isEmpty()) {
            return
        }
        for (listener in permissionRequestSubscribers) {
            if (isGranted) {
                listener.onGranted()
            } else {
                listener.onDenied(false)
            }
        }
        permissionRequestSubscribers.clear()
    }

    fun getPermissionStatus(manifestPermissionName: String): Int {
        if (ContextCompat.checkSelfPermission(activity, manifestPermissionName) == PackageManager.PERMISSION_GRANTED) {
            return PermissionChecker.PERMISSION_GRANTED
        }

        if (isFirstLocationPermissionRequest(manifestPermissionName)) {
            return PermissionChecker.PERMISSION_DENIED
        }

        return if (ActivityCompat.shouldShowRequestPermissionRationale(activity, manifestPermissionName)) {
            PermissionChecker.PERMISSION_DENIED
        } else PermissionChecker.PERMISSION_DENIED_APP_OP
    }

    private fun requestPermission(permission: String, permissionState: Int,
                                  requestCode: Int, listener: IPermissionListener?) {
        when (permissionState) {
            PermissionChecker.PERMISSION_DENIED -> {
                permissionRequestSubscribers.add(listener)
                ActivityCompat.requestPermissions(activity,
                    arrayOf(permission),
                    requestCode)
            }
            PermissionChecker.PERMISSION_DENIED_APP_OP -> {
                listener?.onDenied(true)
            }
            PermissionChecker.PERMISSION_GRANTED -> {
                listener?.onGranted()
            }
        }
    }

    private fun isFirstLocationPermissionRequest(manifestPermissionName: String): Boolean {
        return !LocatorPreferences.readFomPreferences(manifestPermissionName)
    }
}
