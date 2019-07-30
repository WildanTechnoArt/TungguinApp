package com.hyperdev.tungguin.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.authentication.CityItem
import com.hyperdev.tungguin.model.authentication.ProvinceItem
import com.hyperdev.tungguin.model.profile.DataUser
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.ProfilePresenter
import com.hyperdev.tungguin.presenter.ProfileUpdatePresenter
import com.hyperdev.tungguin.ui.view.ProfileUpdateView
import com.hyperdev.tungguin.ui.view.ProfileView
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.utils.Validation.Companion.validateEmail
import com.hyperdev.tungguin.utils.Validation.Companion.validateFields
import com.shashank.sony.fancytoastlib.FancyToast
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

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        initData()

        swipe_refresh.setOnRefreshListener {
            presenter.getUserProfile("Bearer $getToken")
        }

        btn_edit_profil.setOnClickListener {
            if (btn_edit_profil.text == "Edit Profil") {
                btn_edit_profil.text = "Simpan"
                enabledView()
                btn_edit_password.text = "Tutup"
            } else if (btn_edit_profil.text == "Simpan") {
                editProfile()
            }
        }

        btn_edit_password.setOnClickListener {
            if (btn_edit_password.text == "Tutup") {
                disabledView()
                btn_edit_password.text = "Ubah Password"
            } else if (btn_edit_password.text == "Ubah Password") {
                val intent = Intent(this, ChangePassActivity::class.java)
                intent.putExtra("userName", customer_name.text.toString())
                intent.putExtra("userEmail", customer_email.text.toString())
                intent.putExtra("userPhone", customer_phone.text.toString())
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

        val scheduler = AppSchedulerProvider()

        presenter = ProfilePresenter(this, this@ProfileActivity, baseApiService, scheduler)
        presenter2 = ProfileUpdatePresenter(this@ProfileActivity, this, baseApiService, scheduler)
        presenter.getProvinceAll()
        presenter.getCityAll(provinceId)
    }

    private fun editProfile() {
        namaUser = customer_name.text.toString()
        nomorUser = customer_phone.text.toString()
        emailUser = customer_email.text.toString()

        var err = 0

        if (!validateFields(namaUser)) {
            err++
            customer_name.error = "Nama tidak boleh kosong !"
        }

        if (!validateFields(nomorUser)) {
            err++
            customer_phone.error = "Nomor telepon tidak boleh kosong !"
        }

        if (!validateEmail(emailUser)) {
            err++
            customer_email.error = "Email tidak valid !"
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
        customer_name.isEnabled = true
        customer_email.isEnabled = true
        customer_phone.isEnabled = true
        province_layout.visibility = View.GONE
        city_layout.visibility = View.GONE
        province_items.visibility = View.VISIBLE
        city_items.visibility = View.VISIBLE
        name_layout.isCounterEnabled = true
    }

    @SuppressLint("SetTextI18n")
    private fun disabledView() {
        btn_edit_profil.text = "Edit Profil"
        customer_name.isEnabled = false
        customer_email.isEnabled = false
        province_layout.isEnabled = false
        province_layout.visibility = View.VISIBLE
        city_layout.visibility = View.VISIBLE
        province_items.visibility = View.GONE
        city_items.visibility = View.GONE
        name_layout.isCounterEnabled = false
    }

    override fun displayProvince(provinceItem: List<ProvinceItem>) {
        provinceItem.forEach {
            provinceList.add(it.name.toString())
            provinceIdList.add(it.id.toString())
        }

        province_items.adapter =
            ArrayAdapter(this@ProfileActivity, android.R.layout.simple_dropdown_item_1line, provinceList)

        province_items.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                cityList.clear()
                cityIdList.clear()
                cityList.add("Pilih Kota...")
                cityIdList.add("null")
                provinceId = provinceIdList[position]
                if (provinceId != "null") {
                    city_items.isEnabled = true
                    city_items.isClickable = true
                    presenter.getCityAll(provinceId)
                } else {
                    city_items.isEnabled = false
                    city_items.isClickable = false
                }
            }
        }
    }

    override fun displayCity(cityItem: List<CityItem>) {
        cityItem.forEach {
            cityList.add(it.name.toString())
            cityIdList.add(it.id.toString())
        }

        city_items.adapter = ArrayAdapter(this@ProfileActivity, android.R.layout.simple_spinner_dropdown_item, cityList)

        city_items.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                cityId = cityIdList[position]
            }
        }
    }

    override fun displayProfile(profileItem: DataUser) {
        customer_name.setText(profileItem.name.toString())
        customer_email.setText(profileItem.email.toString())
        customer_phone.setText(profileItem.phoneNumber.toString())
        customer_province.setText(profileItem.province?.name.toString())
        customer_city.setText(profileItem.city?.name.toString())
        getProvinceId = profileItem.province?.id.toString()
        getCityId = profileItem.city?.id.toString()
    }

    // Menampilkan Progress saat memuat profil user
    override fun displayProgress() {
        btn_edit_profil.isEnabled = false
        btn_edit_password.isEnabled = false
        swipe_refresh.isRefreshing = true
    }

    // Progress akan menghilang setelah data user berhasil dimuat
    override fun hideProgress() {
        btn_edit_profil.isEnabled = true
        btn_edit_password.isEnabled = true
        swipe_refresh.isRefreshing = false
    }

    // Menampilkan Progress saat user ingin mengubah data profil
    override fun showProgressBar() {
        swipe_refresh.isRefreshing = true
    }

    // Progress akan menghilang setelah user berhasil mengubah data profil
    override fun hideProgressBar() {
        swipe_refresh.isRefreshing = false
    }

    @SuppressLint("SetTextI18n")
    override fun onSuccess() {
        btn_edit_password.text = "Ubah Password"
        presenter.getUserProfile("Bearer $getToken")
        disabledView()
        FancyToast.makeText(
            this,
            "Profil berhasil diubah",
            FancyToast.LENGTH_SHORT,
            FancyToast.SUCCESS,
            false
        ).show()
    }

    override fun noInternetConnection(message: String) {
        FancyToast.makeText(
            this,
            message,
            FancyToast.LENGTH_SHORT,
            FancyToast.SUCCESS,
            false
        ).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}