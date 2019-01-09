package de.p72b.locator.location

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location

import com.google.android.gms.location.LocationRequest

import java.util.concurrent.CopyOnWriteArrayList

class LocationManager internal constructor(
    activity: Activity,
    private val settingsClientManager: SettingsClientManager
) : ILocationUpdatesListener {

    companion object {
        const val ERROR_CANCELED_SETTINGS_CHANGE = 0
        const val ERROR_MISSING_PERMISSION = 1
        const val ERROR_MISSING_PERMISSION_DO_NOT_ASK_AGAIN = 2
        const val ERROR_SETTINGS_NOT_FULFILLED = 3
        const val ERROR_FUSED_LOCATION_ERROR = 4
        const val ERROR_LOCATION_UPDATES_RETRY_LIMIT = 5
        const val ERROR_CANCELED_PERMISSION_CHANGE = 6
        const val ERROR_PROVIDERS_DISABLED = 7
    }

    private val fusedLocationSource: GooglePlayServicesLocationSource
    private val subscribers = CopyOnWriteArrayList<ILocationUpdatesListener>()
    private val permissionRequestSubscribers = CopyOnWriteArrayList<IPermissionListener>()
    private val permissionManager: PermissionManager = PermissionManager(activity)

    init {
        fusedLocationSource = GooglePlayServicesLocationSource(
            activity, permissionManager,
            settingsClientManager, this
        )
    }

    override fun onLocationChanged(location: Location) {
        if (subscribers.isEmpty()) {
            return
        }
        for (listener in subscribers) {
            listener.onLocationChanged(location)
        }
    }

    override fun onLocationChangedError(code: Int, message: String?) {
        if (subscribers.isEmpty()) {
            return
        }
        for (listener in subscribers) {
            listener.onLocationChangedError(code, message)
        }
    }

    @JvmOverloads
    fun getLastLocation(listener: ILastLocationListener, shouldRequestLocationPermission: Boolean = true,
                        shouldRequestSettingsChange: Boolean = true) {
        fusedLocationSource.getLastLocation(listener, shouldRequestLocationPermission, shouldRequestSettingsChange)
    }

    fun subscribeToLocationChanges(listener: ILocationUpdatesListener) {
        subscribers.add(listener)
        if (subscribers.size == 1) {
            fusedLocationSource.startReceivingLocationUpdates()
        }
    }

    fun unSubscribeToLocationChanges(listener: ILocationUpdatesListener) {
        subscribers.remove(listener)
        if (subscribers.isEmpty()) {
            fusedLocationSource.stopReceivingLocationUpdates()
        }
    }

    fun setLocationRequest(locationRequest: LocationRequest) {
        fusedLocationSource.locationRequest = locationRequest
        if (!subscribers.isEmpty()) {
            fusedLocationSource.restartLocationUpdates()
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
        permissionManager.notifyPermissionListener(isGranted)
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

    internal fun notifyPermissionRequestResults(permissions: Array<String>, grantResults: IntArray) {
        for ((index, permission) in permissions.withIndex()) {
            when (permission) {
                Manifest.permission_group.LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION -> {
                    val grantResult = grantResults[index]
                    if (PackageManager.PERMISSION_GRANTED == grantResult) {
                        fusedLocationSource.startReceivingLocationUpdates()
                    } else if (PackageManager.PERMISSION_DENIED == grantResult) {
                        fusedLocationSource.stopReceivingLocationUpdates()
                    }
                    notifyPermissionListener(PackageManager.PERMISSION_GRANTED == grantResult)
                }
            }
        }
    }

    @JvmOverloads
    fun deviceLocationSettingFulfilled(listener: ISettingsClientResultListener,
                                       shouldRequestSettingsChange: Boolean = false) {
        val locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        settingsClientManager.checkIfDeviceLocationSettingFulfillRequestRequirements(
            shouldRequestSettingsChange, locationRequest, listener
        )
    }

    fun onProviderStateChanged(isLocationProviderAvailable: Boolean) {
        if (!isLocationProviderAvailable) {
            onLocationChangedError(LocationManager.ERROR_PROVIDERS_DISABLED, "Location provider set to disabled.")
        } else {
            fusedLocationSource.getLastLocation(object : ILastLocationListener {
                override fun onSuccess(location: Location?) {
                    if (location != null) {
                        onLocationChanged(location)
                    }
                }

                override fun onError(code: Int, message: String?) {
                    onLocationChangedError(code, message)
                }
            }, false, false)
        }
    }
}
