package com.hyperdev.tungguin.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.adapter.ChatListAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.chat.ChatListData
import com.hyperdev.tungguin.model.chat.ChatListItem
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.repository.chat.ChatRepositoryImp
import com.hyperdev.tungguin.ui.view.ChatListView
import com.hyperdev.tungguin.utils.UtilsConstant.Companion.SAVED_STATE
import com.hyperdev.tungguin.viewmodel.ChatListViewModel
import kotlinx.android.synthetic.main.activity_chat_list.*
import retrofit2.HttpException
import java.net.SocketTimeoutException
import kotlin.properties.Delegates

class ChatListActivity : AppCompatActivity(), ChatListView.View {

    private lateinit var chatListViewModel: ChatListViewModel
    private lateinit var mToken: String
    private var baseApiService: BaseApiService? = null
    private var adapter by Delegates.notNull<ChatListAdapter>()
    private var page: Int = 1
    private var nextPageUrl: String? = null
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        prepare()

        if (savedInstanceState == null) {
            requestChatList()
        }

        swipe_refresh.setOnRefreshListener {
            requestChatList()
        }

    }

    private fun prepare() {
        chatListViewModel = ViewModelProviders.of(this).get(ChatListViewModel::class.java)
        chatListViewModel.getChatList().observe(this, getChatList)

        mToken = SharedPrefManager.getInstance(this@ChatListActivity).token.toString()

        adapter = ChatListAdapter()
        adapter.notifyDataSetChanged()

        rv_chat_list.setHasFixedSize(true)
        rv_chat_list.layoutManager = LinearLayoutManager(this)
        rv_chat_list.addOnScrollListener(scrollListener)

        baseApiService = NetworkClient.getClient(this)
            ?.create(BaseApiService::class.java)

        rv_chat_list.adapter = adapter
    }

    private val getChatList =
        Observer<ArrayList<ChatListItem>> { chatItems ->
            if (chatItems != null) {
                if (page == 1) {
                    adapter.setData(chatItems)
                } else {
                    adapter.refreshAdapter(chatItems)
                }
            }
        }

    private fun requestChatList() {
        val repository = baseApiService?.let { ChatRepositoryImp(it) }
        repository?.let {
            chatListViewModel.setChatList(
                it, this,
                "Bearer $mToken",
                "application/json", page = page
            )
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
            val countItem = linearLayoutManager.itemCount
            val lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()
            val isLastPosition = countItem.minus(1) == lastVisiblePosition
            if (!isLoading && isLastPosition && nextPageUrl != "null") {
                page = page.plus(1)
                requestChatList()
            }
        }
    }

    override fun showChatListData(data: ChatListData) {
        nextPageUrl = data.next_page_url.toString()
    }

    override fun showProgressBar() {
        swipe_refresh.isRefreshing = true
    }

    override fun hideProgressBar() {
        swipe_refresh.isRefreshing = false
    }

    override fun onSuccess() {
        swipe_refresh.isRefreshing = false
    }

    override fun handleError(t: Throwable?) {
        if (ConnectivityStatus.isConnected(this)) {
            when (t) {
                is HttpException -> // non 200 error codes
                    HandleError.handleError(t, t.code(), this)
                is SocketTimeoutException -> // connection errors
                    Toast.makeText(this, "Connection Timeout!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Tidak Terhubung Dengan Internet!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVED_STATE, "saved")
    }

    override fun onDestroy() {
        super.onDestroy()
        chatListViewModel.onDestroy()
    }
}