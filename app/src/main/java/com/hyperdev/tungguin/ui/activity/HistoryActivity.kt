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
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.HistoryPresenter
import com.hyperdev.tungguin.ui.view.BalanceView
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_history.*
import retrofit2.HttpException
import java.net.SocketTimeoutException

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
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        history_tabs.addTab(history_tabs.newTab().setText("Histori Top Up"))
        history_tabs.addTab(history_tabs.newTab().setText("Histori Transaksi"))

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

        btn_topup.setOnClickListener {
            startActivity(Intent(this, TopUpActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        baseApiService = NetworkClient.getClient(this)
            .create(BaseApiService::class.java)

        getToken = SharedPrefManager.getInstance(this).token.toString()

        val scheduler = AppSchedulerProvider()
        presenter = HistoryPresenter(this, baseApiService, scheduler)
        presenter.getUserBalance("Bearer $getToken")

        //Memanggil dan Memasukan Value pada Class PagerAdapter(FragmentManager dan JumlahTab)
        val pageAdapter = PagerAdapter(
            supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, history_tabs.tabCount
        )

        //Memasang Adapter pada ViewPager
        viewpager.adapter = pageAdapter
    }

    override fun showTransaction(transactionItem: DataTransaction) {
        tv_saldo_wallet.text = transactionItem.balance.toString()
    }

    override fun showProgressBar() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progress_bar.visibility = View.GONE
    }

    override fun onSuccess() {
        progress_bar.visibility = View.GONE
        appbar.visibility = View.VISIBLE
        viewpager.visibility = View.VISIBLE
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
}
