package de.p72b.locator.location

interface IPermissionListener {
    fun onGranted()

    fun onDenied(donNotAskAgain: Boolean)
}