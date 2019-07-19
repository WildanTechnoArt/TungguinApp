package com.hyperdev.tungguin.network

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build

class ConnectivityStatus{
    companion object {
        fun isConnected(context: Context): Boolean {
            val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            @Suppress("DEPRECATION")
            val connection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager.activeNetwork
            } else {
                manager.activeNetworkInfo
            }
            return connection != null
        }
    }
}