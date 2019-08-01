package com.hyperdev.tungguin.ui.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.model.authentication.CityItem
import com.hyperdev.tungguin.model.authentication.ProvinceItem
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.RegisterPresenter
import com.hyperdev.tungguin.ui.view.RegisterView
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.utils.Validation.Companion.validateEmail
import com.hyperdev.tungguin.utils.Validation.Companion.validateFields
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_register_page.*
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.*

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

        baseApiService = NetworkClient.getClient(this@RegisterActivity)
            .create(BaseApiService::class.java)

        val scheduler = AppSchedulerProvider()

        presenter = RegisterPresenter(this, baseApiService, scheduler)
        presenter.getProvinceAll()
        presenter.getCityAll(provinceId)

        btn_register.setOnClickListener {
            try {
                register()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        city_items.isEnabled = false
        city_items.isClickable = false
    }

    private fun register() {

        //Memasukan DataUser User Pada Variable
        namaUser = input_name.text.toString()
        nomorUser = input_phone.text.toString()
        emailUser = input_email.text.toString()
        passUser = input_pass.text.toString()
        retypePassUser = input_retype_pass.text.toString()

        var err = 0

        if (!validateFields(namaUser)) {
            err++
            input_name.error = "Nama tidak boleh kosong !"
        }

        if (!validateFields(nomorUser)) {
            err++
            input_phone.error = "Nomor telepon tidak boleh kosong !"
        }

        if (!validateEmail(emailUser)) {
            err++
            input_email.error = "Email tidak valid !"
        }

        if (!validateFields(passUser)) {
            err++
            input_pass.error = "Password tidak boleh kosong !"
        }

        if (retypePassUser != passUser) {
            err++
            input_retype_pass.error = "Password yang dimasukan tidak sama !"
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
            presenter.postDataUser(registerMap, this)
        }
    }

    override fun displayProvince(provinceItem: List<ProvinceItem>) {
        provinceItem.forEach {
            provinceList.add(it.name.toString())
            provinceIdList.add(it.id.toString())
        }

        province_items.adapter =
            ArrayAdapter(this@RegisterActivity, android.R.layout.simple_dropdown_item_1line, provinceList)

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

        city_items.adapter =
            ArrayAdapter(this@RegisterActivity, android.R.layout.simple_spinner_dropdown_item, cityList)

        city_items.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                cityId = cityIdList[position]
            }
        }
    }

    override fun displayProgress() {
        shadow.visibility = View.VISIBLE
        progress_bar.visibility = View.VISIBLE
        btn_register.isEnabled = false
        input_name.isEnabled = false
        input_phone.isEnabled = false
        input_email.isEnabled = false
        input_pass.isEnabled = false
        input_retype_pass.isEnabled = false
        province_items.isEnabled = false
        province_items.isClickable = false
    }

    override fun hideProgress() {
        shadow.visibility = View.GONE
        progress_bar.visibility = View.GONE
        scroll_view.visibility = View.VISIBLE
        btn_register.isEnabled = true
        input_name.isEnabled = true
        input_phone.isEnabled = true
        input_email.isEnabled = true
        input_pass.isEnabled = true
        input_retype_pass.isEnabled = true
        province_items.isEnabled = true
        province_items.isClickable = true
    }

    override fun onSuccess() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finishAffinity()
    }

    override fun handleError(e: Throwable) {
        if (ConnectivityStatus.isConnected(this)) {
            when (e) {
                is HttpException -> // non 200 error codes
                    HandleError.handleError(e, e.code(), this)
                is SocketTimeoutException -> // connection errors
                    FancyToast.makeText(
                        this,
                        "Connection Timeout!",
                        FancyToast.LENGTH_SHORT,
                        FancyToast.ERROR,
                        false
                    ).show()
            }
        } else {
            FancyToast.makeText(
                this,
                "Tidak Terhubung Dengan Internet!",
                FancyToast.LENGTH_SHORT,
                FancyToast.ERROR,
                false
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}