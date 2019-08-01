package com.hyperdev.tungguin.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.adapter.ImageRefListAdapter
import com.hyperdev.tungguin.adapter.PriceListAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.detailproduct.FieldListFormatted
import com.hyperdev.tungguin.model.detailproduct.PriceList
import com.hyperdev.tungguin.model.detailproduct.ProductDetailItem
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.AddToCartPresenter
import com.hyperdev.tungguin.presenter.DetailProductPresenter
import com.hyperdev.tungguin.presenter.UploadFilePresenter
import com.hyperdev.tungguin.ui.view.*
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.utils.UtilsConstant.Companion.HASHED_ID
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_order_design.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.net.SocketTimeoutException
import kotlin.properties.Delegates

class OrderDesignActivity : AppCompatActivity(), DetailProductView.View, UploadImageView.View,
    PriceListView, DeleteImageView, AddToCartView.View {

    // Deklarasi Variable
    private var kondisi = false
    private lateinit var baseApiService: BaseApiService
    private lateinit var presenter: DetailProductView.Presenter
    private lateinit var presenterImage: UploadImageView.Presenter
    private lateinit var presenterCart: AddToCartView.Presenter
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

    @SuppressLint("UseSparseArrays")
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
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        token = SharedPrefManager.getInstance(this).token.toString()
        hashed_id = intent?.getStringExtra(HASHED_ID).toString()
        cartDataMap["type"] = setRequestBody(hashed_id)

        baseApiService = NetworkClient.getClient(this)
            .create(BaseApiService::class.java)

        val layout = GridLayoutManager(this, 3)
        rv_price_list.layoutManager = layout
        rv_price_list.setHasFixedSize(true)

        val scheduler = AppSchedulerProvider()
        presenter = DetailProductPresenter(this, baseApiService, scheduler)
        presenterCart = AddToCartPresenter(this, baseApiService, scheduler)
        presenterImage = UploadFilePresenter(this, scheduler)

        presenter.getDetailProduct("Bearer $token", hashed_id)

        val layoutImg = LinearLayoutManager(this)
        rv_refrence_list.layoutManager = layoutImg
        rv_refrence_list.setHasFixedSize(false)
        rv_refrence_list.isNestedScrollingEnabled = false

        btn_design_order.setOnClickListener {
            if (kondisi) {
                textIndex = 0
                numberIndex = 0
                textareaIndex = 0
                spinnerIndex = 0

                cartDataMap["type"] = setRequestBody(hashed_id)
                cartDataMap["design_concept"] = setRequestBody(input_design_concept.text.toString())
                for ((index, data) in keyList.withIndex()) {

                    if (typeList[index] == "text") {
                        cartDataMap["details[$data]"] = setRequestBody(inputTextViews[textIndex]?.text.toString())
                        textIndex++
                    }
                    if (typeList[index] == "number") {
                        cartDataMap["details[$data]"] = setRequestBody(inputNumberViews[numberIndex]?.text.toString())
                        numberIndex++
                    }
                    if (typeList[index] == "dropdown") {
                        if (options[spinnerIndex].toString() != "Pilih Opsi...") {
                            cartDataMap["details[$data]"] = setRequestBody(options[spinnerIndex].toString())
                            spinnerIndex++
                        } else {
                            cartDataMap["details[$data]"] = setRequestBody("null")
                        }
                    }
                    if (typeList[index] == "checkbox") {
                        val checkboxItem = StringBuilder()
                        for ((posotion, item) in checkboxOption.withIndex()) {
                            if (checkboxListViews[posotion]?.isChecked!!) {
                                checkboxItem.append(item).append(", ")
                            }
                        }
                        val textBuilder = StringBuilder(checkboxItem)
                        if (textBuilder.isNotEmpty()) {
                            textBuilder.deleteCharAt(checkboxItem.length - 2)
                            cartDataMap["details[$data]"] = setRequestBody(textBuilder.toString())
                        }
                    }
                    if (typeList[index] == "textarea") {
                        cartDataMap["details[$data]"] =
                            setRequestBody(inputTextareaViews[textareaIndex]?.text.toString())
                        textareaIndex++
                    }

                }

                orderDesainRequest()
            } else {
                FancyToast.makeText(
                    this,
                    "Jumlah desain belum dipilih",
                    FancyToast.LENGTH_SHORT,
                    FancyToast.INFO,
                    false
                ).show()
            }
        }

        btn_add_reference.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this@OrderDesignActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this@OrderDesignActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this@OrderDesignActivity, arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    PERMISSION_STORAGE
                )

            } else {
                presenterImage.takePhotoGallery(this@OrderDesignActivity)
            }
        }

        hideDetailOrder()

        toggle_detail_order.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showDetailOrder()
            } else {
                hideDetailOrder()
            }
        }
    }

    private fun hideDetailOrder() {
        tv_concept_design.visibility = View.GONE
        input_design_concept.visibility = View.GONE
        formatted_layout.visibility = View.GONE
        tv_ref_design.visibility = View.GONE
        btn_add_reference.visibility = View.GONE
    }

    private fun showDetailOrder() {
        tv_concept_design.visibility = View.VISIBLE
        input_design_concept.visibility = View.VISIBLE
        formatted_layout.visibility = View.VISIBLE
        tv_ref_design.visibility = View.VISIBLE
        btn_add_reference.visibility = View.VISIBLE
    }

    private fun loadImageFileName(imgFileName: ArrayList<String?>) {
        imgAdapter = ImageRefListAdapter(imgFileName, this@OrderDesignActivity)
        rv_refrence_list.adapter = imgAdapter
    }

    private fun setRequestBody(data: String): RequestBody {
        return data.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    override fun loadFileDoc(fileDoc: FileData?) {

    }

    override fun onDeleteImage(position: Int) {
        imgFileList.removeAt(position)
    }

    override fun loadImage(fileData: MutableList<FileData?>) {

        fileData.forEach {
            imgFileList.add(it)
        }

        rv_refrence_list.visibility = View.VISIBLE

        fileData.forEach {
            imgFileName.add(it?.filename.toString())
        }

        loadImageFileName(imgFileName)
    }

    private fun saveimageBody(file: FileData?, nomor: String): MultipartBody.Part {
        request = file?.file!!.asRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("design_preference[$nomor]", file.filename, request)
    }

    private fun orderDesainRequest() {
        progress_bar.visibility = View.VISIBLE
        shadow.visibility = View.VISIBLE

        for ((nomor, data) in imgFileList.withIndex()) {
            imageBody.add(saveimageBody(data, nomor.toString()))
        }

        cartDataMap["want_designer_id"] = setRequestBody(
            SharedPrefManager
                .getInstance(this@OrderDesignActivity).idDesigner.toString()
        )

        presenterCart.addToCart("Bearer $token", "application/json", cartDataMap, imageBody, fileBody, productPrice)
    }

    override fun showPriceList(priceList: List<PriceList>) {
        priceListDesain.addAll(priceList)
        adapter = PriceListAdapter(priceListDesain as ArrayList<PriceList>, this@OrderDesignActivity)
        rv_price_list.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
    override fun showFieldList(fieldListFormatted: List<FieldListFormatted>) {

        // View Properties
        val marginDp8 = 8f
        val marginDp4 = 4f
        val marginPx8 = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, marginDp8,
            applicationContext.resources.displayMetrics
        ).toInt()
        val marginPx4 = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, marginDp4,
            applicationContext.resources.displayMetrics
        ).toInt()

        val paramsInput = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        paramsInput.setMargins(0, marginPx4, 0, marginPx8)

        val paramsText = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        paramsText.setMargins(0, marginPx8, 0, 0)

        val paddingDp = 10f
        val paddingPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, paddingDp,
            applicationContext.resources.displayMetrics
        ).toInt()

        for ((index, data) in fieldListFormatted.withIndex()) {
            if (data.type?.equals("text")!!) {
                typeList.add(data.type.toString())
                keyList.add(data.key.toString())
                fieldListFormatted[index].required?.let {
                    editTextProperties(
                        paddingPx, paramsInput, paramsText,
                        fieldListFormatted[index].placeholder.toString(),
                        fieldListFormatted[index].name.toString(), textIndex, it
                    )
                }
                textIndex++
            }
            if (data.type?.equals("number")!!) {
                typeList.add(data.type.toString())
                keyList.add(data.key.toString())
                fieldListFormatted[index].required?.let {
                    editNumberProperties(
                        paddingPx, paramsInput, paramsText,
                        fieldListFormatted[index].placeholder.toString(),
                        fieldListFormatted[index].name.toString(), numberIndex, it
                    )
                }
                numberIndex++
            }
            if (data.type?.equals("dropdown")!!) {
                typeList.add(data.type.toString())
                keyList.add(data.key.toString())
                fieldListFormatted[index].options?.forEach {
                    spinnerOption.add(it.toString())
                }

                fieldListFormatted[index].required?.let {
                    spinnerProperties(
                        paramsInput, paramsText, fieldListFormatted[index].name.toString(),
                        spinnerOption, spinnerIndex, it
                    )
                }
                spinnerIndex++
            }
            if (data.type?.equals("checkbox")!!) {
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

                if (fieldListFormatted[index].required!!) {
                    textViews.text = "${fieldListFormatted[index].name.toString()} *"
                } else {
                    textViews.text = fieldListFormatted[index].name.toString()
                }

                fieldListFormatted[index].options?.forEach {
                    checkboxItem.add(it.toString())
                }
                for ((nomor, data2) in fieldListFormatted[index].options?.withIndex()!!) {
                    checkboxProperties(paramsInput, data2.toString(), nomor)
                }
            }
            if (data.type?.equals("textarea")!!) {
                typeList.add(data.type.toString())
                keyList.add(data.key.toString())
                fieldListFormatted[index].required?.let {
                    editTextareaProperties(
                        paddingPx, paramsInput, paramsText,
                        fieldListFormatted[index].placeholder.toString(),
                        fieldListFormatted[index].name.toString(), textareaIndex, it
                    )
                }
                textareaIndex++
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun editTextProperties(
        paddingPx: Int, paramsInput: ViewGroup.LayoutParams,
        paramsText: ViewGroup.LayoutParams, palaceholder: String,
        text: String, index: Int, required: Boolean
    ) {

        // TextView
        textViews = TextView(this@OrderDesignActivity)
        textViews.layoutParams = paramsText
        textViews.textSize = 15.5f
        textViews.setTextColor(Color.parseColor("#000000"))
        textViews.setTypeface(null, Typeface.BOLD)

        if (required) {
            textViews.text = "$text *"
        } else {
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
    private fun editNumberProperties(
        paddingPx: Int, paramsInput: ViewGroup.LayoutParams,
        paramsText: ViewGroup.LayoutParams, palaceholder: String,
        text: String, index: Int, required: Boolean
    ) {

        // TextView
        textViews = TextView(this@OrderDesignActivity)
        textViews.layoutParams = paramsText
        textViews.textSize = 15.5f
        textViews.setTextColor(Color.parseColor("#000000"))
        textViews.setTypeface(null, Typeface.BOLD)

        if (required) {
            textViews.text = "$text *"
        } else {
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
    private fun editTextareaProperties(
        paddingPx: Int, paramsInput: ViewGroup.LayoutParams,
        paramsText: ViewGroup.LayoutParams, palaceholder: String,
        text: String, index: Int, required: Boolean
    ) {

        // TextView
        textViews = TextView(this@OrderDesignActivity)
        textViews.layoutParams = paramsText
        textViews.textSize = 15.5f
        textViews.setTextColor(Color.parseColor("#000000"))
        textViews.setTypeface(null, Typeface.BOLD)

        if (required) {
            textViews.text = "$text *"
        } else {
            textViews.text = text
        }

        editText = EditText(this@OrderDesignActivity)
        inputTextareaViews.add(editText)
        inputTextareaViews[index]?.layoutParams = paramsInput
        inputTextareaViews[index]?.setBackgroundResource(R.drawable.input_layout)
        inputTextareaViews[index]?.textSize = 14.0f
        inputTextareaViews[index]?.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
        inputTextareaViews[index]?.hint = palaceholder
        inputTextareaViews[index]?.isSingleLine = false
        inputTextareaViews[index]?.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION

        formatted_layout.addView(textViews)
        formatted_layout.addView(inputTextareaViews[index])
    }

    @SuppressLint("SetTextI18n")
    private fun spinnerProperties(
        paramsInput: ViewGroup.LayoutParams, paramsText: ViewGroup.LayoutParams,
        text: String, spinnerOption: MutableList<String>, index: Int, required: Boolean
    ) {

        // TextView
        textViews = TextView(this@OrderDesignActivity)
        textViews.layoutParams = paramsText
        textViews.textSize = 15.5f
        textViews.setTextColor(Color.parseColor("#000000"))
        textViews.setTypeface(null, Typeface.BOLD)

        if (required) {
            textViews.text = "$text *"
        } else {
            textViews.text = text
        }

        // Spinner
        spinnerViews = Spinner(this@OrderDesignActivity)
        spinnerList.add(spinnerViews)
        spinnerList[index]?.layoutParams = paramsInput
        spinnerList[index]?.adapter = ArrayAdapter(
            this@OrderDesignActivity,
            android.R.layout.simple_dropdown_item_1line, spinnerOption
        )

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
    private fun checkboxProperties(paramsInput: ViewGroup.LayoutParams, text: String, index: Int) {

        // Checkbox
        checkboxViews = CheckBox(this@OrderDesignActivity)
        checkboxListViews.add(checkboxViews)
        checkboxListViews[index]?.layoutParams = paramsInput

        checkboxListViews[index]?.text = text
        formatted_layout.addView(checkboxListViews[index])
    }

    override fun showDetailProductItem(productItem: ProductDetailItem) {
        tv_design_name.text = productItem.name.toString()
    }

    // Menampilkan PrograssBar saat memuat data
    override fun displayProgress() {
        progress_bar.visibility = View.VISIBLE
    }

    // ProgressBar menghilang setelah data selesai dimuat
    override fun hideProgress() {
        progress_bar.visibility = View.GONE
    }

    override fun hideProgressFile() {
        progress_bar.visibility = View.GONE
        shadow.visibility = View.GONE
    }

    override fun showProgressFile() {
        progress_bar.visibility = View.VISIBLE
        shadow.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    override fun shaowPriceList(priceFormatted: String, desainCount: String, kondisi: Boolean, price: String) {
        this.kondisi = kondisi
        tv_design_count.text = "$desainCount Desain"
        cartDataMap["design_option"] = setRequestBody(desainCount)
        productPrice = setRequestBody(price)
        tv_total_price.text = priceFormatted
    }

    // Menampilkan Progress saat menambahkan produk ke keranjang
    override fun showProgressBar() {
        progress_bar.visibility = View.VISIBLE
        shadow.visibility = View.VISIBLE
    }

    // ProgressBar akan menghilang jika terjadi kesalahan saat menambahkan barang ke reranjang
    override fun hideProgressBar() {
        progress_bar.visibility = View.GONE
        shadow.visibility = View.GONE
    }

    override fun onSuccess() {
        progress_bar.visibility = View.GONE
        shadow.visibility = View.GONE
        val intent = Intent(this@OrderDesignActivity, CartActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
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

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
        presenterCart.onDestroy()
        presenterImage.onDestroy()
    }

    companion object {
        const val PERMISSION_STORAGE = 2
    }
}