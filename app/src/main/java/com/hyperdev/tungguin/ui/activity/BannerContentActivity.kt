package com.hyperdev.tungguin.ui.activity

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.*
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.utils.UtilsConstant.Companion.BANNER_NAME
import com.hyperdev.tungguin.utils.UtilsConstant.Companion.BANNER_URL
import kotlinx.android.synthetic.main.activity_banner_content.*

class BannerContentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner_content)

        val bannerTitle = intent?.getStringExtra(BANNER_NAME).toString()

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = bannerTitle
            setDisplayShowHomeEnabled(true)
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        swipe_refresh.setOnRefreshListener {
            loadUrl()
        }

        settings()
        loadUrl()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun settings(){
        val settings = banner_content.settings
        settings.javaScriptEnabled = true
        settings.allowContentAccess = true
        settings.useWideViewPort = true
        settings.loadsImagesAutomatically = true
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.domStorageEnabled = true
    }

    private fun loadUrl(){
        banner_content.webChromeClient = object: WebChromeClient(){
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                swipe_refresh.isRefreshing = true
                super.onProgressChanged(view, newProgress)
            }
        }
        banner_content.webViewClient = object: WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, URL: String?): Boolean {
                view?.loadUrl(URL)
                swipe_refresh.isRefreshing = true
                return true
            }
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view?.loadUrl(request?.url.toString())
                }
                swipe_refresh.isRefreshing = true
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                swipe_refresh.isRefreshing = false
            }
        }

        banner_content.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        val bannerUrl = intent?.getStringExtra(BANNER_URL).toString()
        banner_content.loadUrl(bannerUrl)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if(banner_content.canGoBack()){
            banner_content.goBack()
        }else{
            super.onBackPressed()
        }
    }
}