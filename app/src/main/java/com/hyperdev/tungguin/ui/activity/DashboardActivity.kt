package com.hyperdev.tungguin.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.View
import android.widget.*
import com.google.firebase.messaging.FirebaseMessaging
import com.hyperdev.tungguin.GlideApp
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.dashboard.AnnouncementData
import com.hyperdev.tungguin.model.profile.DataUser
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.DashboardPresenter
import com.hyperdev.tungguin.repository.dashboard.DashboardRepositoryImp
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.ui.view.DashboardView
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.dashboard_main_menu.*
import java.util.*

class DashboardActivity : AppCompatActivity(), DashboardView.View {

    private lateinit var toast: Toast
    private var lastBackPressTime: Long = 0
    private lateinit var presenter: DashboardView.Presenter
    private lateinit var baseApiService: BaseApiService
    private lateinit var getToken: String
    private lateinit var imageList: MutableList<String>
    private lateinit var imageListener: ImageListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initData()

        mainMenuListener()

        carousel_view.setImageListener(imageListener)

        postTokenFCM()

        swipe_refresh.setOnRefreshListener {
            loadData()
        }

    }

    private fun postTokenFCM() {
        val tokenFCM = SharedPrefManager.getInstance(this@DashboardActivity).tokenFCM.toString()
        presenter.sendTokenFcm("Bearer $getToken", "application/json", tokenFCM)
    }

    private fun mainMenuListener(){
        img_cart.setOnClickListener {
            startActivity(Intent(this@DashboardActivity, CartActivity::class.java))
        }

        img_message.setOnClickListener {
            startActivity(Intent(this@DashboardActivity, ChatListActivity::class.java))
        }

        tv_search_design.setOnClickListener {
            startActivity(Intent(this, SearchProductActivity::class.java))
        }

        btn_profile.setOnClickListener {
            startActivity(Intent(this@DashboardActivity, ProfileActivity::class.java))
        }

        btn_topup.setOnClickListener {
            startActivity(Intent(this@DashboardActivity, TopUpActivity::class.java))
        }

        btn_topup_histori.setOnClickListener {
            startActivity(Intent(this@DashboardActivity, HistoryActivity::class.java))
        }

        btn_order.setOnClickListener {
            startActivity(Intent(this@DashboardActivity, OrderActivity::class.java))
        }

        order_histori.setOnClickListener {
            startActivity(Intent(this@DashboardActivity, HistoriOrderActivity::class.java))
        }

        katalog_desain.setOnClickListener {
            startActivity(Intent(this@DashboardActivity, KatalogDesainActivity::class.java))
        }

        btnHelp.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse("https://tungguin.com/help/")
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            val keluar = AlertDialog.Builder(this)
            keluar.setTitle("Konfirmasi")
            keluar.setMessage("Anda yakin ingin keluar?")
            keluar.setNegativeButton("Tidak") { d, _ ->
                d.dismiss()
            }
            keluar.setPositiveButton("Ya") { _, _ ->
                resetTokenFCM()
            }
            keluar.setCancelable(false)
            keluar.create()
            keluar.show()
        }
    }

    private fun initData() {
        FirebaseMessaging.getInstance().isAutoInitEnabled = true

        imageList = arrayListOf()

        imageListener = ImageListener { position, imageView ->
            GlideApp.with(this@DashboardActivity)
                .load(imageList[position])
                .into(imageView)

        }

        baseApiService = NetworkClient.getClient(this@DashboardActivity)!!
            .create(BaseApiService::class.java)

        getToken = SharedPrefManager.getInstance(this@DashboardActivity).token.toString()
        val repository = DashboardRepositoryImp(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = DashboardPresenter(this, this@DashboardActivity, repository, scheduler)
    }

    private fun resetTokenFCM() {
        swipe_refresh.isRefreshing = true
        presenter.resetTokenFcm("Bearer $getToken", "application/json", null)
    }

    override fun onSuccessResetToken() {
        SharedPrefManager.getInstance(this@DashboardActivity).deleteToken()
        startActivity(Intent(this@DashboardActivity, MainActivity::class.java))
        finishAffinity()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        presenter.getUserProfile("Bearer $getToken")
        presenter.getSliderImage("Bearer $getToken")
        presenter.getAnnouncementData("Bearer $getToken")
    }

    override fun onBackPressed() {
        if (this.lastBackPressTime < System.currentTimeMillis() - 3000) {
            toast = Toast.makeText(this@DashboardActivity, "Tekan tombol kembali lagi untuk keluar", Toast.LENGTH_SHORT)
            toast.show()
            this.lastBackPressTime = System.currentTimeMillis()
        } else {
            toast.cancel()
            super.onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun showProfile(profileItem: DataUser) {
        val date = Calendar.getInstance()
        val hours: Int = date.get(Calendar.HOUR_OF_DAY)
        tv_user_balance.text = profileItem.formattedBalance.toString()

        if (profileItem.cartCount != 0) {
            tv_cart_count.visibility = View.VISIBLE
        } else {
            tv_cart_count.visibility = View.GONE
        }
        tv_cart_count.text = profileItem.cartCount.toString()

        if (profileItem.activeOrderCount != 0) {
            tv_chat_active_count.visibility = View.VISIBLE
        } else {
            tv_chat_active_count.visibility = View.GONE
        }
        tv_chat_active_count.text = profileItem.activeOrderCount.toString()

        when (hours) {
            in 1..9 -> tv_my_name.text = "Selamat pagi, ${profileItem.name}!"
            in 10..12 -> tv_my_name.text = "Selamat Siang, ${profileItem.name}!"
            in 13..18 -> tv_my_name.text = "Selamat Sore, ${profileItem.name}!"
            in 19..24 -> tv_my_name.text = "Selamat Malam, ${profileItem.name}!"
        }
    }

    override fun showSliderImage(image: List<String>) {
        imageList.clear()
        image.forEach {
            imageList.add(it)
        }
        carousel_view.pageCount = imageList.size
    }

    override fun showAnnouncement(text: AnnouncementData) {
        if (text.announcement.toString() != "null") {
            card_announcement.visibility = View.VISIBLE
            announcement.loadData(text.announcement.toString(), "text/html", null)
        } else {
            card_announcement.visibility = View.GONE
        }
    }

    override fun showProgressBar() {
        swipe_refresh.isRefreshing = true
    }

    override fun hideProgressBar() {
        swipe_refresh.isRefreshing = false
    }
}