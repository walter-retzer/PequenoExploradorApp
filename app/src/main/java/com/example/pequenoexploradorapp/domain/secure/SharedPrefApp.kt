package com.example.pequenoexploradorapp.domain.secure

import android.content.Context
import android.content.SharedPreferences
import com.example.pequenoexploradorapp.R

class SharedPrefApp(context: Context) {
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences("explorador", Context.MODE_PRIVATE)
            ?: throw IllegalArgumentException("Shared Preferences Error!")

    fun saveBoolean(id: String, boolean: Boolean) {
        sharedPref.edit()?.putBoolean(id, boolean)?.apply()
    }

    fun readBoolean(id: String): Boolean {
        return sharedPref.getBoolean(id, false)
    }

    fun saveString(id: String, string: String) {
        sharedPref.edit()?.putString(id, string)?.apply()
    }

    fun readString(id: String): String {
        return sharedPref.getString(id, "") ?: ""
    }

    fun saveInt(id: String, int: Int) {
        sharedPref.edit()?.putInt(id, int)?.apply()
    }

    fun readInt(id: String): Int {
        return sharedPref.getInt(id, R.drawable.perfil01)
    }

    fun deleteId(id: String) {
        sharedPref.edit()?.remove(id)?.apply()
    }

    fun deleteAll() {
        sharedPref.edit()?.clear()?.apply()
    }
}
