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

        btnLogin.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
        btnRegister.setOnClickListener {
            startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        val getUserToken = SharedPrefManager.getInstance(this@MainActivity).token
        if (getUserToken != null) {
            startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
            finish()
        }
    }
}
