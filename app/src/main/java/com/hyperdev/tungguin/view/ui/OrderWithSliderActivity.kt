package com.hyperdev.tungguin.view.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Paint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.hyperdev.tungguin.GlideApp
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.adapter.SearchProductAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.orderlandingpage.OrderData
import com.hyperdev.tungguin.model.profile.DataUser
import com.hyperdev.tungguin.model.searchproduct.DesignItem
import com.hyperdev.tungguin.model.searchproduct.SearchProductData
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkUtil
import com.hyperdev.tungguin.presenter.OrderWithSliderPresenter
import com.hyperdev.tungguin.presenter.SearchProductPresenter
import com.hyperdev.tungguin.repository.OrderWithSliderRepositoryImpl
import com.hyperdev.tungguin.repository.ProfileRepositoryImpl
import com.hyperdev.tungguin.repository.SearchProductRepositoryImpl
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.view.OrderWithSliderView
import com.hyperdev.tungguin.view.SearchProductView
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.activity_order_with_slider.*
import kotlin.properties.Delegates

class OrderWithSliderActivity : AppCompatActivity(), OrderWithSliderView.View, SearchProductView.View {

    //Deklarasi Variable
    private lateinit var presenter: OrderWithSliderView.Presenter
    private lateinit var token: String
    private lateinit var baseApiService: BaseApiService
    private lateinit var imageListener: ImageListener
    private lateinit var imageList: MutableList<String>

    private lateinit var searchPresenter: SearchProductView.Presenter
    private lateinit var closeSearchMenu: ImageButton
    private var condition: Boolean = false
    private lateinit var recycler: RecyclerView
    private lateinit var searchByName: EditText
    private var events: MutableList<DesignItem> = mutableListOf()
    private lateinit var searchProgress: ProgressBar
    private lateinit var adapter : SearchProductAdapter
    private lateinit var imageCross: ImageView
    private lateinit var textSrc1: TextView
    private lateinit var textSrc2: TextView
    private lateinit var textContactUs: TextView
    private var page by Delegates.notNull<Int>()
    private var isLoading by Delegates.notNull<Boolean>()
    private lateinit var nextPageURL: String
    private val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_with_slider)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        page = 1

        closeSearchMenu = findViewById(R.id.closeSearch)
        searchProgress = findViewById(R.id.progress_search)
        recycler = findViewById(R.id.search_layout_item)
        searchByName = findViewById(R.id.search_design_by_name)
        imageCross = findViewById(R.id.imgCross)
        textSrc1 = findViewById(R.id.txt1)
        textSrc2 = findViewById(R.id.txt2)
        textContactUs = findViewById(R.id.contactUs)

        token = SharedPrefManager.getInstance(this@OrderWithSliderActivity).token.toString()

        baseApiService = NetworkUtil.getClient(this@OrderWithSliderActivity)!!
            .create(BaseApiService::class.java)

        val request = OrderWithSliderRepositoryImpl(baseApiService)
        val request2 = ProfileRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = OrderWithSliderPresenter(this, this@OrderWithSliderActivity, request, request2, scheduler)

        presenter.getOrderWithSlider("Bearer $token")
        presenter.getUserProfile("Bearer $token")

        imageList = arrayListOf()

        imageListener = ImageListener { position, imageView ->
            GlideApp.with(this@OrderWithSliderActivity)
                .load(imageList[position])
                .into(imageView)
        }

        carouselView.setImageListener(imageListener)

        startInit()
    }

    private fun startInit(){

        val request = SearchProductRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()

        searchPresenter = SearchProductPresenter(this@OrderWithSliderActivity, this, request, scheduler)

        adapter = SearchProductAdapter(events as ArrayList<DesignItem>, this@OrderWithSliderActivity)

        initListener()

        search_design.setOnClickListener {
            showSearchMenu()
        }

        closeSearchMenu.setOnClickListener {
            hideSearchMenu()
        }

        searchByName.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                searchPresenter.getProductByName("Bearer $token", name = searchByName.text.toString())
                return@OnEditorActionListener true
            }
            false
        })

        textContactUs.setOnClickListener {
            startActivity(Intent(this@OrderWithSliderActivity, ContactUsActivity::class.java))
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
                    searchPresenter.getProductByPage("Bearer $token", page = page)
                }
            }
        })
    }

    private fun showSearchMenu(){
        cardSearch.visibility = View.GONE
        carouselView.visibility = View.GONE
        txt11.visibility = View.GONE
        intoMyName.visibility = View.GONE
        cardDesigner.visibility = View.GONE
        search_layout.visibility = View.VISIBLE
        searchPresenter.getProductByName("Bearer $token", name = "")
        val layout = LinearLayoutManager(this@OrderWithSliderActivity)
        recycler.layoutManager = layout
        recycler.setHasFixedSize(true)
        recycler.adapter = adapter
        condition = true
    }

    private fun hideSearchMenu(){
        searchByName.setText("")
        cardSearch.visibility = View.VISIBLE
        carouselView.visibility = View.VISIBLE
        txt11.visibility = View.VISIBLE
        intoMyName.visibility = View.VISIBLE
        cardDesigner.visibility = View.VISIBLE
        search_layout.visibility = View.GONE
        condition = false
    }

    @SuppressLint("SetTextI18n")
    override fun displayProfile(profileItem: DataUser) {
        intoMyName.text = "Hai ${profileItem.name.toString()}"
    }

    override fun displayOrderWithSlider(orderItem: List<String>) {
        orderItem.forEach {
            imageList.add(it)
        }
        carouselView.pageCount = imageList.size
    }

    override fun onPause() {
        super.onPause()
        carouselView.pauseCarousel()
    }

    override fun onResume() {
        super.onResume()
        carouselView.playCarousel()
    }

    override fun showProduct(product: SearchProductData) {
        nextPageURL = product.nextPageUrl.toString()
    }

    override fun displayOnlineDesigner(designerItem: OrderData) {
        designer_count.text = designerItem.availableDesigner.toString()
    }

    override fun displayProgress() {
        isLoading = true
        progressBar.visibility = View.VISIBLE
        carouselView.visibility = View.GONE
        cardSearch.visibility = View.GONE
        txt11.visibility = View.GONE
        intoMyName.visibility = View.GONE
        cardDesigner.visibility = View.GONE
        center.visibility = View.GONE
        cardSearch.visibility = View.GONE
    }

    override fun hideProgress() {
        isLoading = false
        progressBar.visibility = View.GONE
        carouselView.visibility = View.VISIBLE
        cardSearch.visibility = View.VISIBLE
        txt11.visibility = View.VISIBLE
        intoMyName.visibility = View.VISIBLE
        cardDesigner.visibility = View.VISIBLE
        center.visibility = View.VISIBLE
        cardSearch.visibility = View.VISIBLE
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
        product.forEach {
            events.add(it)
        }
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
        isLoading = true
        searchProgress.visibility = View.VISIBLE
    }

    override fun hideProgressItem() {
        isLoading = false
        searchProgress.visibility = View.GONE
    }

    override fun onBackPressed() {

        if(condition){
            hideSearchMenu()
        }else{
            finish()
        }
    }
}
