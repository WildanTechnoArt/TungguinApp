package com.hyperdev.tungguin.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.hyperdev.tungguin.BuildConfig
import com.hyperdev.tungguin.GlideApp
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.adapter.MessageAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.chat.ChatData
import com.hyperdev.tungguin.model.chat.HistoriItem
import com.hyperdev.tungguin.model.detailorder.DesignerData
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.ChatPresenter
import com.hyperdev.tungguin.repository.chat.ChatRepositoryImp
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.ui.view.ChatView
import com.hyperdev.tungguin.utils.UtilsConstant.Companion.HASHED_ID
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData
import kotlinx.android.synthetic.main.activity_chat.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException
import kotlin.properties.Delegates

class ChatActivity : AppCompatActivity(), ChatView.View {

    //Deklarasi Variable
    private var listChatItem: MutableList<ChatData> = mutableListOf()
    private lateinit var presenter: ChatView.Presenter
    private lateinit var token: String
    private lateinit var baseApiService: BaseApiService
    private var adapter by Delegates.notNull<MessageAdapter>()
    private lateinit var orderId: String
    private lateinit var chatData: ChatData
    private lateinit var designerName: String
    private var page by Delegates.notNull<Int>()
    private var isLoading by Delegates.notNull<Boolean>()
    private lateinit var requestFile: RequestBody
    private lateinit var requestMessage: RequestBody
    private var fileBody: MultipartBody.Part? = null
    private lateinit var nextPageURL: String
    private lateinit var prevPageUrl: String

    // WebSocket
    private var socket: Socket? = null

    private fun initSocket() {
        try {
            socket = IO.socket(BuildConfig.WEBSOCKET_URL)
        } catch (ex: URISyntaxException) {
            ex.printStackTrace()
        }
    }

    companion object {
        const val TYPE_MESSAGE_SENT: Int = 0
        const val TYPE_MESSAGE_RECEIVED: Int = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setSupportActionBar(toolbar)

        initSocket()
        initData()

        send_chat.setOnClickListener {

            //Menyembunyikan keyboard saat tombol Kirim Pesan Diklik
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(chat_layout.windowToken, 0)
            sendMessage(chat_input.text.toString())
            chat_input.setText("")

        }

        attach_file.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this@ChatActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this@ChatActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this@ChatActivity, arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    OrderDesignActivity.PERMISSION_STORAGE
                )

            } else {
                presenter.takeFile(this@ChatActivity)
            }
        }

        initListener()

        chat_input.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) {
                    send_chat.isEnabled = false
                    send_chat.setBackgroundResource(R.drawable.menu_round_no_hover2)
                } else {
                    send_chat.isEnabled = true
                    send_chat.setBackgroundResource(R.drawable.ref_round_button)
                }
            }

        })
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
            val countItem = linearLayoutManager.itemCount
            val lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()
            val isLastPosition = countItem.minus(1) == lastVisiblePosition
            if (!isLoading && isLastPosition && nextPageURL != "null") {
                page = page.plus(1)
                presenter.getChatData("Bearer $token", orderId, page = page)
            }
        }
    }

    private fun initListener() {
        list_chat.addOnScrollListener(scrollListener)
    }

    private fun initData() {
        swipe_refresh.isEnabled = false
        page = 1

        RxPaparazzo.register(application)

        orderId = intent?.getStringExtra(HASHED_ID).toString()

        socket?.on("order", chatDesigner)
        socket?.connect()
        socket?.emit("order", orderId)

        token = SharedPrefManager.getInstance(this@ChatActivity).token.toString()

        baseApiService = NetworkClient.getClient(this@ChatActivity)!!
            .create(BaseApiService::class.java)

        val layout = LinearLayoutManager(this@ChatActivity)
        list_chat.layoutManager = layout
        list_chat.setHasFixedSize(true)

        val request = ChatRepositoryImp(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = ChatPresenter(this, this@ChatActivity, request, scheduler)

        adapter = MessageAdapter(this@ChatActivity, listChatItem)

        presenter.getChatData("Bearer $token", orderId, page = page)
        presenter.getDetailOrder("Bearer $token", orderId)

        list_chat.adapter = adapter
    }

    private fun setRequestBody(data: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), data)
    }

    private fun sendMessage(message: String) {
        requestMessage = setRequestBody(message)
        presenter.sendMessage(
            "Bearer $token", "application/json",
            orderId, requestMessage, fileBody
        )
    }

    @SuppressLint("SetTextI18n")
    private val chatDesigner = Emitter.Listener { args ->
        runOnUiThread {

            val data = args[0] as JSONObject
            val jsonObject = data.getJSONObject("data")
            val type: String?
            val text: String?
            val date: String?
            val file: String?
            val sender: String?
            val fileType: String?

            try {

                type = data.getString("type")
                text = jsonObject.getString("text")
                date = jsonObject.getString("formatted_date")
                file = jsonObject.getString("file")
                sender = jsonObject.getString("sender")
                fileType = jsonObject.getString("file_type")

                if (type != null) {
                    when (type) {
                        "new_message" -> {
                            chatData = if (sender.toString() == "customer") {
                                ChatData(TYPE_MESSAGE_SENT, text, file, date, sender, fileType)
                            } else {
                                ChatData(TYPE_MESSAGE_RECEIVED, text, file, date, sender, fileType)
                            }
                            adapter.addMessage(chatData)
                            list_chat.post { list_chat.smoothScrollToPosition(adapter.itemCount - 1) }
                        }
                    }
                }
            } catch (ex: JSONException) {
                ex.printStackTrace()
            }
        }
    }

    override fun onChatSuccess() {
        swipe_refresh.isRefreshing = false
        fileBody = null
    }

    override fun noInternetConnection(message: String) {
        Toast.makeText(this@ChatActivity, message, Toast.LENGTH_SHORT).show()
    }

    override fun showChatData(historiItem: HistoriItem?) {
        nextPageURL = historiItem?.nextPageUrl.toString()
        prevPageUrl = historiItem?.prevPageUrl.toString()
    }

    override fun loadFile(file: FileData?) {
        requestFile = RequestBody.create("application/x-www-form-urlencoded".toMediaTypeOrNull(), file?.file!!)
        fileBody = MultipartBody.Part.createFormData("file", file.filename, requestFile)
        sendMessage("null")
    }

    override fun showProgress() {
        swipe_refresh.isRefreshing = true
        isLoading = true
    }

    override fun hideProgress() {
        swipe_refresh.isRefreshing = false
        isLoading = false
    }

    override fun onSuccess() {
        swipe_refresh.isRefreshing = false
        isLoading = false
    }

    override fun profileDesigner(designer: DesignerData) {
        designerName = designer.name.toString()
        designer_name.text = designerName
        GlideApp.with(this@ChatActivity)
            .load(designer.photoUrl.toString())
            .placeholder(R.drawable.circle_profil)
            .into(photo)
    }

    override fun onDestroy() {
        super.onDestroy()
        socket?.disconnect()
        socket?.off("order", chatDesigner)
        presenter.onDestroy()
    }

    override fun showChatItem(chatlist: MutableList<ChatData>) {
        if(page == 1){
            adapter.setData(chatlist)
        }else{
            adapter.refreshAdapter(chatlist)
        }
    }
}