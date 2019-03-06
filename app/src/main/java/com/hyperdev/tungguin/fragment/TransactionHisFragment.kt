package com.hyperdev.tungguin.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.adapter.TransactionRecyclerAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.transactionhistory.ListTransaction
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkUtil
import com.hyperdev.tungguin.presenter.TransactionHistoriPresenter
import com.hyperdev.tungguin.repository.TransactionHistoryRepositoryImpl
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.view.HistoriTransactionView
import kotlin.properties.Delegates

class TransactionHisFragment : Fragment(), HistoriTransactionView.View {

    //Deklarasi Variable
    private var listTransaction: MutableList<ListTransaction> = mutableListOf()
    private lateinit var presenter: HistoriTransactionView.Presenter
    private lateinit var token: String
    private lateinit var recycler : RecyclerView
    private lateinit var progressBar : ProgressBar
    private lateinit var baseApiService: BaseApiService
    private val TAG = javaClass.simpleName
    private var adapter by Delegates.notNull<TransactionRecyclerAdapter>()
    private var page by Delegates.notNull<Int>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_transaction_his, container, false)

        recycler = view.findViewById(R.id.recyclerTransactionList)
        progressBar = view.findViewById(R.id.progressBar)

        page = 1

        loadData()

        recycler.adapter = adapter

        initListener()

        return view
    }

    private fun loadData(){
        token = SharedPrefManager.getInstance(context!!).token.toString()

        baseApiService = NetworkUtil.getClient()!!
            .create(BaseApiService::class.java)

        val layout = LinearLayoutManager(context)
        recycler.layoutManager = layout
        recycler.setHasFixedSize(true)

        val request = TransactionHistoryRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = TransactionHistoriPresenter(this, request, scheduler)
        adapter = TransactionRecyclerAdapter(listTransaction as ArrayList<ListTransaction>)

        presenter.getTransactionHistory(context!!, "Bearer $token", page = page)
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
                if (isLastPosition) {
                    page = page.plus(1)
                    presenter.getTransactionHistory(context!!, "Bearer $token", page = page)
                }
            }
        })
    }

    override fun displayProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
    }

    override fun showTransactionHistory(dataTransaction: List<ListTransaction>) {
        if(page == 1){
            listTransaction.clear()
            listTransaction.addAll(dataTransaction)
            adapter.notifyDataSetChanged()
        }else{
            adapter.refreshAdapter(dataTransaction)
        }
    }
}
