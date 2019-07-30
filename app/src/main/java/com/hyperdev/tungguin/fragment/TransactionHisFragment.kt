package com.hyperdev.tungguin.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.adapter.TransactionRecyclerAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.transaction.ListTransaction
import com.hyperdev.tungguin.model.transaction.TransactionHistory
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.TransactionHistoriPresenter
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.ui.view.HistoriTransactionView
import kotlin.properties.Delegates

class TransactionHisFragment : Fragment(), HistoriTransactionView.View {

    //Deklarasi Variable
    private var listTransaction: MutableList<ListTransaction> = mutableListOf()
    private lateinit var presenter: HistoriTransactionView.Presenter
    private lateinit var token: String
    private lateinit var recycler: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var txtImgNotFound: TextView
    private lateinit var baseApiService: BaseApiService
    private val TAG = javaClass.simpleName
    private var adapter by Delegates.notNull<TransactionRecyclerAdapter>()
    private var page: Int = 1
    private lateinit var nextPageURL: String
    private var isLoading by Delegates.notNull<Boolean>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_transaction_his, container, false)

        recycler = view.findViewById(R.id.recyclerTransactionList)
        progressBar = view.findViewById(R.id.progress_bar)
        txtImgNotFound = view.findViewById(R.id.tv_img_not_found)

        loadData()

        recycler.adapter = adapter

        initListener()

        return view
    }

    private fun loadData() {
        token = SharedPrefManager.getInstance(context!!).token.toString()

        baseApiService = NetworkClient.getClient(context!!)!!
            .create(BaseApiService::class.java)

        val layout = LinearLayoutManager(context)
        recycler.layoutManager = layout
        recycler.setHasFixedSize(true)

        val scheduler = AppSchedulerProvider()
        presenter = TransactionHistoriPresenter(this, context!!, baseApiService, scheduler)
        adapter = TransactionRecyclerAdapter(listTransaction as ArrayList<ListTransaction>)

        presenter.getTransactionHistory("Bearer $token", page = page)
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
                    presenter.getTransactionHistory("Bearer $token", page = page)
                }
            }
        })
    }

    override fun showTransaction(transaction: TransactionHistory) {
        nextPageURL = transaction.nextPageUrl.toString()
    }

    override fun onSuccess() {
        progressBar.visibility = View.GONE
        isLoading = false
    }

    override fun displayProgress() {
        progressBar.visibility = View.VISIBLE
        isLoading = true
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
    }

    override fun showTransactionHistory(dataTransaction: List<ListTransaction>) {
        if (page == 1) {
            listTransaction.clear()
            listTransaction.addAll(dataTransaction)
            adapter.notifyDataSetChanged()
        } else {
            adapter.refreshAdapter(dataTransaction)
        }

        if (adapter.itemCount != 0) {
            txtImgNotFound.visibility = View.GONE
            recycler.visibility = View.VISIBLE
        } else {
            txtImgNotFound.visibility = View.VISIBLE
            recycler.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}
