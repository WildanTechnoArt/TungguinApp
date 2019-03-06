package com.hyperdev.tungguin.view.ui

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.Data
import com.hyperdev.tungguin.model.login.LoginResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkUtil
import com.hyperdev.tungguin.utils.Validation.Companion.validateEmail
import com.hyperdev.tungguin.utils.Validation.Companion.validateFields
import kotlinx.android.synthetic.main.activity_login_page.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class LoginPage : AppCompatActivity() {

    //Deklarasi Variable
    private lateinit var emailUser: String
    private lateinit var passUser: String
    private lateinit var baseApiService: BaseApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        baseApiService = NetworkUtil.getClient()!!
            .create(BaseApiService::class.java)

        btnLogin.setOnClickListener {
            val connectivity  = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivity.activeNetworkInfo

            if (networkInfo != null && networkInfo.isConnected) {
                disableView()
                login()
            } else {
                Toast.makeText(this@LoginPage, "Tidak terhubung dengan internet !", Toast.LENGTH_SHORT).show()
            }
        }

        forgot_pass.setOnClickListener {
            startActivity(Intent(this@LoginPage, ResetPassActivity::class.java))
        }
    }

    private fun login(){

        //Memasukan DataUser User Pada Variable
        emailUser = email.text.toString()
        passUser = pass.text.toString()

        var err = 0

        if(!validateEmail(emailUser)){
            err++
            email.error = "Email tidak valid !"
        }

        if(!validateFields(passUser)){
            err++
            pass.error = "Password tidak boleh kosong !"
        }

        if(err == 0){
            loginRequest(emailUser, passUser)
        }else{
            enableView()
        }
    }

    private fun loginRequest(emailUser: String, passUser: String){
        baseApiService.loginRequest(emailUser, passUser)
            .enqueue(object : Callback<LoginResponse> {

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("RegisterPage.kt", "onFailure ERROR -> "+ t.message)
                    showSnackBarMessage("Tidak terhubung dengan internet !")
                    enableView()
                }

                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {

                    if (response.isSuccessful) {
                        Log.i("debug", "onResponse: BERHASIL")
                        try {
                            val data: Data = response.body()?.getData!!
                            val message = response.body()?.getMeta?.message
                            if(message != null){
                                Toast.makeText(this@LoginPage, message.toString(), Toast.LENGTH_SHORT).show()
                            }
                            SharedPrefManager.getInstance(this@LoginPage).storeToken(data.token.toString())
                            startActivity(Intent(this@LoginPage, Dashboard::class.java))
                            finishAffinity()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.i("debug", "onResponse: GA BERHASIL")
                        enableView()
                        val gson = Gson()
                        val message = gson.fromJson(response.errorBody()?.charStream(), LoginResponse::class.java)
                        if(message != null){
                            Toast.makeText(this@LoginPage, message.getMeta?.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
    }

    private fun disableView(){
        shadow.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        btnLogin.isEnabled = false
        email.isEnabled = false
        pass.isEnabled = false
    }

    private fun enableView(){
        shadow.visibility = View.GONE
        progressBar.visibility = View.GONE
        btnLogin.isEnabled = true
        email.isEnabled = true
        pass.isEnabled = true
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar.make(login_page, message, Snackbar.LENGTH_SHORT).show()
    }
}