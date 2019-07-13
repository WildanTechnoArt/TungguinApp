package com.hyperdev.tungguin.ui.activity

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import android.util.Log
import android.view.View
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.ForgotPassPresenter
import com.hyperdev.tungguin.repository.other.ForgotPassRepositoryImpl
import com.hyperdev.tungguin.ui.view.ForgotPassView
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.utils.Validation.Companion.validateEmail
import kotlinx.android.synthetic.main.activity_reset_pass.*

class ForgotPassActivity : AppCompatActivity(), ForgotPassView.View {

    //Deklarasi Variable
    private lateinit var emailUser: String
    private lateinit var presenter: ForgotPassView.Presenter
    private lateinit var baseApiService: BaseApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_pass)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        baseApiService = NetworkClient.getClient(this@ForgotPassActivity)!!
            .create(BaseApiService::class.java)

        val repository = ForgotPassRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = ForgotPassPresenter(this@ForgotPassActivity, this, repository, scheduler)

        btnResetPass.setOnClickListener {
            emailUser = email.text.toString()

            var err = 0

            if (!validateEmail(emailUser)) {
                err++
                email.error = "Email tidak valid !"
            }

            if (err == 0) {
                presenter.forgotPassword(emailUser)
            }
        }
    }

    override fun showProgressBar() {
        shadow.visibility = View.VISIBLE
        progress_bar.visibility = View.VISIBLE
        btnResetPass.isEnabled = false
        email.isEnabled = false
    }

    override fun hideProgressBar() {
        shadow.visibility = View.GONE
        progress_bar.visibility = View.GONE
        btnResetPass.isEnabled = true
        email.isEnabled = true
    }

    override fun onSuccess() {
        Log.d(ForgotPassActivity::class.java.simpleName, "Forgot Password Success")
    }

    override fun noInternetConnection(message: String) {
        Snackbar.make(forgot_pass_layout, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
