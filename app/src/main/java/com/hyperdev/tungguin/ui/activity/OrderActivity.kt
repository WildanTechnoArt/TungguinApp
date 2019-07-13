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
import com.hyperdev.tungguin.repository.order.OrderRepositoryImp
import com.hyperdev.tungguin.repository.profile.ProfileRepositoryImpl
import com.hyperdev.tungguin.utils.AppSchedulerProvider
import com.hyperdev.tungguin.ui.view.OrderWithSliderView
import kotlinx.android.synthetic.main.activity_order.*
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationSet
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator

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

        token = SharedPrefManager.getInstance(this@OrderActivity).token.toString()

        baseApiService = NetworkClient.getClient(this@OrderActivity)!!
            .create(BaseApiService::class.java)

        val request = OrderRepositoryImp(baseApiService)
        val request2 = ProfileRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()

        presenter = OrderWithSliderPresenter(this, this@OrderActivity, request, request2, scheduler)

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

        GlideApp.with(this@OrderActivity)
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
        intoMyName.text = "Hai ${profileItem.name.toString()}"
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
        designer_count.text = designerItem.availableDesigner.toString()
    }

    override fun displayProgress() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress_bar.visibility = View.GONE
    }
}
