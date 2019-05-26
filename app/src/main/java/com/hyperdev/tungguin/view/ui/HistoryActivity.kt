package com.hyperdev.tungguin.view.ui

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.view.View
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.adapter.PagerAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.transactionhistory.DataTransaction
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkUtil
import com.hyperdev.tungguin.presenter.HistoryPresenter
import com.hyperdev.tungguin.repository.TransactionHistoryRepositoryImpl
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.view.BalanceView
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity(), BalanceView.View {

    //Deklarasi Variable
    private lateinit var baseApiService: BaseApiService
    private lateinit var getToken: String
    private lateinit var presenter: BalanceView.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        collapsing_toolbar.title = " "

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        history_tabs.addTab(history_tabs.newTab().setText("Histori Transaksi"))
        history_tabs.addTab(history_tabs.newTab().setText("Histori Top Up"))

        /*
         Menambahkan Listener yang akan dipanggil kapan pun halaman berubah atau
         bergulir secara bertahap, sehingga posisi tab tetap singkron
         */
        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(history_tabs))

        //Callback Interface dipanggil saat status pilihan tab berubah.
        history_tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                //Dipanggil ketika tab memasuki state/keadaan yang dipilih.
                viewpager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        topup.setOnClickListener {
            startActivity(Intent(this@HistoryActivity, TopUpActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        disabledView()
        loadData()
    }

    private fun loadData(){

        val connectivity  = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivity.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
            baseApiService = NetworkUtil.getClient(this@HistoryActivity)!!
                .create(BaseApiService::class.java)

            getToken = SharedPrefManager.getInstance(this@HistoryActivity).token.toString()

            val request = TransactionHistoryRepositoryImpl(baseApiService)
            val scheduler = AppSchedulerProvider()
            presenter = HistoryPresenter(this, this@HistoryActivity, request, scheduler)
            presenter.getUserBalance("Bearer $getToken")

            //Memanggil dan Memasukan Value pada Class PagerAdapter(FragmentManager dan JumlahTab)
            val pageAdapter = supportFragmentManager?.let {
                PagerAdapter(it, history_tabs.tabCount
                )
            }

            //Memasang Adapter pada ViewPager
            viewpager.adapter = pageAdapter
        } else {
            disabledView()
            Snackbar.make(histori_layout, "Tidak terhubung dengan internet !", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun displayTransaction(transactionItem: DataTransaction) {
        my_saldo.text = transactionItem.balance.toString()
    }

    override fun displayProgress() {
        disabledView()
    }

    override fun hideProgress() {
        enabledView()
    }

    private fun enabledView(){
        progress_bar.visibility = View.GONE
        appbar.visibility = View.VISIBLE
        viewpager.visibility = View.VISIBLE
    }

    private fun disabledView(){
        progress_bar.visibility = View.VISIBLE
        appbar.visibility = View.GONE
        viewpager.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
