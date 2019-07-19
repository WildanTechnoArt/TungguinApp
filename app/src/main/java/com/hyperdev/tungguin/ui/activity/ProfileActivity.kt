package com.hyperdev.tungguin.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.profile.DataUser
import com.hyperdev.tungguin.model.authentication.CityItem
import com.hyperdev.tungguin.model.authentication.ProvinceItem
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.ProfilePresenter
import com.hyperdev.tungguin.presenter.ProfileUpdatePresenter
import com.hyperdev.tungguin.repository.profile.ProfileRepositoryImpl
import com.hyperdev.tungguin.repository.authentication.AuthRepositoryImp
import com.hyperdev.tungguin.ui.view.ProfileUpdateView
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.utils.Validation.Companion.validateEmail
import com.hyperdev.tungguin.utils.Validation.Companion.validateFields
import com.hyperdev.tungguin.ui.view.ProfileView
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity(), ProfileView.View, ProfileUpdateView.View {

    //Deklarasi Varibale
    private lateinit var presenter: ProfileView.Presenter
    private lateinit var presenter2: ProfileUpdateView.Presenter
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

        initData()

        swipeRefresh.setOnRefreshListener {
            presenter.getUserProfile("Bearer $getToken")
        }

        btnEditProfil.setOnClickListener {
            if (btnEditProfil.text == "Edit Profil") {
                btnEditProfil.text = "Simpan"
                enabledView()
                btnEditPass.text = "Tutup"
            } else if (btnEditProfil.text == "Simpan") {
                editProfile()
            }
        }

        btnEditPass.setOnClickListener {
            if (btnEditPass.text == "Tutup") {
                disabledView()
                btnEditPass.text = "Ubah Password"
            } else if (btnEditPass.text == "Ubah Password") {
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
        presenter.getUserProfile("Bearer $getToken")
    }

    private fun initData() {
        provinceList.add("Pilih Provinsi...")
        provinceIdList.add("null")
        cityList.add("Pilih Kota...")
        cityIdList.add("null")

        baseApiService = NetworkClient.getClient(this@ProfileActivity)!!
            .create(BaseApiService::class.java)

        getToken = SharedPrefManager.getInstance(this@ProfileActivity).token.toString()

        val request = ProfileRepositoryImpl(baseApiService)
        val request2 = AuthRepositoryImp(baseApiService)
        val request3 = ProfileRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()

        presenter = ProfilePresenter(this, this@ProfileActivity, request, request2, scheduler)
        presenter2 = ProfileUpdatePresenter(this@ProfileActivity, this, request3, scheduler)
        presenter.getProvinceAll()
        presenter.getCityAll(provinceId)
    }

    private fun editProfile() {
        namaUser = my_name.text.toString()
        nomorUser = phone_number.text.toString()
        emailUser = my_email.text.toString()

        var err = 0

        if (!validateFields(namaUser)) {
            err++
            my_name.error = "Nama tidak boleh kosong !"
        }

        if (!validateFields(nomorUser)) {
            err++
            phone_number.error = "Nomor telepon tidak boleh kosong !"
        }

        if (!validateEmail(emailUser)) {
            err++
            my_email.error = "Email tidak valid !"
        }

        if (err == 0) {
            presenter2.updateProfile(
                "Bearer $getToken",
                "application/json",
                namaUser,
                emailUser,
                nomorUser,
                provinceId,
                cityId
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun enabledView() {
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
    private fun disabledView() {
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

    override fun displayProvince(provinceItem: List<ProvinceItem>) {
        provinceItem.forEach {
            provinceList.add(it.name.toString())
            provinceIdList.add(it.id.toString())
        }

        user_province.adapter =
            ArrayAdapter(this@ProfileActivity, android.R.layout.simple_dropdown_item_1line, provinceList)

        user_province.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                cityList.clear()
                cityIdList.clear()
                cityList.add("Pilih Kota...")
                cityIdList.add("null")
                provinceId = provinceIdList[position]
                if (provinceId != "null") {
                    user_city.isEnabled = true
                    user_city.isClickable = true
                    presenter.getCityAll(provinceId)
                } else {
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

    override fun displayProfile(profileItem: DataUser) {
        my_name.setText(profileItem.name.toString())
        my_email.setText(profileItem.email.toString())
        phone_number.setText(profileItem.phoneNumber.toString())
        my_province.setText(profileItem.province?.name.toString())
        my_city.setText(profileItem.city?.name.toString())
        getProvinceId = profileItem.province?.id.toString()
        getCityId = profileItem.city?.id.toString()
    }

    // Menampilkan Progress saat memuat profil user
    override fun displayProgress() {
        btnEditProfil.isEnabled = false
        btnEditPass.isEnabled = false
        swipeRefresh.isRefreshing = true
    }

    // Progress akan menghilang setelah data user berhasil dimuat
    override fun hideProgress() {
        btnEditProfil.isEnabled = true
        btnEditPass.isEnabled = true
        swipeRefresh.isRefreshing = false
    }

    // Menampilkan Progress saat user ingin mengubah data profil
    override fun showProgressBar() {
        swipeRefresh.isRefreshing = true
    }

    // Progress akan menghilang setelah user berhasil mengubah data profil
    override fun hideProgressBar() {
        swipeRefresh.isRefreshing = false
    }

    @SuppressLint("SetTextI18n")
    override fun onSuccess() {
        btnEditPass.text = "Ubah Password"
        presenter.getUserProfile("Bearer $getToken")
        disabledView()
        Toast.makeText(this@ProfileActivity, "Profil berhasil diubah", Toast.LENGTH_SHORT).show()
    }

    override fun noInternetConnection(message: String) {
        Snackbar.make(profile_layout, message, Snackbar.LENGTH_SHORT).show()
    }

    //Digunakan untuk menentukan aksi pada tombol home
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}