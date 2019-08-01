package com.hyperdev.tungguin.ui.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.ForgotPassPresenter
import com.hyperdev.tungguin.ui.view.ForgotPassView
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.utils.Validation.Companion.validateEmail
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_reset_pass.*
import retrofit2.HttpException
import java.net.SocketTimeoutException

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
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        baseApiService = NetworkClient.getClient(this)
            .create(BaseApiService::class.java)

        val scheduler = AppSchedulerProvider()
        presenter = ForgotPassPresenter(this, baseApiService, scheduler)

        btn_send_email.setOnClickListener {
            emailUser = input_email.text.toString()

            var err = 0

            if (!validateEmail(emailUser)) {
                err++
                input_email.error = "Email tidak valid!"
            }

            if (err == 0) {
                presenter.forgotPassword(emailUser, this)
            }
        }
    }

    override fun showProgressBar() {
        shadow.visibility = View.VISIBLE
        progress_bar.visibility = View.VISIBLE
        btn_send_email.isEnabled = false
        input_email.isEnabled = false
    }

    override fun hideProgressBar() {
        shadow.visibility = View.GONE
        progress_bar.visibility = View.GONE
        btn_send_email.isEnabled = true
        input_email.isEnabled = true
    }

    override fun onSuccess() {
        FancyToast.makeText(this, "Silakan cek email anda", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
