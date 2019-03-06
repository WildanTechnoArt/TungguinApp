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
import com.hyperdev.tungguin.adapter.TopUpRecyclerAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.topuphistori.ListTopUp
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkUtil
import com.hyperdev.tungguin.presenter.TopUpHistoriPresenter
import com.hyperdev.tungguin.repository.TopUpHIstoryRepositoryImpl
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.view.HistoriTopUpView
import kotlin.properties.Delegates

class TopUpHisFragment : Fragment(), HistoriTopUpView.View{

    //Deklarasi Variable
    private var listTopUp: MutableList<ListTopUp> = mutableListOf()
    private lateinit var presenter: HistoriTopUpView.Presenter
    private lateinit var token: String
    private lateinit var recycler : RecyclerView
    private lateinit var progressBar : ProgressBar
    private lateinit var baseApiService: BaseApiService
    private val TAG = javaClass.simpleName
    private var adapter by Delegates.notNull<TopUpRecyclerAdapter>()
    private var page by Delegates.notNull<Int>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_top_up_his, container, false)

        page = 1

        recycler = view.findViewById(R.id.recyclerTopupList)
        progressBar = view.findViewById(R.id.progressBar)

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

        val request = TopUpHIstoryRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = TopUpHistoriPresenter(this, request, scheduler)
        adapter = TopUpRecyclerAdapter(context, listTopUp as ArrayList<ListTopUp>)

        presenter.getTopUpHistory(context!!, "Bearer $token", page = page)
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
                    presenter.getTopUpHistory(context!!, "Bearer $token", page = page)
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

    override fun showTopUpHistory(data: List<ListTopUp>) {
        if(page == 1){
            listTopUp.clear()
            listTopUp.addAll(data)
            adapter.notifyDataSetChanged()
        }else{
            adapter.refreshAdapter(data)
        }
    }
}