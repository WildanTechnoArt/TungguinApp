package com.hyperdev.tungguin.ui.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.contact.MetaContactUs
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.ContactUsPresenter
import com.hyperdev.tungguin.ui.view.ContactUsView
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.utils.Validation.Companion.validateFields
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_contact_us.*
import retrofit2.HttpException
import java.net.SocketTimeoutException

class ContactUsActivity : AppCompatActivity(), ContactUsView.View {

    // Deklarasi Variable
    private lateinit var getTitleRequest: String
    private lateinit var getContentRequest: String
    private lateinit var baseApiService: BaseApiService
    private lateinit var presenter: ContactUsView.Presenter
    private lateinit var token: String
    private lateinit var message: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initData()

        send_request.setOnClickListener {
            try {
                contactRequest()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun initData(){
        baseApiService = NetworkClient.getClient(this)
            .create(BaseApiService::class.java)

        val scheduler = AppSchedulerProvider()
        presenter = ContactUsPresenter(this, baseApiService, scheduler)

        token = SharedPrefManager.getInstance(this).token.toString()
    }

    private fun contactRequest() {

        //Memasukan DataUser User Pada Variable
        getTitleRequest = input_title_request.text.toString()
        getContentRequest = input_request_content.text.toString()

        var err = 0

        if (!validateFields(getTitleRequest)) {
            err++
            FancyToast.makeText(this, "Judul permintaan tida boleh kosong", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show()
        } else if (!validateFields(getContentRequest)) {
            err++
            FancyToast.makeText(this, "Isi permintaan tidak boleh kosong", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show()
        }

        if (err == 0) {
            presenter.contactUs("Bearer $token", "application/json", getTitleRequest, getContentRequest)
        }
    }

    override fun showProgressBar() {
        shadow.visibility = View.VISIBLE
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        shadow.visibility = View.GONE
        progress_bar.visibility = View.GONE
    }

    override fun onSuccess() {
        shadow.visibility = View.GONE
        progress_bar.visibility = View.GONE
        if (message != "null") {
            FancyToast.makeText(this, message, FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
        }
        input_title_request.setText("")
        input_request_content.setText("")
    }

    override fun contactUsMessage(message: MetaContactUs) {
        this.message = message.message.toString()
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
}
