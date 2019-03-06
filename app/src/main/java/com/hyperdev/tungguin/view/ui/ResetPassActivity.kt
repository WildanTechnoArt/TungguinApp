package com.hyperdev.tungguin.view.ui

import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.model.login.LoginResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkUtil
import com.hyperdev.tungguin.utils.Validation.Companion.validateEmail
import kotlinx.android.synthetic.main.activity_reset_pass.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ResetPassActivity : AppCompatActivity() {

    //Deklarasi Variable
    private lateinit var emailUser: String
    private lateinit var baseApiService: BaseApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_pass)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        baseApiService = NetworkUtil.getClient()!!.create(BaseApiService::class.java)

        btnResetPass.setOnClickListener {
            emailUser = email.text.toString()

            var err = 0

            if(!validateEmail(emailUser)){
                err++
                email.error = "Email tidak valid !"
            }

            if(err == 0){
                disableView()
                resetPassResponse()
            }else{
                enableView()
            }
        }
    }

    private fun resetPassResponse(){

        baseApiService.forgotPassword(emailUser)
            .enqueue(object : Callback<LoginResponse>{
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("ResetPassActivity.kt", "onFailure ERROR -> "+ t.message)
                    showSnackBarMessage("Tidak terhubung dengan internet !")
                    enableView()
                }

                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {

                    if (response.isSuccessful) {
                        Log.i("debug", "onResponse: BERHASIL")
                        try {
                            val message = response.body()?.getMeta?.message
                            enableView()
                            if(message != null){
                                Toast.makeText(this@ResetPassActivity, message.toString(), Toast.LENGTH_LONG).show()
                            }
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
                            Toast.makeText(this@ResetPassActivity, message.getMeta?.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
    }

    private fun disableView(){
        shadow.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        btnResetPass.isEnabled = false
        email.isEnabled = false
    }

    private fun enableView(){
        shadow.visibility = View.GONE
        progressBar.visibility = View.GONE
        btnResetPass.isEnabled = true
        email.isEnabled = true
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar.make(forgot_pass_layout, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
