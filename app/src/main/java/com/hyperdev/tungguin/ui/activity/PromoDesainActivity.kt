package com.hyperdev.tungguin.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.adapter.BannerItemAdapter
import com.hyperdev.tungguin.adapter.PromoDesainAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.promodesain.BannerData
import com.hyperdev.tungguin.model.promodesain.PromoData
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.PromoPresenter
import com.hyperdev.tungguin.ui.view.PromoDesainView
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_promo_desain.*
import retrofit2.HttpException
import java.net.SocketTimeoutException
import kotlin.properties.Delegates

class PromoDesainActivity : AppCompatActivity(), PromoDesainView.View {

    //Deklarasi Variable
    private var listKatalogItem: MutableList<PromoData> = mutableListOf()
    private var listBannerItem: MutableList<BannerData> = mutableListOf()
    private lateinit var presenter: PromoDesainView.Presenter
    private lateinit var token: String
    private lateinit var baseApiService: BaseApiService
    private var adapter by Delegates.notNull<PromoDesainAdapter>()
    private var bannerAdapter by Delegates.notNull<BannerItemAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promo_desain)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        loadData()

        swipe_refresh.setOnRefreshListener {
            loadData()
        }
    }

    private fun loadData() {
        token = SharedPrefManager.getInstance(this).token.toString()

        baseApiService = NetworkClient.getClient(this)
            .create(BaseApiService::class.java)

        rv_catalogue_movie.layoutManager = LinearLayoutManager(this)
        rv_catalogue_movie.setHasFixedSize(true)

        rv_banner_item.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false)
        rv_banner_item.setHasFixedSize(true)

        val scheduler = AppSchedulerProvider()
        presenter = PromoPresenter(this, baseApiService, scheduler)

        adapter = PromoDesainAdapter(listKatalogItem as ArrayList<PromoData>, this)
        bannerAdapter = BannerItemAdapter(listBannerItem as ArrayList<BannerData>, this)

        presenter.getKatalogDesain("Bearer $token")
        presenter.getBannerDesain("Bearer $token")

        rv_catalogue_movie.adapter = adapter
        rv_banner_item.adapter = bannerAdapter
    }

    override fun showBennerItemL(bannerItem: List<BannerData>) {
        listBannerItem.clear()
        bannerItem.forEach {
            listBannerItem.add(it)
        }

        bannerAdapter.notifyDataSetChanged()

        if(bannerAdapter.itemCount > 0){
            rv_banner_item.visibility = View.VISIBLE
        }
    }

    override fun showKatalogItemList(katalogItem: List<PromoData>) {
        listKatalogItem.clear()
        katalogItem.forEach {
            listKatalogItem.add(it)
        }
        adapter.notifyDataSetChanged()
    }

    override fun displayProgress() {
        swipe_refresh.isRefreshing = true
        rv_catalogue_movie.visibility = View.GONE
    }

    override fun hideProgress() {
        swipe_refresh.isRefreshing = false
        rv_catalogue_movie.visibility = View.VISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}
