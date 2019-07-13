package com.hyperdev.tungguin.ui.activity

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.adapter.SearchProductAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.searchproduct.SearchItem
import com.hyperdev.tungguin.model.searchproduct.SearchProductData
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.SearchProductPresenter
import com.hyperdev.tungguin.repository.product.ProductRepositoryImpl
import com.hyperdev.tungguin.ui.view.SearchProductView
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import kotlinx.android.synthetic.main.activity_search_product.*
import java.util.*

class SearchProductActivity : AppCompatActivity(), SearchProductView.View, View.OnClickListener {

    private lateinit var mPresenter: SearchProductView.Presenter
    private var mBaseApiService: BaseApiService? = null
    private lateinit var mAdapter: SearchProductAdapter
    private lateinit var mNextPageUrl: String
    private var mListProduct = arrayListOf<SearchItem>()
    private var mToken: String? = null
    private var mIsLoading = false
    private var mPage: Int = 1
    private var mProductName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_product)
        prepare()
        recyclerViewScrollListener()
        swipe_refresh.setOnRefreshListener {
            mPresenter.getProductByName(mToken.toString(), mProductName)
        }
        tv_search_product.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mPresenter.getProductByName(mToken.toString(), name = tv_search_product.text.toString())
                return@OnEditorActionListener true
            }
            false
        })
        tv_search_product.addTextChangedListener(object : TextWatcher {

            private var timer = Timer()
            private val DELAY: Long = 1000

            override fun afterTextChanged(s: Editable?) {
                timer.cancel()
                timer = Timer()
                timer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            runOnUiThread {
                                mPresenter.getProductByName(mToken.toString(), s.toString())
                            }
                        }
                    },
                    DELAY
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
    }

    private fun prepare() {
        tv_contact_us.setOnClickListener(this)
        img_back.setOnClickListener(this)
        img_search.setOnClickListener(this)

        mProductName = tv_search_product.text.toString()

        mBaseApiService = NetworkClient.getClient(this)
            ?.create(BaseApiService::class.java)

        val repository = mBaseApiService?.let { ProductRepositoryImpl(it) }
        val scheduler = AppSchedulerProvider()

        mPresenter = SearchProductPresenter(this, this, repository, scheduler)

        mToken = "Bearer ${SharedPrefManager.getInstance(this).token.toString()}"

        mAdapter = SearchProductAdapter(this, mListProduct)

        rv_product_list.layoutManager = LinearLayoutManager(this)
        rv_product_list.setHasFixedSize(true)
        rv_product_list.adapter = mAdapter

        mPresenter.getProductByName(mToken.toString(), mProductName)
    }

    private fun recyclerViewScrollListener() {
        rv_product_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val countItem = linearLayoutManager.itemCount
                val lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                val isLastPosition = countItem.minus(1) == lastVisiblePosition
                if (!mIsLoading && isLastPosition && mNextPageUrl != "null") {
                    mPage = mPage.plus(1)
                    mPresenter.getProductByPage(mToken.toString(), page = mPage)
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_contact_us -> startActivity(Intent(this, ContactUsActivity::class.java))
            R.id.img_back -> finish()
            R.id.img_search -> mPresenter.getProductByName(mToken.toString(), name = tv_search_product.text.toString())
        }
    }

    override fun showProductByName(product: List<SearchItem>) {
        mListProduct.clear()
        mListProduct.addAll(product)
        if (mListProduct.isEmpty()) {
            rv_product_list.visibility = View.GONE
            img_cross.visibility = View.VISIBLE
            tv_message1.visibility = View.VISIBLE
            tv_message2.visibility = View.VISIBLE
            tv_contact_us.visibility = View.VISIBLE
            tv_contact_us.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        } else {
            rv_product_list.visibility = View.VISIBLE
            img_cross.visibility = View.GONE
            tv_message1.visibility = View.GONE
            tv_message1.visibility = View.GONE
            tv_contact_us.visibility = View.GONE
        }
        mAdapter.notifyDataSetChanged()
    }

    override fun showProductByPage(product: List<SearchItem>) {
        if (mPage == 1) {
            mListProduct.clear()
            mListProduct.addAll(product)
            mAdapter.notifyDataSetChanged()
        } else {
            mAdapter.refreshAdapter(product)
        }
    }

    override fun getProductPageUrl(product: SearchProductData) {
        mNextPageUrl = product.nextPageUrl.toString()
    }

    override fun showSearchProgressBar() {
        mIsLoading = true
        swipe_refresh.isRefreshing = true
    }

    override fun hideSearchProgressBar() {
        mIsLoading = false
        swipe_refresh.isRefreshing = false
    }
}