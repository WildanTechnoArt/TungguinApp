package com.hyperdev.tungguin.view.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.profile.DataUser
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkUtil
import com.hyperdev.tungguin.presenter.DashboardPresenter
import com.hyperdev.tungguin.repository.ProfileRepositoryImpl
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.view.DashboardView
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.util.*

class Dashboard : AppCompatActivity(), DashboardView.View {

    //Deklarasi Variable
    private lateinit var toast: Toast
    private var lastBackPressTime: Long = 0
    private lateinit var presenter: DashboardView.Presenter
    private lateinit var baseApiService: BaseApiService
    private lateinit var getToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        btn_profile.setOnClickListener {
            startActivity(Intent(this@Dashboard, ProfileActivity::class.java))
        }

        topup.setOnClickListener {
            startActivity(Intent(this@Dashboard, TopUpActivity::class.java))
        }

        topup_histori.setOnClickListener {
            startActivity(Intent(this@Dashboard, HistoryActivity::class.java))
        }

        btnLogout.setOnClickListener {
            val keluar = AlertDialog.Builder(this)
            keluar.setTitle("Konfirmasi")
            keluar.setMessage("Anda yakin ingin keluar?")
            keluar.setNegativeButton("Tidak") { d, _ ->
                d.dismiss()
            }
            keluar.setPositiveButton("Ya") { _, _ ->
                SharedPrefManager.getInstance(this@Dashboard).deleteToken()
                startActivity(Intent(this@Dashboard, MainPage::class.java))
                finishAffinity()
            }
            keluar.setCancelable(false)
            keluar.create()
            keluar.show()
        }

        swipeRefresh.setOnRefreshListener {
            loadData()
        }

    }

    override fun onResume() {
        super.onResume()
        swipeRefresh.isRefreshing = true
        loadData()
    }

    private fun loadData(){
        val connectivity  = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivity.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
            baseApiService = NetworkUtil.getClient()!!
                .create(BaseApiService::class.java)

            getToken = SharedPrefManager.getInstance(this@Dashboard).token.toString()
            val request = ProfileRepositoryImpl(baseApiService)
            val scheduler = AppSchedulerProvider()
            presenter = DashboardPresenter(this, request, scheduler)
            presenter.getUserProfile(this@Dashboard, "Bearer $getToken")
        } else {
            swipeRefresh.isRefreshing = false
            Snackbar.make(main_layout, "Tidak terhubung dengan internet !", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
        @Suppress("SENSELESS_COMPARISON")
        if(this.lastBackPressTime < System.currentTimeMillis() - 3000){
            toast = Toast.makeText(this@Dashboard,"Tekan tombol kembali lagi untuk keluar", 3000)
            toast.show()
            this.lastBackPressTime = System.currentTimeMillis()
        }else {
            if(toast != null){
                toast.cancel()
            }
            super.onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun displayProfile(profileItem: DataUser) {
        val date = Calendar.getInstance()
        val hours: Int = date.get(Calendar.HOUR_OF_DAY)
        my_name.text = "${profileItem.name}!"
        userBalance.text = profileItem.formattedBalance.toString()
        cartCount.text = profileItem.cartCount.toString()

        when (hours) {
            in 1..9 -> welcome.text = "Selamat pagi, "
            in 10..12 -> welcome.text = "Selamat Siang, "
            in 13..18 -> welcome.text = "Selamat Sore, "
            in 19..24 -> welcome.text = "Selamat Malam, "
        }
    }

    override fun displayProgress() {
        swipeRefresh.isRefreshing = true
    }

    override fun hideProgress() {
        swipeRefresh.isRefreshing = false
    }
}