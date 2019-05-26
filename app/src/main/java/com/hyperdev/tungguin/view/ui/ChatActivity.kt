package com.hyperdev.tungguin.view.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.hyperdev.tungguin.GlideApp
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.adapter.MessageAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.chat.ChatData
import com.hyperdev.tungguin.model.chat.HistoriItem
import com.hyperdev.tungguin.model.chat.MessageModel
import com.hyperdev.tungguin.model.detailorder.DesainerProfile
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkUtil
import com.hyperdev.tungguin.presenter.ChatHIstoriPresenter
import com.hyperdev.tungguin.repository.ChatHIstoriRepositoryImpl
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.view.ChatHistoriView
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData
import kotlinx.android.synthetic.main.activity_chat.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URISyntaxException
import kotlin.properties.Delegates

class ChatActivity : AppCompatActivity(), ChatHistoriView.View {

    //Deklarasi Variable
    private var listChatItem: MutableList<ChatData> = mutableListOf()
    private lateinit var presenter: ChatHistoriView.Presenter
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

    // WebSocket
    private var socket: Socket? = null

    private fun initSocket(){
        try{
            socket = IO.socket("https://tungguin-socket.azishapidin.com/")
        }catch (ex: URISyntaxException){
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

        page = 1

        RxPaparazzo.register(application)

        initSocket()
        loadData()

        socket?.on("order", chatDesigner)
        socket?.connect()
        socket?.emit("order", orderId)

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
                ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this@ChatActivity, arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    OrderDesignActivity.PERMISSION_STORAGE
                )

            }else{
                presenter.takeFile(this@ChatActivity)
            }
        }

        refreshLayput.setOnRefreshListener {
            presenter.getChatData("Bearer $token", orderId, page = page)
        }

        initListener()

        chat_input.addTextChangedListener(object : TextWatcher{

            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isEmpty()){
                    send_chat.isEnabled = false
                    send_chat.setBackgroundResource(R.drawable.menu_round_no_hover2)
                }else{
                    send_chat.isEnabled = true
                    send_chat.setBackgroundResource(R.drawable.ref_round_button)
                }
            }

        })
    }

    private val scrollListener = object: RecyclerView.OnScrollListener() {
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

    private fun loadData(){
        orderId = intent.getStringExtra("sendOrderID").toString()
        token = SharedPrefManager.getInstance(this@ChatActivity).token.toString()

        baseApiService = NetworkUtil.getClient(this@ChatActivity)!!
            .create(BaseApiService::class.java)

        val layout = LinearLayoutManager(this@ChatActivity)
        list_chat.layoutManager = layout
        list_chat.setHasFixedSize(true)

        val request = ChatHIstoriRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = ChatHIstoriPresenter(this, this@ChatActivity, request, scheduler)

        adapter = MessageAdapter(this@ChatActivity, listChatItem as ArrayList<ChatData>)

        presenter.getChatData("Bearer $token", orderId, page = page)
        presenter.getDetailOrder("Bearer $token", orderId)

        list_chat.adapter = adapter
    }

    private fun setRequestBody(data: String): RequestBody{
        return RequestBody.create(MediaType.parse("text/plain"), data)
    }

    private fun sendMessage(message: String){

        refreshLayput.isRefreshing = true

        requestMessage = setRequestBody(message)

        baseApiService.chatRequest("Bearer $token", "application/json",
            orderId, requestMessage, fileBody)

            .enqueue(object : Callback<MessageModel> {

                override fun onFailure(call: Call<MessageModel>, t: Throwable) {
                    Log.e("RegisterPage.kt", "onFailure ERROR -> "+ t.message)
                    Toast.makeText(this@ChatActivity, t.message.toString(), Toast.LENGTH_LONG).show()
                    refreshLayput.isRefreshing = true
                }

                override fun onResponse(call: Call<MessageModel>, response: Response<MessageModel>) {

                    if (response.isSuccessful) {

                        Log.i("debug", "onResponse: BERHASIL")
                        refreshLayput.isRefreshing = false
                        fileBody = null

                    }else{

                        try{
                            refreshLayput.isRefreshing = false
                            val gson = Gson()
                            val alertMessage = gson.fromJson(response.errorBody()?.charStream(), MessageModel::class.java)
                            Toast.makeText(this@ChatActivity, alertMessage.meta?.message.toString(), Toast.LENGTH_SHORT).show()
                        }catch (ex: IllegalStateException){
                            Toast.makeText(this@ChatActivity, "Terjadi Kesalahan, Jangan Terlalu banyak Upload File!", Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            })
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

            try{

                type = data.getString("type")
                text = jsonObject.getString("text")
                date = jsonObject.getString("formatted_date")
                file = jsonObject.getString("file")
                sender = jsonObject.getString("sender")

                if(type != null){
                    when(type){
                        "new_message" ->{
                            chatData = if(sender.toString() == "customer"){
                                ChatData(TYPE_MESSAGE_SENT, text, file, date, sender)
                            }else{
                                ChatData(TYPE_MESSAGE_RECEIVED, text, file, date, sender)
                            }
                            adapter.addMessage(chatData)
                            list_chat.post { list_chat.smoothScrollToPosition(adapter.itemCount - 1) }

                        }
                    }
                }
            }catch (ex: JSONException){
                ex.printStackTrace()
            }
        }
    }

    override fun showChatData(historiItem: HistoriItem?) {
        nextPageURL = historiItem?.nextPageUrl.toString()
    }

    override fun loadFile(file: FileData?) {
        requestFile = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), file?.file!!)
        fileBody = MultipartBody.Part.createFormData("file", file.filename, requestFile)
        sendMessage("null")
    }

    override fun showProgress() {
        refreshLayput.isRefreshing = true
        isLoading = true
    }

    override fun hideProgress() {
        refreshLayput.isRefreshing = false
    }

    override fun onSuccess() {
        refreshLayput.isRefreshing = false
        isLoading = false
    }

    override fun profileDesigner(designer: DesainerProfile) {
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

    override fun showChatItem(chatlist: List<ChatData>) {
        if(page == 1){
            listChatItem.clear()
            listChatItem.addAll(chatlist)
            adapter.notifyDataSetChanged()
        }else{
            adapter.refreshAdapter(chatlist)
        }
    }
}