package com.hyperdev.tungguin.ui.activity

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.util.Linkify
import android.view.View
import android.widget.Toast
import com.hyperdev.tungguin.GlideApp
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.adapter.DesignOrderAdapter
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.detailorder.ItemDesign
import com.hyperdev.tungguin.model.detailorder.DetailOrderData
import com.hyperdev.tungguin.model.profile.DataUser
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.DetailOrderPresenter
import com.hyperdev.tungguin.repository.order.OrderRepositoryImp
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.ui.view.DetailOrderView
import kotlinx.android.synthetic.main.activity_detail_order.*
import kotlinx.android.synthetic.main.customer_information.*
import kotlinx.android.synthetic.main.designer_information.*
import kotlinx.android.synthetic.main.information_order.*
import kotlinx.android.synthetic.main.product_order_information.*
import kotlinx.android.synthetic.main.testimoni_designer.*
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

        orderId = intent?.getStringExtra("sendOrderID").toString()
        token = SharedPrefManager.getInstance(this@DetailOrderActivity).token.toString()

        baseApiService = NetworkClient.getClient(this@DetailOrderActivity)!!
            .create(BaseApiService::class.java)

        val repository = OrderRepositoryImp(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = DetailOrderPresenter(this@DetailOrderActivity, this@DetailOrderActivity, repository, scheduler)

        val layout = LinearLayoutManager(this@DetailOrderActivity)
        rv_list_product_order.layoutManager = layout
        rv_list_product_order.setHasFixedSize(false)
        rv_list_product_order.isNestedScrollingEnabled = false

        presenter.getUserProfile("Bearer $token")
        presenter.getDetailOrder("Bearer $token", orderId)

        adapter = DesignOrderAdapter(listDesignItem as ArrayList<ItemDesign>)

        rv_list_product_order.adapter = adapter

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
    override fun showDetailOrder(data: DetailOrderData?) {

        val getDesignerName = data?.designer?.name.toString()
        val getDesignerKode = "Kode: ${data?.designer?.formattedId.toString()}"
        val photoUrl = data?.designer?.photoUrl.toString()
        val getIdOrder = data?.formattedId.toString()
        val getPdfUrl = data?.pdfUrl.toString()
        val internalDesigner = data?.designer?.isInternal
        val phoneNumner = data?.designer?.phoneNumber.toString()
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

                tv_designer_name.visibility = View.VISIBLE
                tv_designer_name.text = getDesignerName
                tv_id_designer.visibility = View.VISIBLE
                tv_id_designer.text = getDesignerKode
                img_designer.visibility = View.VISIBLE

                if(internalDesigner == true){
                    val numberWithCountry = phoneNumner.replaceFirst("0", "62")
                    btn_contact_designer.visibility = View.VISIBLE
                    btn_contact_designer.setOnClickListener {
                        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$numberWithCountry")
                        startActivity(Intent(Intent.ACTION_VIEW, uri))
                    }
                }

                GlideApp.with(this@DetailOrderActivity)
                    .load(photoUrl)
                    .placeholder(R.drawable.circle_profil)
                    .into(img_designer)

                order_id.visibility = View.VISIBLE
                order_id.text = getIdOrder

                tv_order_information.visibility = View.GONE
                btn_payment_instructions.visibility = View.GONE
                btn_chat.visibility = View.VISIBLE
                btn_rating.visibility = View.GONE

                status_order.setBackgroundResource(R.drawable.order_round_no_hover)

            }

            "pending" -> {

                tv_order_information.visibility = View.VISIBLE
                btn_payment_instructions.visibility = View.VISIBLE
                btn_payment_instructions.setOnClickListener {
                    try {
                        val pdfUrl = Uri.parse(getPdfUrl)
                        val intent = Intent(Intent.ACTION_VIEW, pdfUrl)
                        startActivity(intent)
                    } catch (ex: ActivityNotFoundException) {
                        Toast.makeText(this, "Tidak ada aplikasi yang dapat menangani permintaan ini. " +
                                "Silakan instal browser web", Toast.LENGTH_LONG).show()
                        ex.printStackTrace()
                    }
                }

                status_order.setBackgroundResource(R.drawable.orange_round_no_hover)

            }

            "expired" -> {

                tv_designer_name.visibility = View.GONE
                tv_id_designer.visibility = View.GONE
                img_designer.visibility = View.GONE

                tv_order_information.visibility = View.VISIBLE
                btn_payment_instructions.visibility = View.GONE

                status_order.setBackgroundResource(R.drawable.red_round_no_hover)

            }

            "success" -> {

                if (data?.result?.isNotEmpty() == true) {
                    card_result_design.visibility = View.VISIBLE
                    val fileUrl = data.result[0].pathUrl.toString()
                    tv_download_file.text = fileUrl
                    Linkify.addLinks(tv_download_file, Linkify.WEB_URLS)
                }

                status_order.setBackgroundResource(R.drawable.order_round_no_hover)

                btn_contact_designer.visibility = View.GONE
                tv_designer_name.visibility = View.VISIBLE
                tv_designer_name.text = getDesignerName
                tv_id_designer.visibility = View.VISIBLE
                tv_id_designer.text = getDesignerKode
                img_designer.visibility = View.VISIBLE

                GlideApp.with(this@DetailOrderActivity)
                    .load(photoUrl)
                    .placeholder(R.drawable.circle_profil)
                    .into(img_designer)

                order_id.visibility = View.VISIBLE
                order_id.text = getIdOrder

                tv_order_information.visibility = View.GONE
                btn_payment_instructions.visibility = View.GONE

                if (data?.testimonial?.orderId != null) {
                    btn_rating.visibility = View.GONE
                    testimoni_designer.visibility = View.VISIBLE
                    rating_bar.rating = data.testimonial!!.starRating!!.toFloat()
                    tv_testi_designer.text = data.testimonial!!.designerTestimonial.toString()
                    tv_testi_app.text = data.testimonial!!.appTestimonial.toString()
                    tv_tip_designer.text = data.testimonial!!.designerTipFormatted.toString()
                } else {
                    btn_rating.visibility = View.VISIBLE
                    testimoni_designer.visibility = View.GONE
                }

                btn_repeat_order.visibility = View.VISIBLE
                btn_repeat_order.setOnClickListener {
                    val getIdDesigner = data?.designer?.hashedId.toString()
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

    override fun displayProfile(profileItem: DataUser) {
        username.text = profileItem.name.toString()
        email_user.text = profileItem.email.toString()
        phone_nomor.text = profileItem.phoneNumber.toString()
    }

    override fun displayProgress() {
        swipe_refresh.isRefreshing = true
    }

    override fun hideProgress() {
        swipe_refresh.isRefreshing = false
    }

    override fun onSuccess() {
        swipe_refresh.isRefreshing = false
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