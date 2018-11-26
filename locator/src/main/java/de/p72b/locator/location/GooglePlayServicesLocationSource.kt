package de.p72b.locator.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

internal class GooglePlayServicesLocationSource(activity: Activity,
                                                private val permissionManager: PermissionManager,
                                                private val settingsClientManager: SettingsClientManager,
                                                private val locationUpdatesListener: ILocationUpdatesListener?) {

    private val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activity)
    private var locationRequest: LocationRequest = LocationRequest()
    private val locationCallback: LocationCallback

    init {
        initLocationRequest()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null || locationUpdatesListener == null) {
                    return
                }
                for (location in locationResult.locations) {
                    locationUpdatesListener.onLocationChanged(location)
                }
            }
        }
    }

    fun getLastLocation(listener: ILastLocationListener) {
        if (!permissionManager.hasPermissionIfNotRequest(Manifest.permission.ACCESS_FINE_LOCATION)) {
            listener.onLastLocationFailure("Location permission missing")
            return
        }

        settingsClientManager.checkIfDeviceLocationSettingFulfillRequestRequirements(
                true, locationRequest, object : ISettingsClientResultListener {
            override fun onSuccess() {
                getLastFusedLocation(listener)
            }

            override fun onFailure(message: String) {
                listener.onLastLocationFailure(message)
            }
        })
    }

    fun startReceivingLocationUpdates() {
        if (!permissionManager.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                || locationUpdatesListener == null) {
            return
        }

        settingsClientManager.checkIfDeviceLocationSettingFulfillRequestRequirements(
                false, locationRequest, object : ISettingsClientResultListener {
            @SuppressLint("MissingPermission")
            override fun onSuccess() {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            }

            override fun onFailure(message: String) {
                // it makes no sense to start location updates without proper settings.
            }
        })
    }

    fun stopReceivingLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    private fun getLastFusedLocation(listener: ILastLocationListener) {
        val getLastLocationTask = fusedLocationClient.lastLocation
        getLastLocationTask.addOnSuccessListener { location -> listener.onLastLocationSuccess(location) }
        getLastLocationTask.addOnFailureListener { e -> listener.onLastLocationFailure(e.message) }
    }

    private fun initLocationRequest() {
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
}
