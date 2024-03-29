package com.hyperdev.tungguin.database

import android.annotation.SuppressLint
import android.content.Context

//Digunakan untuk mengatur penyimpanan data pada SharedPreference
class SharedPrefManager private constructor(context: Context) {

    init {
        mContext = context
    }

    companion object {

        //Nama File untuk SharedPreferenxe
        private const val TOKEN_USER = "tokenUser"
        private const val TOKEN_FCM = "tokenFCM"
        private const val DESIGNER_ID = "designerID"

        //Key untuk mengambil Value pada SharedPreference
        private const val TOKEN_KEY_ACCESS = "token"
        private const val TOKEN_KEY_FCM = "fcmAccess"
        private const val HASHED_ID = "hashedId"

        @SuppressLint("StaticFieldLeak")
        private lateinit var mContext: Context
        @SuppressLint("StaticFieldLeak")
        private var mInstance: SharedPrefManager? = null

        @Synchronized
        fun getInstance(context: Context): SharedPrefManager {
            if (mInstance == null)
                mInstance = SharedPrefManager(context)
            return mInstance!!
        }
    }

    //Mendapatkan Token yang tersimpan didalam SharedPreference
    val token: String?
        get() {
            val preferences = mContext.getSharedPreferences(TOKEN_USER, Context.MODE_PRIVATE)
            return preferences.getString(TOKEN_KEY_ACCESS, null)
        }

    val tokenFCM: String?
        get() {
            val preferences = mContext.getSharedPreferences(TOKEN_FCM, Context.MODE_PRIVATE)
            return preferences.getString(TOKEN_KEY_FCM, null)
        }

    val idDesigner: String?
        get() {
            val preferences = mContext.getSharedPreferences(DESIGNER_ID, Context.MODE_PRIVATE)
            return preferences.getString(HASHED_ID, null)
        }

    fun deleteToken(): Boolean {
        val preferences = mContext.getSharedPreferences(TOKEN_USER, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.clear()
        return editor.commit()
    }

    //Method untuk meyimpan Token pada SharedPreference
    fun storeToken(token: String): Boolean {
        val preferences = mContext.getSharedPreferences(TOKEN_USER, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(TOKEN_KEY_ACCESS, token)
        editor.apply()
        return true
    }

    fun saveDesigner(hashedId: String): Boolean {
        val preferences = mContext.getSharedPreferences(DESIGNER_ID, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(HASHED_ID, hashedId)
        editor.apply()
        return true
    }

    fun storeTokenFCM(token: String): Boolean {
        val preferences = mContext.getSharedPreferences(TOKEN_FCM, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(TOKEN_KEY_FCM, token)
        editor.apply()
        return true
    }
}