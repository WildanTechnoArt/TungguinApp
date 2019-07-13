package com.hyperdev.tungguin.ui.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import android.view.View
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.LoginPresenter
import com.hyperdev.tungguin.repository.authentication.AuthRepositoryImp
import com.hyperdev.tungguin.ui.view.LoginView
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.utils.Validation.Companion.validateEmail
import com.hyperdev.tungguin.utils.Validation.Companion.validateFields
import kotlinx.android.synthetic.main.activity_login_page.*

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

        baseApiService = NetworkClient.getClient(this@LoginActivity)!!
            .create(BaseApiService::class.java)

        val loginRepository = AuthRepositoryImp(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = LoginPresenter(this@LoginActivity, this, loginRepository, scheduler)

        btnLogin.setOnClickListener {
            login()
        }

        forgot_pass.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPassActivity::class.java))
        }
    }

    private fun login() {

        emailUser = email.text.toString()
        passUser = pass.text.toString()

        var err = 0

        if (!validateEmail(emailUser)) {
            err++
            email.error = "Email tidak valid !"
        }

        if (!validateFields(passUser)) {
            err++
            pass.error = "Password tidak boleh kosong !"
        }

        if (err == 0) {
            presenter.loginUser(emailUser, passUser)
        }
    }

    override fun showProgressBar() {
        shadow.visibility = View.VISIBLE
        progress_bar.visibility = View.VISIBLE
        btnLogin.isEnabled = false
        email.isEnabled = false
        pass.isEnabled = false
    }

    override fun hideProgressBar() {
        shadow.visibility = View.GONE
        progress_bar.visibility = View.GONE
        btnLogin.isEnabled = true
        email.isEnabled = true
        pass.isEnabled = true
    }

    override fun onSuccess() {
        startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
        finishAffinity()
    }

    override fun noInternetConnection(message: String) {
        Snackbar.make(login_page, message, Snackbar.LENGTH_SHORT).show()
    }
}