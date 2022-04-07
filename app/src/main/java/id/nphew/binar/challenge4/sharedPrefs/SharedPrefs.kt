package id.nphew.binar.challenge4.sharedPrefs

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_ID, Context.MODE_PRIVATE)

    fun setLoggedInUser(email: String, password: String) {
        val editor = prefs.edit()
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_PASSWORD, password)
        editor.apply()
    }

    fun getLoggedInUser(): String? {
        return if (prefs.contains(KEY_EMAIL)) {
            prefs.getString(KEY_PASSWORD, null)
        } else {
            clearLoggedInUser()
            null
        }
    }

    fun loggedInEmail(): String? {
        val email = prefs.getString(KEY_EMAIL, null)
        return email.toString()
    }

    fun checkAuth (): Boolean {
        return getLoggedInUser() != null
    }

    fun clearLoggedInUser() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        const val PREFS_ID = "auth_prefs"
        const val KEY_EMAIL = "key_email"
        const val KEY_PASSWORD = "key_password"
    }
}