package uz.ismoilroziboyev.nuntium.utils

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences {


    companion object {
        private var sharedPreferences: SharedPreferences? = null
        private var editor: SharedPreferences.Editor? = null
        private val FILE_NAME = "my_shared_preferences"


        fun getInstence(context: Context): MySharedPreferences {
            if (sharedPreferences == null) {
                sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
                editor = sharedPreferences?.edit()
            }
            return MySharedPreferences()
        }
    }


    fun isLocaleChanged(): Boolean {
        return sharedPreferences?.getBoolean("changed", false) ?: false
    }

    fun setLocaleChanged(boolean: Boolean) {
        editor?.putBoolean("changed", boolean)?.commit()
    }

    fun isFirstTime(): Boolean {
        return sharedPreferences?.getBoolean("isFirstTime", true) ?: true
    }

    fun setIsFirstTime(isFirstTime: Boolean) {
        editor?.putBoolean("isFirstTime", isFirstTime)?.commit()
    }

    fun isLightMode(): Boolean {
        return sharedPreferences?.getBoolean("isLightMode", true) ?: true
    }

    fun setLightMode(isLightMode: Boolean) {
        editor?.putBoolean("isLightMode", isLightMode)?.commit()
    }

    fun setLanguageNews(lang: String) {
        editor?.putString("langNews", lang)?.commit()
    }

    fun getLanguageNews(): String {
        return sharedPreferences?.getString("langNews", "English") ?: "English"
    }


}