package com.hyperdev.tungguin.view.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.hyperdev.tungguin.GlideApp
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.detailproduct.FieldListFormatted
import com.hyperdev.tungguin.model.detailproduct.PriceList
import com.hyperdev.tungguin.model.detailproduct.ProductDetailItem
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkUtil
import com.hyperdev.tungguin.presenter.DetailProductPresenter
import com.hyperdev.tungguin.repository.DetailProductRepositoryImpl
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.view.DetailProductView
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.activity_detail_product.*

class DetailProductActivity : AppCompatActivity(), DetailProductView.View {

    // Deklarasi Variable
    private lateinit var baseApiService: BaseApiService
    private lateinit var presenter: DetailProductView.Presenter
    private lateinit var imageListener: ImageListener
    private var imageList: MutableList<String> = mutableListOf()
    private lateinit var token: String
    private lateinit var hashed_id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_product)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        token = SharedPrefManager.getInstance(this@DetailProductActivity).token.toString()
        hashed_id = intent.getStringExtra("sendProductID").toString()

        baseApiService = NetworkUtil.getClient(this@DetailProductActivity)!!
            .create(BaseApiService::class.java)

        val request = DetailProductRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = DetailProductPresenter(this, this@DetailProductActivity, request, scheduler)

        presenter.getDetailProduct("Bearer $token", hashed_id)

        imageListener = ImageListener { position, imageView ->
           GlideApp.with(this@DetailProductActivity)
               .load(imageList[position])
               .into(imageView)
        }

        imageSlider.setImageListener(imageListener)

        btn_order.setOnClickListener {
            val intent = Intent(this@DetailProductActivity, OrderDesignActivity::class.java)
            intent.putExtra("sendProductID", hashed_id)
            startActivity(intent)
        }

        refreshLayput.setOnRefreshListener {
            presenter.getDetailProduct("Bearer $token", hashed_id)
        }
    }

    override fun showPriceList(priceList: List<PriceList>) {

    }

    override fun showFieldList(fieldListFormatted: List<FieldListFormatted>) {

    }

    override fun showDetailProductItem(productItem: ProductDetailItem) {
        imageList.add(productItem.iconUrl.toString())
        productName.text = productItem.name.toString()

        product_description.loadData(productItem.description.toString(), "text/html", null)

        imageSlider.pageCount = imageList.size
    }

    override fun displayProgress() {
        scrollView.visibility = View.GONE
        refreshLayput.isRefreshing = true
    }

    override fun hideProgress() {
        scrollView.visibility = View.VISIBLE
        refreshLayput.isRefreshing = false
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}
