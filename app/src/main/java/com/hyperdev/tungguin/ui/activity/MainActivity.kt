package com.hyperdev.tungguin.ui.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import kotlinx.android.synthetic.main.activity_main_page.*
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        btn_login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        btn_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        privacy_policy.loadUrl("file:///android_asset/privacy_policy.html")
    }

    override fun onStart() {
        super.onStart()
        val getUserToken = SharedPrefManager.getInstance(this).token
        if (getUserToken != null) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }
}
