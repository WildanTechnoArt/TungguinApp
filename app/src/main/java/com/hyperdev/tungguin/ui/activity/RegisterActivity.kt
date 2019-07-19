package com.hyperdev.tungguin.ui.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.utils.Validation.Companion.validateEmail
import kotlinx.android.synthetic.main.activity_register_page.*
import com.hyperdev.tungguin.utils.Validation.Companion.validateFields
import com.google.android.material.snackbar.Snackbar
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.hyperdev.tungguin.model.authentication.CityItem
import com.hyperdev.tungguin.model.authentication.ProvinceItem
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.presenter.RegisterPresenter
import com.hyperdev.tungguin.repository.authentication.AuthRepositoryImp
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import java.util.*
import com.hyperdev.tungguin.ui.view.RegisterView

class RegisterActivity : AppCompatActivity(), RegisterView.View {

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

        baseApiService = NetworkClient.getClient(this@RegisterActivity)!!
            .create(BaseApiService::class.java)

        val request = AuthRepositoryImp(baseApiService)
        val scheduler = AppSchedulerProvider()

        presenter = RegisterPresenter(this@RegisterActivity, this, request, scheduler)
        presenter.getProvinceAll()
        presenter.getCityAll(provinceId)

        btnRegister.setOnClickListener {
            try {
                register()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        user_city.isEnabled = false
        user_city.isClickable = false
    }

    private fun register() {

        //Memasukan DataUser User Pada Variable
        namaUser = firstName.text.toString()
        nomorUser = nomor.text.toString()
        emailUser = email.text.toString()
        passUser = pass.text.toString()
        retypePassUser = retypePass.text.toString()

        var err = 0

        if (!validateFields(namaUser)) {
            err++
            firstName.error = "Nama tidak boleh kosong !"
        }

        if (!validateFields(nomorUser)) {
            err++
            nomor.error = "Nomor telepon tidak boleh kosong !"
        }

        if (!validateEmail(emailUser)) {
            err++
            email.error = "Email tidak valid !"
        }

        if (!validateFields(passUser)) {
            err++
            pass.error = "Password tidak boleh kosong !"
        }

        if (retypePassUser != passUser) {
            err++
            retypePass.error = "Password yang dimasukan tidak sama !"
        }

        if (err == 0) {
            val registerMap = HashMap<String, String>()
            registerMap["name"] = namaUser
            registerMap["email"] = emailUser
            registerMap["password"] = passUser
            registerMap["c_password"] = retypePassUser
            registerMap["phone_number"] = nomorUser
            registerMap["province_id"] = provinceId
            registerMap["city_id"] = cityId
            presenter.postDataUser(registerMap)
        }
    }

    override fun displayProvince(provinceItem: List<ProvinceItem>) {
        provinceItem.forEach {
            provinceList.add(it.name.toString())
            provinceIdList.add(it.id.toString())
        }

        user_province.adapter =
            ArrayAdapter(this@RegisterActivity, android.R.layout.simple_dropdown_item_1line, provinceList)

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

        user_city.adapter = ArrayAdapter(this@RegisterActivity, android.R.layout.simple_spinner_dropdown_item, cityList)

        user_city.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                cityId = cityIdList[position]
            }
        }
    }

    override fun displayProgress() {
        shadow.visibility = View.VISIBLE
        progress_bar.visibility = View.VISIBLE
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
        progress_bar.visibility = View.GONE
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

    override fun onSuccess() {
        startActivity(Intent(this@RegisterActivity, DashboardActivity::class.java))
        finishAffinity()
    }

    override fun noInternetConnection(message: String) {
        Snackbar.make(register_page, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}