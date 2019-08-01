package com.hyperdev.tungguin.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.hyperdev.tungguin.GlideApp
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import com.hyperdev.tungguin.model.orderlandingpage.OrderData
import com.hyperdev.tungguin.model.profile.DataUser
import com.hyperdev.tungguin.network.BaseApiService
import com.hyperdev.tungguin.network.NetworkClient
import com.hyperdev.tungguin.presenter.OrderWithSliderPresenter
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.ui.view.OrderWithSliderView
import kotlinx.android.synthetic.main.activity_order.*
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationSet
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import com.hyperdev.tungguin.network.ConnectivityStatus
import com.hyperdev.tungguin.network.HandleError
import com.shashank.sony.fancytoastlib.FancyToast
import retrofit2.HttpException
import java.net.SocketTimeoutException

class OrderActivity : AppCompatActivity(), OrderWithSliderView.View {

    //Deklarasi Variable
    private lateinit var presenter: OrderWithSliderView.Presenter
    private lateinit var token: String
    private lateinit var baseApiService: BaseApiService
    private var imageList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        token = SharedPrefManager.getInstance(this).token.toString()

        baseApiService = NetworkClient.getClient(this)
            .create(BaseApiService::class.java)

        val scheduler = AppSchedulerProvider()

        presenter = OrderWithSliderPresenter(this, baseApiService, scheduler)

        presenter.getOrderWithSlider("Bearer $token")
        presenter.getUserProfile("Bearer $token")

        tv_search_design.setOnClickListener {
            startActivity(Intent(this, SearchProductActivity::class.java))
        }
    }

    private fun imageWithFade(imageIndex: Int, forever: Boolean) {

        val fadeInDuration = 500
        val timeBetween = 2000
        val fadeOutDuration = 1000

        GlideApp.with(this)
            .load(imageList[imageIndex])
            .into(img_list_fade)

        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator() // add this
        fadeIn.duration = fadeInDuration.toLong()

        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateInterpolator() // and this
        fadeOut.startOffset = (fadeInDuration + timeBetween).toLong()
        fadeOut.duration = fadeOutDuration.toLong()

        val animation = AnimationSet(false) // change to false
        animation.addAnimation(fadeIn)
        animation.addAnimation(fadeOut)
        animation.repeatCount = 1
        img_list_fade.animation = animation

        animation.setAnimationListener(object : AnimationListener {
            override fun onAnimationEnd(animation: Animation) {
                if (imageList.size - 1 > imageIndex) {
                    imageWithFade(imageIndex + 1, forever)
                } else {
                    if (forever) {
                        imageWithFade(0, forever)
                    }
                }
            }

            override fun onAnimationRepeat(animation: Animation) {
                // TODO Auto-generated method stub
            }

            override fun onAnimationStart(animation: Animation) {
                // TODO Auto-generated method stub
            }
        })
    }

    @SuppressLint("SetTextI18n")
    override fun displayProfile(profileItem: DataUser) {
        tv_username.text = "Hai ${profileItem.name.toString()}"
    }

    override fun displayOrderWithSlider(orderItem: List<String>) {
        orderItem.forEach {
            imageList.add(it)
        }
        if(imageList.size > 0){
            imageWithFade(0, true)
        }
    }

    override fun displayOnlineDesigner(designerItem: OrderData) {
        tv_designer_online.text = designerItem.availableDesigner.toString()
    }

    override fun displayProgress() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress_bar.visibility = View.GONE
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
    }
}
