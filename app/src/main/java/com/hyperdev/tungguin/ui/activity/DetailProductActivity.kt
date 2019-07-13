package com.hyperdev.tungguin.ui.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.hyperdev.tungguin.GlideApp
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.detailproduct.FieldListFormatted
import com.hyperdev.tungguin.model.detailproduct.PriceList
import com.hyperdev.tungguin.model.detailproduct.ProductDetailItem
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.DetailProductPresenter
import com.hyperdev.tungguin.repository.product.ProductRepositoryImpl
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.ui.view.DetailProductView
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

        baseApiService = NetworkClient.getClient(this@DetailProductActivity)!!
            .create(BaseApiService::class.java)

        val request = ProductRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = DetailProductPresenter(this, this@DetailProductActivity, request, scheduler)

        presenter.getDetailProduct("Bearer $token", hashed_id)

        imageListener = ImageListener { position, imageView ->
            GlideApp.with(this@DetailProductActivity)
                .load(imageList[position])
                .transition(withCrossFade())
                .centerCrop()
                .into(imageView)
        }

        img_slider_product.setImageListener(imageListener)

        btn_order.setOnClickListener {
            val intent = Intent(this@DetailProductActivity, OrderDesignActivity::class.java)
            intent.putExtra("sendProductID", hashed_id)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        swipe_refresh.setOnRefreshListener {
            presenter.getDetailProduct("Bearer $token", hashed_id)
        }
    }

    override fun showPriceList(priceList: List<PriceList>) {

    }

    override fun showFieldList(fieldListFormatted: List<FieldListFormatted>) {

    }

    override fun showDetailProductItem(productItem: ProductDetailItem) {
        imageList.add(productItem.iconUrl.toString())
        tv_product_name.text = productItem.name.toString()

        val productDescription = productItem.description.toString()
        if(productDescription != "null"){
            product_description.loadData(productDescription, "text/html", null)
        }else{
            tv_description.visibility = View.GONE
            product_description.visibility = View.GONE
            view.visibility = View.GONE
            view2.visibility = View.GONE
        }

        img_slider_product.pageCount = imageList.size
    }

    override fun displayProgress() {
        swipe_refresh.isRefreshing = true
    }

    override fun hideProgress() {
        swipe_refresh.isRefreshing = false
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
