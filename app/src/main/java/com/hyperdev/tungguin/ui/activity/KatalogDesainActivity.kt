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
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.KatalogDesainPresenter
import com.hyperdev.tungguin.repository.product.ProductRepositoryImpl
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.ui.view.KatalogDesainView
import kotlinx.android.synthetic.main.activity_katalog_desain.*
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
        setContentView(R.layout.activity_katalog_desain)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loadData()

        swipe_refresh.setOnRefreshListener {
            loadData()
        }
    }

    private fun loadData() {
        token = SharedPrefManager.getInstance(this@KatalogDesainActivity).token.toString()

        baseApiService = NetworkClient.getClient(this@KatalogDesainActivity)!!
            .create(BaseApiService::class.java)

        val layout = LinearLayoutManager(this@KatalogDesainActivity)
        rv_katalog.layoutManager = layout
        rv_katalog.setHasFixedSize(true)

        val request = ProductRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = KatalogDesainPresenter(this, this@KatalogDesainActivity, request, scheduler)

        adapter = KatalogDesainAdapter(listKatalogItem as ArrayList<KatalogItem>, this@KatalogDesainActivity)

        presenter.getKatalogDesain("Bearer $token")

        rv_katalog.adapter = adapter
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
        rv_katalog.visibility = View.GONE
    }

    override fun hideProgress() {
        swipe_refresh.isRefreshing = false
        rv_katalog.visibility = View.VISIBLE
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
