package com.hyperdev.tungguin.view.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Paint
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.hyperdev.tungguin.GlideApp
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.adapter.SearchProductAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.announcement.AnnouncementData
import com.hyperdev.tungguin.model.fcm.FCMResponse
import com.hyperdev.tungguin.model.login.LoginResponse
import com.hyperdev.tungguin.model.profile.DataUser
import com.hyperdev.tungguin.model.searchproduct.DesignItem
import com.hyperdev.tungguin.model.searchproduct.SearchProductData
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkUtil
import com.hyperdev.tungguin.presenter.DashboardPresenter
import com.hyperdev.tungguin.presenter.SearchProductPresenter
import com.hyperdev.tungguin.repository.ProfileRepositoryImpl
import com.hyperdev.tungguin.repository.SearchProductRepositoryImpl
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.view.DashboardView
import com.hyperdev.tungguin.view.SearchProductView
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.activity_dashboard.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class Dashboard : AppCompatActivity(), DashboardView.View, SearchProductView.View {

    //Deklarasi Variable
    private lateinit var toast: Toast
    private var lastBackPressTime: Long = 0
    private lateinit var presenter: DashboardView.Presenter
    private lateinit var searchPresenter: SearchProductView.Presenter
    private lateinit var baseApiService: BaseApiService
    private lateinit var getToken: String
    private lateinit var closeSearchMenu: ImageButton
    private lateinit var progressBar: ProgressBar
    private var condition: Boolean = false
    private lateinit var recycler: RecyclerView
    private lateinit var searchByName: EditText
    private var events: MutableList<DesignItem> = mutableListOf()
    private lateinit var imageList: MutableList<String>
    private lateinit var adapter : SearchProductAdapter
    private lateinit var imageCross: ImageView
    private lateinit var textSrc1: TextView
    private lateinit var textSrc2: TextView
    private lateinit var textContactUs: TextView
    private var page by Delegates.notNull<Int>()
    private lateinit var imageListener: ImageListener
    private var isLoading by Delegates.notNull<Boolean>()
    private lateinit var nextPageURL: String
    private val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Mengaktifkan Kembali FCM
        FirebaseMessaging.getInstance().isAutoInitEnabled = true

        page = 1

        imageList = arrayListOf()

        closeSearchMenu = findViewById(R.id.closeSearch)
        progressBar = findViewById(R.id.progress_search)
        recycler = findViewById(R.id.search_layout_item)
        searchByName = findViewById(R.id.search_design_by_name)
        imageCross = findViewById(R.id.imgCross)
        textSrc1 = findViewById(R.id.txt1)
        textSrc2 = findViewById(R.id.txt2)
        textContactUs = findViewById(R.id.contactUs)

        getToken = SharedPrefManager.getInstance(this@Dashboard).token.toString()

        swipeRefresh.isEnabled = false

        cart.setOnClickListener {
            startActivity(Intent(this@Dashboard, CartActivity::class.java))
        }

        order_histori.setOnClickListener {
            startActivity(Intent(this@Dashboard, HistoriOrderActivity::class.java))
        }

        imageListener = ImageListener { position, imageView ->
            GlideApp.with(this@Dashboard)
                .load(imageList[position])
                .into(imageView)

        }

        carouselView.setImageListener(imageListener)

    }

    private fun postTokenFCM(){
        val tokenFCM = SharedPrefManager.getInstance(this@Dashboard).tokenFCM.toString()
        baseApiService.fcmRequest("Bearer $getToken", "application/json", tokenFCM)
            .enqueue(object : Callback<FCMResponse> {

                override fun onFailure(call: Call<FCMResponse>, t: Throwable) {
                    Log.e("RegisterPage.kt", "onFailure ERROR -> "+ t.message)
                }

                override fun onResponse(call: Call<FCMResponse>, response: Response<FCMResponse>) {

                    if (response.isSuccessful) {
                        Log.i("debug", "onResponse: BERHASIL")
                        try {
                            val message = response.body()?.meta?.message
                            if(message != null){
                                Toast.makeText(this@Dashboard, message.toString(), Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.i("debug", "onResponse: GA BERHASIL")
                        val gson = Gson()
                        val message = gson.fromJson(response.errorBody()?.charStream(), LoginResponse::class.java)
                        if(message != null){
                            Toast.makeText(this@Dashboard, message.getMeta?.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
    }

    private fun startInit(){

        val request = SearchProductRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()

        searchPresenter = SearchProductPresenter(this, this@Dashboard, request, scheduler)

        adapter = SearchProductAdapter(events as ArrayList<DesignItem>, this@Dashboard)

        initListener()

        btn_profile.setOnClickListener {
            startActivity(Intent(this@Dashboard, ProfileActivity::class.java))
        }

        topup.setOnClickListener {
            startActivity(Intent(this@Dashboard, TopUpActivity::class.java))
        }

        topup_histori.setOnClickListener {
            startActivity(Intent(this@Dashboard, HistoryActivity::class.java))
        }

        btn_order.setOnClickListener {
            startActivity(Intent(this@Dashboard, OrderWithSliderActivity::class.java))
        }

        katalog_desain.setOnClickListener {
            startActivity(Intent(this@Dashboard, KatalogDesainActivity::class.java))
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

        search_design.setOnClickListener {
            showSearchMenu()
        }

        closeSearchMenu.setOnClickListener {
            hideSearchMenu()
        }

        searchByName.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                searchPresenter.getProductByName("Bearer $getToken", name = searchByName.text.toString())
                return@OnEditorActionListener true
            }
            false
        })

        textContactUs.setOnClickListener {
            startActivity(Intent(this@Dashboard, ContactUsActivity::class.java))
        }
    }

    private fun initListener() {
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val countItem = linearLayoutManager.itemCount
                val lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                val isLastPosition = countItem.minus(1) == lastVisiblePosition
                Log.d(TAG, "countItem: $countItem")
                Log.d(TAG, "lastVisiblePosition: $lastVisiblePosition")
                Log.d(TAG, "isLastPosition: $isLastPosition")
                if (!isLoading && isLastPosition && nextPageURL != "null") {
                    page = page.plus(1)
                    searchPresenter.getProductByPage("Bearer $getToken", page = page)
                }
            }
        })
    }

    override fun showProduct(product: SearchProductData) {
        nextPageURL = product.nextPageUrl.toString()
    }

    private fun showSearchMenu(){
        cardSearch.visibility = View.GONE
        cart.visibility = View.GONE
        cartCount.visibility = View.GONE
        swipeRefresh.visibility = View.GONE
        search_layout.visibility = View.VISIBLE
        searchPresenter.getProductByName("Bearer $getToken", name = "")
        val layout = LinearLayoutManager(this@Dashboard)
        recycler.layoutManager = layout
        recycler.setHasFixedSize(true)
        recycler.adapter = adapter
        condition = true
    }

    private fun hideSearchMenu(){
        searchByName.setText("")
        cardSearch.visibility = View.VISIBLE
        cart.visibility = View.VISIBLE
        cartCount.visibility = View.VISIBLE
        swipeRefresh.visibility = View.VISIBLE
        search_layout.visibility = View.GONE
        condition = false
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
            baseApiService = NetworkUtil.getClient(this@Dashboard)!!
                .create(BaseApiService::class.java)

            getToken = SharedPrefManager.getInstance(this@Dashboard).token.toString()
            val request = ProfileRepositoryImpl(baseApiService)
            val scheduler = AppSchedulerProvider()
            presenter = DashboardPresenter(this, this@Dashboard, request, scheduler)
            presenter.getUserProfile("Bearer $getToken")
            presenter.getSliderImage("Bearer $getToken")
            presenter.getAnnouncementData("Bearer $getToken")

            startInit()
            postTokenFCM()
        } else {
            swipeRefresh.isRefreshing = false
            Snackbar.make(main_layout, "Tidak terhubung dengan internet !", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {

        if(condition){
            hideSearchMenu()
        }else{
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
    }

    override fun loaddAnnouncement(text: AnnouncementData) {
        if(text.announcement.toString() != "null"){
            card_announcement.visibility = View.VISIBLE
            announcement.loadData(text.announcement.toString(), "text/html", null)
        }else{
            card_announcement.visibility = View.GONE
        }
    }

    override fun shodSliderImage(image: List<String>) {
        imageList.clear()
        image.forEach {
            imageList.add(it)
        }
        carouselView.pageCount = imageList.size
    }

    @SuppressLint("SetTextI18n")
    override fun displayProfile(profileItem: DataUser) {
        val date = Calendar.getInstance()
        val hours: Int = date.get(Calendar.HOUR_OF_DAY)
        my_name.text = "${profileItem.name}!"
        userBalance.text = profileItem.formattedBalance.toString()

        if(profileItem.cartCount.toString() != "0"){
            cartCount.visibility = View.VISIBLE
        }else{
            cartCount.visibility = View.GONE
        }

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
        isLoading = true
    }

    override fun hideProgress() {
        swipeRefresh.isRefreshing = false
        isLoading = false
    }

    override fun showProductByPage(product: List<DesignItem>) {
        if(page == 1){
            events.clear()
            events.addAll(product)
            adapter.notifyDataSetChanged()
        }else{
            adapter.refreshAdapter(product)
        }
    }

    override fun showProductByName(product: List<DesignItem>) {
        events.clear()
        events.addAll(product)
        if(events.isEmpty()){
            recycler.visibility = View.GONE
            imageCross.visibility = View.VISIBLE
            textSrc1.visibility = View.VISIBLE
            textSrc2.visibility = View.VISIBLE
            textContactUs.visibility = View.VISIBLE
            textContactUs.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        }else{
            recycler.visibility = View.VISIBLE
            imageCross.visibility = View.GONE
            textSrc1.visibility = View.GONE
            textSrc2.visibility = View.GONE
            textContactUs.visibility = View.GONE
        }
        adapter.notifyDataSetChanged()
    }

    override fun displayProgressItem() {
        progressBar.visibility = View.VISIBLE
        isLoading = true
    }

    override fun hideProgressItem() {
        progressBar.visibility = View.GONE
        isLoading = false
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}