package com.hyperdev.tungguin.view.ui

import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.contactus.ContactUsResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkUtil
import com.hyperdev.tungguin.utils.Validation.Companion.validateFields
import kotlinx.android.synthetic.main.activity_contact_us.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ContactUsActivity : AppCompatActivity() {

    // Deklarasi Variable
    private lateinit var getTitleRequest: String
    private lateinit var getContentRequest: String
    private lateinit var baseApiService: BaseApiService
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        baseApiService = NetworkUtil.getClient(this@ContactUsActivity)!!
            .create(BaseApiService::class.java)

        token = SharedPrefManager.getInstance(this).token.toString()

        send_request.setOnClickListener {
            try{
                contactRequest()
            }catch(ex: Exception){
                ex.printStackTrace()
            }
        }
    }

    private fun contactRequest(){

        //Memasukan DataUser User Pada Variable
        getTitleRequest = title_request.text.toString()
        getContentRequest = request_content.text.toString()

        var err = 0

        if(!validateFields(getTitleRequest)){
            err++
            Toast.makeText(this@ContactUsActivity, "Judul permintaan tida boleh kosong !", Toast.LENGTH_SHORT).show()
        }
        else if(!validateFields(getContentRequest)){
            err++
            Toast.makeText(this@ContactUsActivity, "Isi permintaan tidak boleh kosong !", Toast.LENGTH_SHORT).show()
        }

        if(err == 0){
            contactRequestResponse("Bearer $token", getTitleRequest, getContentRequest)
            disableView()
        }
    }

    private fun contactRequestResponse(token: String, title: String, content: String){
        disableView()
        baseApiService.contactUsRequesr(token, "application/json", title, content)
            .enqueue(object : Callback<ContactUsResponse> {

                override fun onFailure(call: Call<ContactUsResponse>, t: Throwable) {
                    showSnackBarMessage("Tidak ada koneksi Internet!")
                    enableView()
                }

                override fun onResponse(call: Call<ContactUsResponse>, response: Response<ContactUsResponse>) {
                    if (response.isSuccessful) {
                        try {
                            val message: String = response.body()?.meta?.message.toString()
                            @Suppress("SENSELESS_COMPARISON")
                            if(message != null){
                                Toast.makeText(this@ContactUsActivity, message, Toast.LENGTH_SHORT).show()
                            }
                            enableView()
                            title_request.setText("")
                            request_content.setText("")
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }else{
                        enableView()
                        val gson = Gson()
                        val message = gson.fromJson(response.errorBody()?.charStream(), ContactUsResponse::class.java)
                        if(message != null){
                            Toast.makeText(this@ContactUsActivity, message.meta?.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
    }

    private fun disableView(){
        shadow.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        send_request.isEnabled = false
        title_request.isEnabled = false
        request_content.isEnabled = false
    }

    private fun enableView(){
        shadow.visibility = View.GONE
        progressBar.visibility = View.GONE
        send_request.isEnabled = true
        title_request.isEnabled = true
        request_content.isEnabled = true
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar.make(contact_layout, message, Snackbar.LENGTH_SHORT).show()
    }
}
