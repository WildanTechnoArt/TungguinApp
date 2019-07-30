package com.hyperdev.tungguin.ui.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.ProfileUpdatePresenter
import com.hyperdev.tungguin.ui.view.ProfileUpdateView
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.utils.Validation.Companion.validateFields
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_change_pass.*

class ChangePassActivity : AppCompatActivity(), ProfileUpdateView.View {

    private lateinit var newPassword: String
    private lateinit var cPassword: String
    private lateinit var baseApiService: BaseApiService
    private lateinit var presenter: ProfileUpdateView.Presenter
    private lateinit var getToken: String
    private lateinit var getName: String
    private lateinit var getEmail: String
    private lateinit var getPhone: String
    private lateinit var getProvince: String
    private lateinit var getCity: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pass)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initData()

        btn_save_password.setOnClickListener {
            changePassUser()
        }
    }

    private fun initData() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        baseApiService = NetworkClient.getClient(this@ChangePassActivity)!!
            .create(BaseApiService::class.java)

        val scheduler = AppSchedulerProvider()
        presenter = ProfileUpdatePresenter(this@ChangePassActivity, this, baseApiService, scheduler)

        getToken = SharedPrefManager.getInstance(this@ChangePassActivity).token.toString()
        getName = intent?.extras?.getString("userName")!!
        getEmail = intent?.extras?.getString("userEmail")!!
        getPhone = intent?.extras?.getString("userPhone")!!
        getProvince = intent?.extras?.getString("userProvince")!!
        getCity = intent?.extras?.getString("userCity")!!
    }

    private fun changePassUser() {
        newPassword = input_new_password.text.toString()
        cPassword = input_retype_password.text.toString()

        var err = 0

        if (!validateFields(newPassword)) {
            err++
            input_new_password.error = "Masukan password baru !"
        }

        if (!validateFields(cPassword)) {
            err++
            input_retype_password.error = "Tidak boleh kosong !"
        }

        if (err == 0) {
            if (newPassword != cPassword) {
                input_retype_password.error = "Password yang dimasukan tidak sama!"
            } else {
                presenter.updatePassword(
                    "Bearer $getToken", "application/json",
                    getName, getEmail, getPhone, getProvince, getCity, newPassword, cPassword
                )
            }
        }
    }

    override fun showProgressBar() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progress_bar.visibility = View.GONE
    }

    override fun onSuccess() {
        FancyToast.makeText(this, "Password berhasil diubah", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
        finish()
    }

    override fun noInternetConnection(message: String) {
        FancyToast.makeText(this, message, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
