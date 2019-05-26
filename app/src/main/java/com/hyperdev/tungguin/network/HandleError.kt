package com.hyperdev.tungguin.network

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.view.ui.LoginPage
import retrofit2.HttpException

class HandleError {

    companion object {
        fun handleError(e: HttpException, code: Int, context: Context){
            if(code == 401){
                Toast.makeText(context, "Sesi Anda Sudah Berakhir, Silakan Login Kembali", Toast.LENGTH_LONG).show()
                SharedPrefManager.getInstance(context).deleteToken()
                val intent = Intent(context, LoginPage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
                (context as Activity).finish()
            }else{
                // non 200 and 401 error codes
                val gson = Gson()
                val response = gson.fromJson(e.response().errorBody()?.charStream(), Response::class.java)
                val message = response.meta?.message.toString()
                if(message == "null"){
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}