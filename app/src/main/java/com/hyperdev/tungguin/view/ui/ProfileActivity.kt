package com.hyperdev.tungguin.view.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.profile.DataUser
import com.hyperdev.tungguin.model.profile.ProfileResponse
import com.hyperdev.tungguin.model.register.CityItem
import com.hyperdev.tungguin.model.register.ProvinceItem
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkUtil
import com.hyperdev.tungguin.presenter.ProfilePresenter
import com.hyperdev.tungguin.repository.ProfileRepositoryImpl
import com.hyperdev.tungguin.repository.RegisterRepositoryImpl
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.utils.Validation.Companion.validateEmail
import com.hyperdev.tungguin.utils.Validation.Companion.validateFields
import com.hyperdev.tungguin.view.ProfileView
import kotlinx.android.synthetic.main.activity_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity(), ProfileView.View {

    //Deklarasi Varibale
    private lateinit var presenter: ProfileView.Presenter
    private lateinit var baseApiService: BaseApiService
    private lateinit var getToken: String
    private lateinit var namaUser: String
    private lateinit var nomorUser: String
    private lateinit var emailUser: String
    private var provinceId: String = "null"
    private lateinit var cityId: String
    private var provinceIdList: MutableList<String> = mutableListOf()
    private var cityIdList: MutableList<String> = mutableListOf()
    private var provinceList: MutableList<String> = mutableListOf()
    private var cityList: MutableList<String> = mutableListOf()
    private lateinit var getProvinceId: String
    private lateinit var getCityId: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //Inisialisasi Toolbar dan Menampilkan Menu Home pada Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        provinceList.add("Pilih Provinsi...")
        provinceIdList.add("null")
        cityList.add("Pilih Kota...")
        cityIdList.add("null")

        swipeRefresh.setOnRefreshListener {
            presenter.getUserProfile("Bearer $getToken")
        }

        btnEditProfil.setOnClickListener {
            if(btnEditProfil.text == "Edit Profil"){
                btnEditProfil.text = "Simpan"
                enabledView()
                btnEditPass.text = "Tutup"
            }else if(btnEditProfil.text == "Simpan"){
                editProfile()
            }
        }

        btnEditPass.setOnClickListener {
            if(btnEditPass.text == "Tutup"){
                disabledView()
                btnEditPass.text = "Ubah Password"
            }else if(btnEditPass.text == "Ubah Password"){
                val intent = Intent(this@ProfileActivity, ChangePassActivity::class.java)
                intent.putExtra("userName", my_name.text.toString())
                intent.putExtra("userEmail", my_email.text.toString())
                intent.putExtra("userPhone", phone_number.text.toString())
                intent.putExtra("userProvince", getProvinceId)
                intent.putExtra("userCity", getCityId)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData(){

        val connectivity  = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivity.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {

            baseApiService = NetworkUtil.getClient()!!
                .create(BaseApiService::class.java)

            getToken = SharedPrefManager.getInstance(this@ProfileActivity).token.toString()

            val request = ProfileRepositoryImpl(baseApiService)
            val request2 = RegisterRepositoryImpl(baseApiService)
            val scheduler = AppSchedulerProvider()

            presenter = ProfilePresenter(this, this@ProfileActivity, request,request2, scheduler)
            presenter.getUserProfile("Bearer $getToken")
            presenter.getProvinceAll()
            presenter.getCityAll(provinceId)

        } else {
            swipeRefresh.isRefreshing = false
            Snackbar.make(profile_layout, "Tidak terhubung dengan internet !", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun editProfile(){
        namaUser = my_name.text.toString()
        nomorUser = phone_number.text.toString()
        emailUser = my_email.text.toString()

        var err = 0

        if(!validateFields(namaUser)){
            err++
            my_name.error = "Nama tidak boleh kosong !"
        }

        if(!validateFields(nomorUser)){
            err++
            phone_number.error = "Nomor telepon tidak boleh kosong !"
        }

        if(!validateEmail(emailUser)){
            err++
            my_email.error = "Email tidak valid !"
        }

        if(err == 0){
            editRequest(namaUser, emailUser, nomorUser, provinceId, cityId)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun enabledView(){
        my_name.isEnabled = true
        my_email.isEnabled = true
        phone_number.isEnabled = true
        provinceLayout.visibility = View.GONE
        cityLayout.visibility = View.GONE
        user_province.visibility = View.VISIBLE
        user_city.visibility = View.VISIBLE
        nameLayout.isCounterEnabled = true
    }

    @SuppressLint("SetTextI18n")
    private fun disabledView(){
        btnEditProfil.text = "Edit Profil"
        my_name.isEnabled = false
        my_email.isEnabled = false
        phone_number.isEnabled = false
        provinceLayout.visibility = View.VISIBLE
        cityLayout.visibility = View.VISIBLE
        user_province.visibility = View.GONE
        user_city.visibility = View.GONE
        nameLayout.isCounterEnabled = false
    }

    private fun editRequest(name: String, email: String, number: String, province: String, city: String){
        baseApiService.updateProfile("Bearer $getToken", name, email, number, province, city)
            .enqueue(object : Callback<ProfileResponse> {

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Log.e("RegisterPage.kt", "onFailure ERROR -> "+ t.message)
                    showSnackBarMessage("Tidak ada koneksi internet !")
                }

                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                    if (response.isSuccessful) {
                        Log.i("debug", "onResponse: BERHASIL")
                        btnEditPass.text = "Ubah Password"
                        presenter.getUserProfile("Bearer $getToken")
                        disabledView()
                        Toast.makeText(this@ProfileActivity, "Data berhasil diubah", Toast.LENGTH_SHORT).show()
                    }else{
                        val gson = Gson()
                        val message = gson.fromJson(response.errorBody()?.charStream(), ProfileResponse::class.java)
                        if(message != null){
                            Toast.makeText(this@ProfileActivity, message.getMeta?.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar.make(profile_layout, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun displayProvince(provinceItem: List<ProvinceItem>) {
        provinceItem.forEach {
            provinceList.add(it.name.toString())
            provinceIdList.add(it.id.toString())
        }

        user_province.adapter = ArrayAdapter(this@ProfileActivity, android.R.layout.simple_dropdown_item_1line, provinceList)

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

        user_city.adapter = ArrayAdapter(this@ProfileActivity, android.R.layout.simple_spinner_dropdown_item, cityList)

        user_city.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                cityId = cityIdList[position]
            }
        }
    }

    override fun displayProfile(profileItem: DataUser){
        my_name.setText(profileItem.name.toString())
        my_email.setText(profileItem.email.toString())
        phone_number.setText(profileItem.phoneNumber.toString())
        my_province.setText(profileItem.province?.name.toString())
        my_city.setText(profileItem.city?.name.toString())
        getProvinceId = profileItem.province?.id.toString()
        getCityId = profileItem.city?.id.toString()
    }

    override fun displayProgress() {
        btnEditProfil.isEnabled = false
        btnEditPass.isEnabled = false
        swipeRefresh.isRefreshing = true
    }

    override fun hideProgress() {
        btnEditProfil.isEnabled = true
        btnEditPass.isEnabled = true
        swipeRefresh.isRefreshing = false
    }

    //Digunakan untuk menentukan aksi pada tombol home
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}