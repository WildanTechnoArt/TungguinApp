package com.hyperdev.tungguin.view.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.network.NetworkUtil
import com.hyperdev.tungguin.utils.Validation.Companion.validateEmail
import kotlinx.android.synthetic.main.activity_register_page.*
import com.hyperdev.tungguin.utils.Validation.Companion.validateFields
import android.support.design.widget.Snackbar
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.register.CityItem
import com.hyperdev.tungguin.model.register.ProvinceItem
import com.hyperdev.tungguin.model.register.RegisterResponse
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.presenter.RegisterPresenter
import com.hyperdev.tungguin.repository.RegisterRepositoryImpl
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import org.json.JSONException
import java.io.IOException
import com.google.gson.Gson
import com.hyperdev.tungguin.view.RegisterView

class RegisterPage : AppCompatActivity(), RegisterView.View {

    //Deklarasi Variable
    private lateinit var namaUser: String
    private lateinit var nomorUser: String
    private lateinit var emailUser: String
    private lateinit var passUser: String
    private var provinceId: String = "null"
    private lateinit var cityId: String
    private var provinceIdList: MutableList<String> = mutableListOf()
    private var cityIdList: MutableList<String> = mutableListOf()
    private lateinit var retypePassUser: String
    private lateinit var baseApiService: BaseApiService
    private var provinceList: MutableList<String> = mutableListOf()
    private var cityList: MutableList<String> = mutableListOf()
    private lateinit var presenter: RegisterView.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_page)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        provinceList.add("Pilih Provinsi...")
        provinceIdList.add("null")
        cityList.add("Pilih Kota...")
        cityIdList.add("null")

        baseApiService = NetworkUtil.getClient(this@RegisterPage)!!
            .create(BaseApiService::class.java)

        val request = RegisterRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()

        presenter = RegisterPresenter(this, request, scheduler)
        presenter.getProvinceAll()
        presenter.getCityAll(provinceId)

        btnRegister.setOnClickListener {
            try{
                register()
            }catch(ex: Exception){
                ex.printStackTrace()
            }
        }

        user_city.isEnabled = false
        user_city.isClickable = false
    }

    private fun register(){

        //Memasukan DataUser User Pada Variable
        namaUser = firstName.text.toString()
        nomorUser = nomor.text.toString()
        emailUser = email.text.toString()
        passUser = pass.text.toString()
        retypePassUser = retypePass.text.toString()

        var err = 0

        if(!validateFields(namaUser)){
            err++
            firstName.error = "Nama tidak boleh kosong !"
        }

        if(!validateFields(nomorUser)){
            err++
            nomor.error = "Nomor telepon tidak boleh kosong !"
        }

        if(!validateEmail(emailUser)){
            err++
            email.error = "Email tidak valid !"
        }

        if(!validateFields(passUser)){
            err++
            pass.error = "Password tidak boleh kosong !"
        }

        if(retypePassUser != passUser){
            err++
            retypePass.error = "Password yang dimasukan tidak sama !"
        }

        if(err == 0){
            val registerMap = HashMap<String, String>()
            registerMap["name"] = namaUser
            registerMap["email"] = emailUser
            registerMap["password"] = passUser
            registerMap["c_password"] = retypePassUser
            registerMap["phone_number"] = nomorUser
            registerMap["province_id"] = provinceId
            registerMap["city_id"] = cityId
            disableView()
            registerRequest(registerMap)
        }
    }

    private fun registerRequest(registerMap: HashMap<String, String>){
        disableView()
        baseApiService.registerRequest(registerMap)
            .enqueue(object : Callback<RegisterResponse>{

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Log.e("RegisterPage.kt", "onFailure ERROR -> "+ t.message)
                    showSnackBarMessage("Tidak ada koneksi internet !")
                    enableView()
                }

                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.isSuccessful) {
                        Log.i("debug", "onResponse: BERHASIL")
                        try {
                            val token: String = response.body()?.getData?.token.toString()
                            val message = response.body()?.getMeta?.message
                            SharedPrefManager.getInstance(this@RegisterPage).storeToken(token)
                            @Suppress("SENSELESS_COMPARISON")
                            if(message != null){
                                Toast.makeText(this@RegisterPage, message.toString(), Toast.LENGTH_SHORT).show()
                            }
                            startActivity(Intent(this@RegisterPage, Dashboard::class.java))
                            finishAffinity()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }else{
                        enableView()
                        val gson = Gson()
                        val message = gson.fromJson(response.errorBody()?.charStream(), RegisterResponse::class.java)
                        if(message != null){
                            Toast.makeText(this@RegisterPage, message.getMeta?.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
    }

    override fun displayProvince(provinceItem: List<ProvinceItem>) {
        provinceItem.forEach {
            provinceList.add(it.name.toString())
            provinceIdList.add(it.id.toString())
        }

        user_province.adapter = ArrayAdapter(this@RegisterPage, android.R.layout.simple_dropdown_item_1line, provinceList)

        user_province.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                cityList.clear()
                cityIdList.clear()
                cityList.add("Pilih Kota...")
                cityIdList.add("null")
                provinceId = provinceIdList[position]
                if(provinceId != "null"){
                    user_city.isEnabled = true
                    user_city.isClickable = true
                    presenter.getCityAll(provinceId)
                }else{
                    user_city.isEnabled = false
                    user_city.isClickable = false
                }
            }
        }
    }

    override fun displayCity(cityItem: List<CityItem>) {
        cityItem.forEach {
            cityList.add(it.name.toString())
            cityIdList.add(it.id.toString())
        }

        user_city.adapter = ArrayAdapter(this@RegisterPage, android.R.layout.simple_spinner_dropdown_item, cityList)

        user_city.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                cityId = cityIdList[position]
            }
        }
    }

    override fun displayProgress() {
        shadow.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        btnRegister.isEnabled = false
        firstName.isEnabled = false
        nomor.isEnabled = false
        email.isEnabled = false
        pass.isEnabled = false
        retypePass.isEnabled = false
        user_province.isEnabled = false
        user_province.isClickable = false
    }

    override fun hideProgress() {
        shadow.visibility = View.GONE
        progressBar.visibility = View.GONE
        scroll.visibility = View.VISIBLE
        btnRegister.isEnabled = true
        firstName.isEnabled = true
        nomor.isEnabled = true
        email.isEnabled = true
        pass.isEnabled = true
        retypePass.isEnabled = true
        user_province.isEnabled = true
        user_province.isClickable = true
    }

    private fun disableView(){
        shadow.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        btnRegister.isEnabled = false
        firstName.isEnabled = false
        nomor.isEnabled = false
        email.isEnabled = false
        pass.isEnabled = false
        retypePass.isEnabled = false
    }

    private fun enableView(){
        shadow.visibility = View.GONE
        progressBar.visibility = View.GONE
        btnRegister.isEnabled = true
        firstName.isEnabled = true
        nomor.isEnabled = true
        email.isEnabled = true
        pass.isEnabled = true
        retypePass.isEnabled = true
    }

    private fun showSnackBarMessage(message: String) {
       Snackbar.make(register_page, message, Snackbar.LENGTH_SHORT).show()
    }
}