package com.hyperdev.tungguin.ui.activity

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.adapter.KatalogDesainAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.katalogdesain.KatalogItem
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.CataloguePresenter
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.ui.view.KatalogDesainView
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_catalogue_desain.*
import retrofit2.HttpException
import java.net.SocketTimeoutException
import kotlin.properties.Delegates

class KatalogDesainActivity : AppCompatActivity(), KatalogDesainView.View {

    //Deklarasi Variable
    private var listKatalogItem: MutableList<KatalogItem> = mutableListOf()
    private lateinit var presenter: KatalogDesainView.Presenter
    private lateinit var token: String
    private lateinit var baseApiService: BaseApiService
    private var adapter by Delegates.notNull<KatalogDesainAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogue_desain)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

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

        val layout = LinearLayoutManager(this)
        rv_catalogue.layoutManager = layout
        rv_catalogue.setHasFixedSize(true)

        val scheduler = AppSchedulerProvider()
        presenter = CataloguePresenter(this, baseApiService, scheduler)

        adapter = KatalogDesainAdapter(listKatalogItem as ArrayList<KatalogItem>, this)

        presenter.getKatalogDesain("Bearer $token")

        rv_catalogue.adapter = adapter
    }

    override fun showKatalogItemList(katalogItem: List<KatalogItem>) {
        listKatalogItem.clear()
        katalogItem.forEach {
            listKatalogItem.add(it)
        }
        adapter.notifyDataSetChanged()
    }

    override fun displayProgress() {
        swipe_refresh.isRefreshing = true
        rv_catalogue.visibility = View.GONE
    }

    override fun hideProgress() {
        swipe_refresh.isRefreshing = false
        rv_catalogue.visibility = View.VISIBLE
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
