package com.hyperdev.tungguin.ui.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.adapter.PagerAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.transaction.DataTransaction
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.HistoryPresenter
import com.hyperdev.tungguin.repository.transaction.TransactionRepositoryImp
import com.hyperdev.tungguin.ui.view.BalanceView
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity(), BalanceView.View {

    //Deklarasi Variable
    private lateinit var baseApiService: BaseApiService
    private lateinit var getToken: String
    private lateinit var presenter: BalanceView.Presenter

    companion object {
        const val BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT = 1
    }

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

    private fun loadData() {
        baseApiService = NetworkClient.getClient(this@HistoryActivity)!!
            .create(BaseApiService::class.java)

        getToken = SharedPrefManager.getInstance(this@HistoryActivity).token.toString()

        val repository = TransactionRepositoryImp(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = HistoryPresenter(this, this@HistoryActivity, repository, scheduler)
        presenter.getUserBalance("Bearer $getToken")

        //Memanggil dan Memasukan Value pada Class PagerAdapter(FragmentManager dan JumlahTab)
        val pageAdapter = PagerAdapter(
            supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, history_tabs.tabCount
        )

        //Memasang Adapter pada ViewPager
        viewpager.adapter = pageAdapter
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

    private fun enabledView() {
        progress_bar.visibility = View.GONE
        appbar.visibility = View.VISIBLE
        viewpager.visibility = View.VISIBLE
    }

    private fun disabledView() {
        progress_bar.visibility = View.VISIBLE
        appbar.visibility = View.GONE
        viewpager.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
