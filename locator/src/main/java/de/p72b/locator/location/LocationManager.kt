package de.p72b.locator.location

import android.Manifest
import android.app.Activity
import android.location.Location

import com.google.android.gms.location.LocationRequest

import java.util.concurrent.CopyOnWriteArrayList

class LocationManager internal constructor(activity: Activity,
                                           private val settingsClientManager: SettingsClientManager) : ILocationUpdatesListener {

    private val fusedLocationSource: GooglePlayServicesLocationSource
    private val subscribers = CopyOnWriteArrayList<ILocationUpdatesListener>()
    private val permissionRequestSubscribers = CopyOnWriteArrayList<IPermissionListener>()
    private val permissionManager: PermissionManager = PermissionManager(activity)

    init {
        fusedLocationSource = GooglePlayServicesLocationSource(activity, permissionManager,
                settingsClientManager, this)
    }

    override fun onLocationChanged(location: Location) {
        if (subscribers.isEmpty()) {
            return
        }
        for (listener in subscribers) {
            listener.onLocationChanged(location)
        }
    }

    fun getLastLocation(listener: ILastLocationListener) {
        fusedLocationSource.getLastLocation(listener)
    }

    fun subscribeToLocationChanges(listener: ILocationUpdatesListener) {
        if (subscribers.isEmpty()) {
            fusedLocationSource.startReceivingLocationUpdates()
        }
        subscribers.add(listener)
    }

    fun unSubscribeToLocationChanges(listener: ILocationUpdatesListener) {
        subscribers.remove(listener)
        if (subscribers.isEmpty()) {
            fusedLocationSource.stopReceivingLocationUpdates()
        }
    }

    fun hasLocationPermission(): Boolean {
        return permissionManager.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun checkLocationPermission(listener: IPermissionListener) {
        if (permissionManager.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            listener.onGranted()
            return
        }
        permissionRequestSubscribers.add(listener)
        permissionManager.hasPermissionIfNotRequest(Manifest.permission.ACCESS_FINE_LOCATION, listener)
    }

    private fun notifyPermissionListener(isGranted: Boolean) {
        if (subscribers.isEmpty()) {
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

    internal fun notifyPermissionRequestResults(permissions: Array<String>, grantResults: IntArray) {
        for ((index, permission) in permissions.withIndex()) {
            when (permission) {
                Manifest.permission_group.LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION -> {
                    val grantResult = grantResults[index]
                    if (Activity.RESULT_OK == grantResult) {
                        fusedLocationSource.startReceivingLocationUpdates()
                    } else if (Activity.RESULT_CANCELED == grantResult) {
                        fusedLocationSource.stopReceivingLocationUpdates()
                    }
                    notifyPermissionListener(Activity.RESULT_OK == grantResult)
                }
            }
        }
    }

    @JvmOverloads
    fun deviceLocationSettingFulfilled(listener: ISettingsClientResultListener, shouldRequestSettingsChange: Boolean = false) {
        val locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        settingsClientManager.checkIfDeviceLocationSettingFulfillRequestRequirements(
            shouldRequestSettingsChange, locationRequest, listener)
    }
}
