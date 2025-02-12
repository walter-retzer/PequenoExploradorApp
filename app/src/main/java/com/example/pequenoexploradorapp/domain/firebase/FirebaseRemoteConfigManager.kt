package com.example.pequenoexploradorapp.domain.firebase

import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

object FirebaseRemoteConfigManager {
    private val remoteConfig get() = Firebase.remoteConfig(FirebaseApp.getInstance())
    private const val FETCH_TIMEOUT = 60L

    val isRoverSpiritSearchActivated get() = getBoolean("isRoverSpiritSearchActivated")
    val isRoverCuriositySearchActivated get() = getBoolean("isRoverCuriositySearchActivated")
    val isRoverOpportunitySearchActivated get() = getBoolean("isRoverOpportunitySearchActivated")
    val isRoverPerseveranceSearchActivated get() = getBoolean("isRoverPerseveranceSearchActivated")
    val name get() = getString("name")
    val rover get() = getString("rover")

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = FETCH_TIMEOUT
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
    }

    fun fetchRemoteConfig(onSuccess: (Boolean) -> Unit, onFailure: (Boolean) -> Unit) {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Firebase Remote Config => Success ")
                    onSuccess(task.isSuccessful)
                }
            }
            .addOnFailureListener {
                println("Error Firebase Remote Config => Exception: ${it.cause}, Message: ${it.message}")
                FirebaseCrashlytics.getInstance().log("ERROR FIREBASE REMOTE CONFIG")
                FirebaseCrashlytics.getInstance().setCustomKey("${it.cause}", it.message.toString())
                FirebaseCrashlytics.getInstance().recordException(it)
                onFailure(true)
            }
    }

    private fun getString(key: String) = remoteConfig.getString(key)
    private fun getBoolean(key: String) = remoteConfig.getBoolean(key)
}
