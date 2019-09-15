package com.hyperdev.tungguin.ui.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.network.*
import com.hyperdev.tungguin.presenter.LoginPresenter
import com.hyperdev.tungguin.ui.view.LoginView
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.utils.Validation.Companion.validateEmail
import com.hyperdev.tungguin.utils.Validation.Companion.validateFields
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_login_page.*
import retrofit2.HttpException
import java.net.SocketTimeoutException

class LoginActivity : AppCompatActivity(), LoginView.View {

    //Deklarasi Variable
    private lateinit var emailUser: String
    private lateinit var passUser: String
    private lateinit var presenter: LoginView.Presenter
    private lateinit var baseApiService: BaseApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        baseApiService = NetworkClient.getClient(this)
            .create(BaseApiService::class.java)

        val scheduler = AppSchedulerProvider()
        presenter = LoginPresenter(this, baseApiService, scheduler)

        btn_login.setOnClickListener {
            login()
        }

        tv_forgot_pass.setOnClickListener {
            startActivity(Intent(this, ForgotPassActivity::class.java))
        }
    }

    private fun login() {

        emailUser = input_email.text.toString()
        passUser = input_pass.text.toString()

        var err = 0

        if (!validateEmail(emailUser)) {
            err++
            input_email.error = "Email tidak valid !"
        }

        if (!validateFields(passUser)) {
            err++
            input_pass.error = "Password tidak boleh kosong !"
        }

        if (err == 0) {
            presenter.loginUser(emailUser, passUser, this)
        }
    }

    override fun showProgressBar() {
        shadow.visibility = View.VISIBLE
        progress_bar.visibility = View.VISIBLE
        btn_login.isEnabled = false
        input_email.isEnabled = false
        input_pass.isEnabled = false
    }

    override fun hideProgressBar() {
        shadow.visibility = View.GONE
        progress_bar.visibility = View.GONE
        btn_login.isEnabled = true
        input_email.isEnabled = true
        input_pass.isEnabled = true
    }

    override fun onSuccess() {
        startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
        finishAffinity()
    }

    override fun handleError(e: Throwable) {
        if (ConnectivityStatus.isConnected(this)) {
            when (e) {
                is HttpException -> {
                    val gson = Gson()
                    val response = gson.fromJson(e.response()?.errorBody()?.charStream(), Response::class.java)
                    val message = response.meta?.message
                    if(message != null){
                        FancyToast.makeText(this, message, FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show()
                    }
                }
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
}