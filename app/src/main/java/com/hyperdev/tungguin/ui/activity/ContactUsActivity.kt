package com.hyperdev.tungguin.ui.activity

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import android.view.View
import android.widget.Toast
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.contact.MetaContactUs
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.ContactUsPresenter
import com.hyperdev.tungguin.repository.other.ContactUsRepositoryImp
import com.hyperdev.tungguin.ui.view.ContactUsView
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.utils.Validation.Companion.validateFields
import kotlinx.android.synthetic.main.activity_contact_us.*

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
        baseApiService = NetworkClient.getClient(this@ContactUsActivity)!!
            .create(BaseApiService::class.java)

        val repository = ContactUsRepositoryImp(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = ContactUsPresenter(this@ContactUsActivity, this, repository, scheduler)

        token = SharedPrefManager.getInstance(this).token.toString()
    }

    private fun contactRequest() {

        //Memasukan DataUser User Pada Variable
        getTitleRequest = title_request.text.toString()
        getContentRequest = request_content.text.toString()

        var err = 0

        if (!validateFields(getTitleRequest)) {
            err++
            Toast.makeText(this@ContactUsActivity, "Judul permintaan tida boleh kosong !", Toast.LENGTH_SHORT).show()
        } else if (!validateFields(getContentRequest)) {
            err++
            Toast.makeText(this@ContactUsActivity, "Isi permintaan tidak boleh kosong !", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this@ContactUsActivity, message, Toast.LENGTH_LONG).show()
        }
        title_request.setText("")
        request_content.setText("")
    }

    override fun contactUsMessage(message: MetaContactUs) {
        this.message = message.message.toString()
    }

    override fun noInternetConnection(message: String) {
        Snackbar.make(contact_layout, message, Snackbar.LENGTH_SHORT).show()
    }
}
