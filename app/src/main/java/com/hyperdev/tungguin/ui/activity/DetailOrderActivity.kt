package com.hyperdev.tungguin.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.util.Linkify
import android.view.View
import android.widget.Toast
import com.hyperdev.tungguin.BuildConfig
import com.hyperdev.tungguin.GlideApp
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.adapter.DesignOrderAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.detailorder.ItemDesign
import com.hyperdev.tungguin.model.detailorder.OrderDetailItem
import com.hyperdev.tungguin.model.profile.DataUser
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.DetailOrderPresenter
import com.hyperdev.tungguin.repository.order.OrderRepositoryImp
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.ui.view.DetailOrderView
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import kotlinx.android.synthetic.main.activity_detail_order.*
import kotlin.properties.Delegates

class DetailOrderActivity : AppCompatActivity(), DetailOrderView.View {

    // Deklarasi Variable
    private lateinit var orderId: String
    private lateinit var baseApiService: BaseApiService
    private lateinit var presenter: DetailOrderView.Presenter
    private lateinit var token: String
    private var listDesignItem: MutableList<ItemDesign> = mutableListOf()
    private var adapter by Delegates.notNull<DesignOrderAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_order)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        orderId = intent.getStringExtra("sendOrderID").toString()
        token = SharedPrefManager.getInstance(this@DetailOrderActivity).token.toString()

        baseApiService = NetworkClient.getClient(this@DetailOrderActivity)!!
            .create(BaseApiService::class.java)

        val repository = OrderRepositoryImp(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = DetailOrderPresenter(this@DetailOrderActivity, this@DetailOrderActivity, repository, scheduler)

        val layout = LinearLayoutManager(this@DetailOrderActivity)
        list_product_buy.layoutManager = layout
        list_product_buy.setHasFixedSize(false)
        list_product_buy.isNestedScrollingEnabled = false

        presenter.getUserProfile("Bearer $token")
        presenter.getDetailOrder("Bearer $token", orderId)

        adapter = DesignOrderAdapter(listDesignItem as ArrayList<ItemDesign>)

        list_product_buy.adapter = adapter

        midtransInitialotation()

        swipe_refresh.setOnRefreshListener {
            presenter.getDetailOrder("Bearer $token", orderId)
            presenter.getUserProfile("Bearer $token")
        }

        btn_chat.setOnClickListener {
            val intent = Intent(this@DetailOrderActivity, ChatActivity::class.java)
            intent.putExtra("sendOrderID", orderId)
            startActivity(intent)
        }

        btn_rating.setOnClickListener {
            val intent = Intent(this@DetailOrderActivity, TestimoniActivity::class.java)
            intent.putExtra("sendOrderID", orderId)
            startActivity(intent)
        }
    }

    override fun showDesignItem(orderList: List<ItemDesign>) {
        listDesignItem.clear()
        listDesignItem.addAll(orderList)
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        presenter.getDetailOrder("Bearer $token", orderId)
        presenter.getUserProfile("Bearer $token")
    }

    @SuppressLint("SetTextI18n")
    override fun showDetailOrder(data: OrderDetailItem?) {

        val getDesignerName = data?.designer?.name.toString()
        val getDesignerKode = "Kode: ${data?.designer?.formattedId.toString()}"
        val photoUrl = data?.designer?.photoUrl.toString()
        val getIdOrder = data?.formattedId.toString()
        val getMidtransToken = data?.midtransToken.toString()
        val getStatusDesign = data?.statusFormatted?.status.toString()
        val getDateExpired = data?.expireDateFormatted.toString()

        when (getStatusDesign) {
            "searching_designer" -> {

                val intent = Intent(this@DetailOrderActivity, SearchDesignerActivity::class.java)
                intent.putExtra("sendOrderID", orderId)
                startActivity(intent)
                finish()

            }
            "in_progress" -> {

                tv_name_designer.visibility = View.VISIBLE
                tv_name_designer.text = getDesignerName
                id_designer.visibility = View.VISIBLE
                id_designer.text = getDesignerKode
                profile_image.visibility = View.VISIBLE
                GlideApp.with(this@DetailOrderActivity)
                    .load(photoUrl)
                    .placeholder(R.drawable.circle_profil)
                    .into(profile_image)
                id_order.visibility = View.VISIBLE
                id_order.text = getIdOrder

                txt_no_desainer.visibility = View.GONE
                btn_bayar.visibility = View.GONE
                btn_layout.visibility = View.VISIBLE
                btn_chat.visibility = View.VISIBLE
                btn_rating.visibility = View.GONE

                status_order.setBackgroundResource(R.drawable.order_round_no_hover)

            }
            "pending" -> {

                tv_name_designer.visibility = View.GONE
                id_designer.visibility = View.GONE
                profile_image.visibility = View.GONE
                id_order.visibility = View.GONE

                txt_no_desainer.visibility = View.VISIBLE
                btn_bayar.visibility = View.VISIBLE
                btn_layout.visibility = View.GONE
                btn_bayar.setOnClickListener {
                    MidtransSDK.getInstance().startPaymentUiFlow(this@DetailOrderActivity, getMidtransToken)
                }

                status_order.setBackgroundResource(R.drawable.orange_round_no_hover)

            }
            "expired" -> {

                tv_name_designer.visibility = View.GONE
                id_designer.visibility = View.GONE
                profile_image.visibility = View.GONE

                txt_no_desainer.visibility = View.VISIBLE
                btn_bayar.visibility = View.GONE
                btn_layout.visibility = View.GONE

                status_order.setBackgroundResource(R.drawable.red_round_no_hover)

            }
            "success" -> {

                if (data?.result?.isNotEmpty()!!) {
                    card_result_design.visibility = View.VISIBLE
                    val fileUrl = data.result!![0].pathUrl.toString()
                    link_download_file.text = fileUrl
                    Linkify.addLinks(link_download_file, Linkify.WEB_URLS)
                }

                status_order.setBackgroundResource(R.drawable.order_round_no_hover)

                tv_name_designer.visibility = View.VISIBLE
                tv_name_designer.text = getDesignerName
                id_designer.visibility = View.VISIBLE
                id_designer.text = getDesignerKode
                profile_image.visibility = View.VISIBLE
                GlideApp.with(this@DetailOrderActivity)
                    .load(photoUrl)
                    .placeholder(R.drawable.circle_profil)
                    .into(profile_image)
                id_order.visibility = View.VISIBLE
                id_order.text = getIdOrder

                txt_no_desainer.visibility = View.GONE
                btn_bayar.visibility = View.GONE
                btn_layout.visibility = View.VISIBLE
                btn_chat.visibility = View.VISIBLE

                if (data.testimonial?.orderId != null) {
                    btn_rating.visibility = View.GONE
                    card_testimoni.visibility = View.VISIBLE
                    rating.rating = data.testimonial!!.starRating!!.toFloat()
                    testi_designer.text = data.testimonial!!.designerTestimonial.toString()
                    testi_app.text = data.testimonial!!.appTestimonial.toString()
                    tip_designer.text = data.testimonial!!.designerTipFormatted.toString()
                } else {
                    btn_rating.visibility = View.VISIBLE
                    card_testimoni.visibility = View.GONE
                }

                btn_repeat_order.visibility = View.VISIBLE
                btn_repeat_order.setOnClickListener {
                    val getIdDesigner = data.designer?.hashedId.toString()
                    Toast.makeText(
                        this@DetailOrderActivity,
                        "Designer telah dipilih, silakan order kembali",
                        Toast.LENGTH_LONG
                    ).show()
                    SharedPrefManager.getInstance(this@DetailOrderActivity).saveDesigner(getIdDesigner)
                    startActivity(Intent(this@DetailOrderActivity, DashboardActivity::class.java))
                    finish()
                }
            }
        }

        date_order.text = data?.formattedDate.toString()

        if (getDateExpired != "null") {
            date_expire.text = getDateExpired
        } else {
            date_expire.text = "-"
        }

        status_order.text = data?.statusFormatted?.label.toString()
        total_harga.text = data?.realTotalFormatted.toString()
    }

    private fun midtransInitialotation() {
        SdkUIFlowBuilder.init()
            .setClientKey(BuildConfig.MIDTRANS_CLIENTID)
            .setContext(this@DetailOrderActivity)
            .setTransactionFinishedCallback {
                if (it.response != null) {
                    when (it.status) {
                        TransactionResult.STATUS_SUCCESS -> {
                            Toast.makeText(
                                this@DetailOrderActivity,
                                "Transaction Finished ",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        TransactionResult.STATUS_PENDING -> {
                        }
                        TransactionResult.STATUS_FAILED -> Toast.makeText(
                            this@DetailOrderActivity,
                            "Transaction Failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    it.response.validationMessages
                } else if (it.isTransactionCanceled) {
                    Toast.makeText(this@DetailOrderActivity, "Transaction Canceled", Toast.LENGTH_LONG).show()
                } else {
                    if (it.status.equals(TransactionResult.STATUS_INVALID, ignoreCase = true)) {
                        Toast.makeText(this@DetailOrderActivity, "Transaction Invalid", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(
                            this@DetailOrderActivity,
                            "Transaction Finished with failure.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            .setMerchantBaseUrl(BuildConfig.BASE_URL) //set merchant url (required)
            .enableLog(true) // enable sdk log (optional)
            .setColorTheme(
                CustomColorTheme(
                    "#FFE51255",
                    "#B61548",
                    "#FFE51255"
                )
            ) // set theme. it will replace theme on snap theme on MAP ( optional)
            .buildSDK()
    }

    override fun displayProfile(profileItem: DataUser) {
        username.text = profileItem.name.toString()
        email_user.text = profileItem.email.toString()
        phone_nomor.text = profileItem.phoneNumber.toString()
    }

    override fun displayProgress() {
        swipe_refresh.isRefreshing = true
        detail_order_scrollview.visibility = View.GONE
    }

    override fun hideProgress() {
        swipe_refresh.isRefreshing = false
    }

    override fun onSuccess() {
        swipe_refresh.isRefreshing = false
        detail_order_scrollview.visibility = View.VISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}