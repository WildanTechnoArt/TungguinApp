package com.hyperdev.tungguin.view.ui

import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.adapter.KatalogDesainAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.katalogdesain.KatalogItem
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkUtil
import com.hyperdev.tungguin.presenter.KatalogDesainPresenter
import com.hyperdev.tungguin.repository.KatalogDesainRepositoryImpl
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.view.KatalogDesainView
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

        refreshLayput.setOnRefreshListener {
            loadData()
        }
    }

    private fun loadData(){
        token = SharedPrefManager.getInstance(this@KatalogDesainActivity).token.toString()

        baseApiService = NetworkUtil.getClient(this@KatalogDesainActivity)!!
            .create(BaseApiService::class.java)

        val layout = LinearLayoutManager(this@KatalogDesainActivity)
        katalogVew.layoutManager = layout
        katalogVew.setHasFixedSize(true)

        val request = KatalogDesainRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = KatalogDesainPresenter(this, this@KatalogDesainActivity, request, scheduler)

        adapter = KatalogDesainAdapter(listKatalogItem as ArrayList<KatalogItem>, this@KatalogDesainActivity)

        presenter.getKatalogDesain("Bearer $token")

        katalogVew.adapter = adapter
    }

    override fun showKatalogItemList(katalogItem: List<KatalogItem>) {
        listKatalogItem.clear()
        listKatalogItem.addAll(katalogItem)
        adapter.notifyDataSetChanged()
    }

    override fun displayProgress() {
        refreshLayput.isRefreshing = true
        katalogVew.visibility = View.GONE
    }

    override fun hideProgress() {
        refreshLayput.isRefreshing = false
        katalogVew.visibility = View.VISIBLE
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
