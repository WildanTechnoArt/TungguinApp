package com.hyperdev.tungguin.view.ui

import  android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.google.gson.Gson
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.adapter.ImageRefListAdapter
import com.hyperdev.tungguin.adapter.PriceListAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.addtocart.AddToCartResponse
import com.hyperdev.tungguin.model.detailproduct.FieldListFormatted
import com.hyperdev.tungguin.model.detailproduct.PriceList
import com.hyperdev.tungguin.model.detailproduct.ProductDetailItem
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkUtil
import com.hyperdev.tungguin.presenter.DetailProductPresenter
import com.hyperdev.tungguin.presenter.UploadFilePresenter
import com.hyperdev.tungguin.repository.DetailProductRepositoryImpl
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.view.*
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData
import kotlinx.android.synthetic.main.activity_order_design.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.properties.Delegates

class OrderDesignActivity : AppCompatActivity(), DetailProductView.View,
    UploadImageView.View, PriceListView, DeleteImageView {

    // Deklarasi Variable
    private var kondisi = false
    private lateinit var baseApiService: BaseApiService
    private lateinit var presenter: DetailProductView.Presenter
    private lateinit var presenterImage: UploadImageView.Presenter
    private lateinit var token: String
    private lateinit var hashed_id: String
    private var priceListDesain: MutableList<PriceList> = mutableListOf()
    private var adapter by Delegates.notNull<PriceListAdapter>()

    // Variable untuk Inisialisasi View pada Array
    private lateinit var inputTextViews: ArrayList<EditText?>
    private lateinit var inputNumberViews: ArrayList<EditText?>
    private lateinit var inputTextareaViews: ArrayList<EditText?>
    private lateinit var checkboxListViews: ArrayList<CheckBox?>
    private lateinit var spinnerList: ArrayList<Spinner?>
    private lateinit var editText: EditText
    private lateinit var textViews: TextView
    private lateinit var spinnerViews: Spinner
    private lateinit var checkboxViews: CheckBox

    private lateinit var imgFileList: ArrayList<FileData?>
    private lateinit var imgFileName: ArrayList<String?>

    private var spinnerOption: MutableList<String> = mutableListOf()
    private var checkboxOption: MutableList<String> = mutableListOf()
    private var options = HashMap<Int, String>()

    private var keyList: MutableList<String> = mutableListOf()
    private var typeList: MutableList<String> = mutableListOf()
    private var checkboxItem: MutableList<String> = mutableListOf()
    private var cartDataMap = HashMap<String, RequestBody>()
    private var productPrice: RequestBody? = null

    private var textIndex: Int = 0
    private var numberIndex: Int = 0
    private var textareaIndex: Int = 0
    private var spinnerIndex: Int = 0
    private lateinit var request: RequestBody
    private lateinit var requestFile: RequestBody
    private var imageBody: MutableList<MultipartBody.Part> = mutableListOf()
    private var fileBody: MultipartBody.Part? = null

    private var imgAdapter by Delegates.notNull<ImageRefListAdapter>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_design)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        RxPaparazzo.register(application)
        imgFileList = arrayListOf()
        imgFileName = arrayListOf()
        inputTextViews = arrayListOf()
        inputNumberViews = arrayListOf()
        inputTextareaViews = arrayListOf()
        spinnerList = arrayListOf()
        checkboxListViews = arrayListOf()

        spinnerOption.add("Pilih Opsi...")

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        token = SharedPrefManager.getInstance(this@OrderDesignActivity).token.toString()
        hashed_id = intent.getStringExtra("sendProductID").toString()
        cartDataMap["type"] = setRequestBody(hashed_id)

        baseApiService = NetworkUtil.getClient(this@OrderDesignActivity)!!
            .create(BaseApiService::class.java)

        val layout = GridLayoutManager(this@OrderDesignActivity, 3)
        priceList_view.layoutManager = layout
        priceList_view.setHasFixedSize(true)

        val request = DetailProductRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = DetailProductPresenter(this@OrderDesignActivity, this, request, scheduler)
        presenterImage = UploadFilePresenter(this@OrderDesignActivity, scheduler)

        adapter = PriceListAdapter(priceListDesain as ArrayList<PriceList>, this@OrderDesignActivity)

        presenter.getDetailProduct("Bearer $token", hashed_id)

        priceList_view.adapter = adapter

        val layoutImg = LinearLayoutManager(this@OrderDesignActivity)
        image_ref_list.layoutManager = layoutImg
        image_ref_list.setHasFixedSize(false)
        image_ref_list.isNestedScrollingEnabled = false

        order_desain.setOnClickListener {
            if(kondisi){
                textIndex = 0
                numberIndex = 0
                textareaIndex = 0
                spinnerIndex = 0

                cartDataMap["type"] = setRequestBody(hashed_id)
                cartDataMap["design_concept"] = setRequestBody(konsep_dsn.text.toString())
                for((index, data) in keyList.withIndex()){

                    if(typeList[index] == "text"){
                        cartDataMap["details[$data]"] = setRequestBody(inputTextViews[textIndex]?.text.toString())
                        textIndex++
                    }
                    if(typeList[index] == "number"){
                        cartDataMap["details[$data]"] = setRequestBody(inputNumberViews[numberIndex]?.text.toString())
                        numberIndex++
                    }
                    if(typeList[index] == "dropdown"){
                        if(options[spinnerIndex].toString() != "Pilih Opsi..."){
                            cartDataMap["details[$data]"] = setRequestBody(options[spinnerIndex].toString())
                            spinnerIndex++
                        }else{
                            cartDataMap["details[$data]"] = setRequestBody("null")
                        }
                    }
                    if(typeList[index] == "checkbox"){
                        val checkboxItem = StringBuilder()
                        for((posotion, item) in checkboxOption.withIndex()){
                            if(checkboxListViews[posotion]?.isChecked!!){
                                checkboxItem.append(item).append(", ")
                            }
                        }
                        val textBuilder = StringBuilder(checkboxItem)
                        if(textBuilder.isNotEmpty()){
                            textBuilder.deleteCharAt(checkboxItem.length - 2)
                            cartDataMap["details[$data]"] = setRequestBody(textBuilder.toString())
                        }
                    }
                    if(typeList[index] == "textarea"){
                        cartDataMap["details[$data]"] = setRequestBody(inputTextareaViews[textareaIndex]?.text.toString())
                        textareaIndex++
                    }

                }

                orderDesainRequest()
            }else{
                Toast.makeText(this@OrderDesignActivity, "Jumlah desain belum dipilih!", Toast.LENGTH_SHORT).show()
            }
        }

        referensi.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this@OrderDesignActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this@OrderDesignActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this@OrderDesignActivity, arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_STORAGE
                )

            } else {
                presenterImage.takePhotoGallery(this@OrderDesignActivity)
            }
        }

        dokumen.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this@OrderDesignActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this@OrderDesignActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this@OrderDesignActivity, arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_STORAGE
                )

            } else {
                presenterImage.takeFileDoc(this@OrderDesignActivity)
            }
        }
    }

    private fun loadImageFileName(imgFileName: ArrayList<String?>){
        imgAdapter = ImageRefListAdapter(imgFileName, this@OrderDesignActivity)
        image_ref_list.adapter = imgAdapter
    }

    private fun setRequestBody(data: String): RequestBody{
        return RequestBody.create(MediaType.parse("text/plain"), data)
    }

    override fun loadFileDoc(fileDoc: FileData?) {
        file_list_view.visibility = View.VISIBLE
        file_name.text = fileDoc?.filename
        requestFile = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), fileDoc?.file!!)
        fileBody = MultipartBody.Part.createFormData("design_document", fileDoc.filename, requestFile)

        file_close.setOnClickListener {
            fileBody = null
            file_name.text = ""
            file_list_view.visibility = View.GONE
        }
    }

    override fun onDeleteImage(position: Int) {
        imgFileList.removeAt(position)
    }

    override fun loadImage(fileData: MutableList<FileData?>) {

        fileData.forEach {
            imgFileList.add(it)
        }

        image_ref_list.visibility = View.VISIBLE

        fileData.forEach {
            imgFileName.add(it?.filename.toString())
        }

        loadImageFileName(imgFileName)
    }

    private fun saveimageBody(file: FileData?, nomor: String): MultipartBody.Part{
        request = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), file?.file!!)
        return MultipartBody.Part.createFormData("design_preference[$nomor]", file.filename, request)
    }

    private fun orderDesainRequest(){

        progressBar.visibility = View.VISIBLE
        shadow.visibility = View.VISIBLE

        for((nomor, data) in imgFileList.withIndex()){
            imageBody.add(saveimageBody(data, nomor.toString()))
        }

        cartDataMap["want_designer_id"] = setRequestBody(SharedPrefManager
            .getInstance(this@OrderDesignActivity).idDesigner.toString())

        baseApiService.addToCart("Bearer $token", "application/json", cartDataMap, imageBody, fileBody, productPrice)

            .enqueue(object : Callback<AddToCartResponse> {

                override fun onFailure(call: Call<AddToCartResponse>, t: Throwable) {
                    Log.e("RegisterPage.kt", "onFailure ERROR -> "+ t.message)
                    showSnackBarMessage(t.localizedMessage.toString())
                    progressBar.visibility = View.GONE
                    shadow.visibility = View.GONE
                }

                override fun onResponse(call: Call<AddToCartResponse>, response: Response<AddToCartResponse>) {

                    if (response.isSuccessful) {
                        Log.i("debug", "onResponse: BERHASIL")
                        try {
                            val message = response.body()?.meta?.message
                            if(message != null){
                                Toast.makeText(this@OrderDesignActivity, message.toString(), Toast.LENGTH_SHORT).show()
                            }
                            startActivity(Intent(this@OrderDesignActivity, CartActivity::class.java))
                            finish()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }else{
                        try{
                            progressBar.visibility = View.GONE
                            shadow.visibility = View.GONE
                            val gson = Gson()
                            val message = gson.fromJson(response.errorBody()?.charStream(), AddToCartResponse::class.java)
                            if(message != null){
                                Toast.makeText(this@OrderDesignActivity, message.meta?.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }catch (ex: IllegalStateException){
                            Toast.makeText(this@OrderDesignActivity, "Terjadi Kesalahan, Jangan Terlalu banyak Upload File!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
    }

    override fun showPriceList(priceList: List<PriceList>) {
        priceListDesain.addAll(priceList)
    }

    @SuppressLint("SetTextI18n")
    override fun showFieldList(fieldListFormatted: List<FieldListFormatted>) {

        // View Properties
        val marginDp8 = 8f
        val marginDp4 = 4f
        val marginPx8 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginDp8,
            applicationContext.resources.displayMetrics).toInt()
        val marginPx4 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginDp4,
            applicationContext.resources.displayMetrics).toInt()

        val paramsInput = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        paramsInput.setMargins(0, marginPx4, 0, marginPx8)

        val paramsText = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        paramsText.setMargins(0, marginPx8, 0, 0)

        val paddingDp = 10f
        val paddingPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingDp,
            applicationContext.resources.displayMetrics).toInt()

        for((index, data) in fieldListFormatted.withIndex()){
            if(data.type?.equals("text")!!){
                typeList.add(data.type.toString())
                keyList.add(data.key.toString())
                fieldListFormatted[index].required?.let {
                    editTextProperties(paddingPx, paramsInput, paramsText,
                        fieldListFormatted[index].placeholder.toString(),
                        fieldListFormatted[index].name.toString(), textIndex, it
                    )
                }
                textIndex++
            }
            if(data.type?.equals("number")!!){
                typeList.add(data.type.toString())
                keyList.add(data.key.toString())
                fieldListFormatted[index].required?.let {
                    editNumberProperties(paddingPx, paramsInput, paramsText,
                        fieldListFormatted[index].placeholder.toString(),
                        fieldListFormatted[index].name.toString(), numberIndex, it
                    )
                }
                numberIndex++
            }
            if(data.type?.equals("dropdown")!!){
                typeList.add(data.type.toString())
                keyList.add(data.key.toString())
                fieldListFormatted[index].options?.forEach {
                    spinnerOption.add(it.toString())
                }

                fieldListFormatted[index].required?.let {
                    spinnerProperties(paramsInput, paramsText, fieldListFormatted[index].name.toString(),
                        spinnerOption, spinnerIndex, it
                    )
                }
                spinnerIndex++
            }
            if(data.type?.equals("checkbox")!!){
                typeList.add(data.type.toString())
                keyList.add(data.key.toString())
                fieldListFormatted[index].options?.forEach {
                    checkboxOption.add(it.toString())
                }

                // TextView
                textViews = TextView(this@OrderDesignActivity)
                textViews.layoutParams = paramsText
                textViews.textSize = 15.5f
                textViews.setTextColor(Color.parseColor("#000000"))
                textViews.setTypeface(null, Typeface.BOLD)
                formatted_layout.addView(textViews)

                if(fieldListFormatted[index].required!!){
                    textViews.text = "${fieldListFormatted[index].name.toString()} *"
                }else{
                    textViews.text = fieldListFormatted[index].name.toString()
                }

                fieldListFormatted[index].options?.forEach {
                    checkboxItem.add(it.toString())
                }
                for((nomor, data2) in fieldListFormatted[index].options?.withIndex()!!){
                    checkboxProperties(paramsInput, data2.toString(), nomor)
                }
            }
            if(data.type?.equals("textarea")!!){
                typeList.add(data.type.toString())
                keyList.add(data.key.toString())
                fieldListFormatted[index].required?.let {
                    editTextareaProperties(paddingPx, paramsInput, paramsText,
                        fieldListFormatted[index].placeholder.toString(),
                        fieldListFormatted[index].name.toString(), textareaIndex, it
                    )
                }
                textareaIndex++
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun editTextProperties(paddingPx: Int, paramsInput: ViewGroup.LayoutParams,
                                   paramsText: ViewGroup.LayoutParams, palaceholder: String,
                                   text: String, index: Int, required: Boolean){

        // TextView
        textViews = TextView(this@OrderDesignActivity)
        textViews.layoutParams = paramsText
        textViews.textSize = 15.5f
        textViews.setTextColor(Color.parseColor("#000000"))
        textViews.setTypeface(null, Typeface.BOLD)

        if(required){
            textViews.text = "$text *"
        }else{
            textViews.text = text
        }

        editText = EditText(this@OrderDesignActivity)
        inputTextViews.add(editText)
        inputTextViews[index]?.layoutParams = paramsInput
        inputTextViews[index]?.setBackgroundResource(R.drawable.input_layout)
        inputTextViews[index]?.textSize = 14.0f
        inputTextViews[index]?.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
        inputTextViews[index]?.hint = palaceholder
        inputTextViews[index]?.inputType = InputType.TYPE_CLASS_TEXT

        formatted_layout.addView(textViews)
        formatted_layout.addView(inputTextViews[index])
    }

    @SuppressLint("SetTextI18n")
    private fun editNumberProperties(paddingPx: Int, paramsInput: ViewGroup.LayoutParams,
                                    paramsText: ViewGroup.LayoutParams, palaceholder: String,
                                    text: String, index: Int, required: Boolean){

        // TextView
        textViews = TextView(this@OrderDesignActivity)
        textViews.layoutParams = paramsText
        textViews.textSize = 15.5f
        textViews.setTextColor(Color.parseColor("#000000"))
        textViews.setTypeface(null, Typeface.BOLD)

        if(required){
            textViews.text = "$text *"
        }else{
            textViews.text = text
        }

        editText = EditText(this@OrderDesignActivity)
        inputNumberViews.add(editText)
        inputNumberViews[index]?.layoutParams = paramsInput
        inputNumberViews[index]?.setBackgroundResource(R.drawable.input_layout)
        inputNumberViews[index]?.textSize = 14.0f
        inputNumberViews[index]?.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
        inputNumberViews[index]?.hint = palaceholder
        inputNumberViews[index]?.inputType = InputType.TYPE_CLASS_NUMBER


        formatted_layout.addView(textViews)
        formatted_layout.addView(inputNumberViews[index])
    }

    @SuppressLint("SetTextI18n")
    private fun editTextareaProperties(paddingPx: Int, paramsInput: ViewGroup.LayoutParams,
                                    paramsText: ViewGroup.LayoutParams, palaceholder: String,
                                    text: String, index: Int, required: Boolean){

        // TextView
        textViews = TextView(this@OrderDesignActivity)
        textViews.layoutParams = paramsText
        textViews.textSize = 15.5f
        textViews.setTextColor(Color.parseColor("#000000"))
        textViews.setTypeface(null, Typeface.BOLD)

        if(required){
            textViews.text = "$text *"
        }else{
            textViews.text = text
        }

        editText = EditText(this@OrderDesignActivity)
        inputTextareaViews.add(editText)
        inputTextareaViews[index]?.layoutParams = paramsInput
        inputTextareaViews[index]?.setBackgroundResource(R.drawable.input_layout)
        inputTextareaViews[index]?.textSize = 14.0f
        inputTextareaViews[index]?.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
        inputTextareaViews[index]?.hint = palaceholder
        inputTextareaViews[index]?.setSingleLine(false)
        inputTextareaViews[index]?.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION

        formatted_layout.addView(textViews)
        formatted_layout.addView(inputTextareaViews[index])
    }

    @SuppressLint("SetTextI18n")
    private fun spinnerProperties(paramsInput: ViewGroup.LayoutParams, paramsText: ViewGroup.LayoutParams,
                                  text: String, spinnerOption: MutableList<String>, index: Int,required: Boolean){

        // TextView
        textViews = TextView(this@OrderDesignActivity)
        textViews.layoutParams = paramsText
        textViews.textSize = 15.5f
        textViews.setTextColor(Color.parseColor("#000000"))
        textViews.setTypeface(null, Typeface.BOLD)

        if(required){
            textViews.text = "$text *"
        }else{
            textViews.text = text
        }

        // Spinner
        spinnerViews = Spinner(this@OrderDesignActivity)
        spinnerList.add(spinnerViews)
        spinnerList[index]?.layoutParams = paramsInput
        spinnerList[index]?.adapter = ArrayAdapter(this@OrderDesignActivity,
            android.R.layout.simple_dropdown_item_1line, spinnerOption)

        spinnerList[index]?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                options[index] = spinnerOption[position]
            }
        }

        formatted_layout.addView(textViews)
        formatted_layout.addView(spinnerList[index])
    }

    @SuppressLint("SetTextI18n")
    private fun checkboxProperties(paramsInput: ViewGroup.LayoutParams, text: String, index: Int){

        // Checkbox
        checkboxViews = CheckBox(this@OrderDesignActivity)
        checkboxListViews.add(checkboxViews)
        checkboxListViews[index]?.layoutParams = paramsInput

        checkboxListViews[index]?.text = text
        formatted_layout.addView(checkboxListViews[index])
    }

    override fun showDetailProductItem(productItem: ProductDetailItem) {
        nama_desain.text = productItem.name.toString()
    }

    override fun displayProgress() {
        progressBar.visibility = View.VISIBLE
        scrollView.visibility = View.GONE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
        scrollView.visibility = View.VISIBLE
    }

    override fun hideProgressFile() {
        progressBar.visibility = View.GONE
        shadow.visibility = View.GONE
    }

    override fun showProgressFile() {
        progressBar.visibility = View.VISIBLE
        shadow.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    override fun shaowPriceList(priceFormatted: String, desainCount: String, kondisi: Boolean, price: String) {
        this.kondisi = kondisi
        jumlah_desain.text = "$desainCount Desain"
        cartDataMap["design_option"] = setRequestBody(desainCount)
        productPrice = setRequestBody(price)
        total_harga.text = priceFormatted
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar.make(order_desain_layout, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenterImage.takePhotoGallery(this@OrderDesignActivity)
                }

                return
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    companion object {
        const val PERMISSION_STORAGE = 2
    }
}