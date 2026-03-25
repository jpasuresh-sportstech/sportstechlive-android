package com.sportstechbrands.sportstechlive.data.local

import android.content.Context

class TokenManager private constructor(context: Context) {

    private val prefs = context.getSharedPreferences("st_prefs", Context.MODE_PRIVATE)

    var accessToken: String?
        get() = prefs.getString("access_token", null)
        set(v) = prefs.edit().putString("access_token", v).apply()

    var refreshToken: String?
        get() = prefs.getString("refresh_token", null)
        set(v) = prefs.edit().putString("refresh_token", v).apply()

    var userName: String?
        get() = prefs.getString("user_name", null)
        set(v) = prefs.edit().putString("user_name", v).apply()

    var userEmail: String?
        get() = prefs.getString("user_email", null)
        set(v) = prefs.edit().putString("user_email", v).apply()

    var userLevel: String?
        get() = prefs.getString("user_level", null)
        set(v) = prefs.edit().putString("user_level", v).apply()

    fun isLoggedIn() = accessToken != null

    fun clear() = prefs.edit().clear().apply()

    companion object {
        @Volatile private var INSTANCE: TokenManager? = null

        fun getInstance(context: Context): TokenManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: TokenManager(context.applicationContext).also { INSTANCE = it }
            }
    }
}
