package com.example.pequenoexploradorapp.domain.firebase

import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

object FirebaseRemoteConfigManager {
    private val remoteConfig get() = Firebase.remoteConfig(FirebaseApp.getInstance())
    private const val FETCH_TIMEOUT = 60L

    val isActivated get() = getBoolean("isActivated")
    val isActivatedMenu get() = getBoolean("isActivatedMenu")
    val name get() = getString("name")
    val rover get() = getString("rover")

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = FETCH_TIMEOUT
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
    }

    fun fetchRemoteConfig(onComplete: (Boolean) -> Unit) {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Firebase Remote Config => Success ")
                    onComplete(task.isSuccessful)
                } else {
                    println("Firebase Remote Config => Error")
                }
            }
            .addOnFailureListener {
                println("Firebase Remote Config => Exception: ${it.message}")
            }
    }

    private fun getString(key: String) = remoteConfig.getString(key)
    private fun getBoolean(key: String) = remoteConfig.getBoolean(key)
}
