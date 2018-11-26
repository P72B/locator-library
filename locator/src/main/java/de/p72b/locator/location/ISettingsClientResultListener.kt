package de.p72b.locator.location

interface ISettingsClientResultListener {
    fun onSuccess()

    fun onFailure(message: String)
}