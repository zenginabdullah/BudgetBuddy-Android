package com.budgetbuddy.app.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("budget_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CURRENCY = "currency"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_NOTIFICATIONS = "notifications_enabled"
    }

    fun setCurrency(currency: String) {
        prefs.edit().putString(KEY_CURRENCY, currency).apply()
    }

    fun getCurrency(): String {
        return prefs.getString(KEY_CURRENCY, "₺") ?: "₺"
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    fun isDarkModeEnabled(): Boolean {
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply()
    }

    fun areNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS, true)
    }

    fun setDailyLimit(limit: Double) {
        prefs.edit().putFloat("daily_limit", limit.toFloat()).apply()
    }

    fun getDailyLimit(): Double {
        return prefs.getFloat("daily_limit", 0f).toDouble()
    }
}
