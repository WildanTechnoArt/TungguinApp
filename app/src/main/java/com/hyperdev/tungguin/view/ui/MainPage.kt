package com.hyperdev.tungguin.view.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.hyperdev.tungguin.R
import com.hyperdev.tungguin.database.SharedPrefManager
import kotlinx.android.synthetic.main.activity_main_page.*

class MainPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        btnLogin.setOnClickListener {
            startActivity(Intent(this@MainPage, LoginPage::class.java))
        }
        btnRegister.setOnClickListener {
            startActivity(Intent(this@MainPage, RegisterPage::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        val getUserToken = SharedPrefManager.getInstance(this@MainPage).token
        if(getUserToken != null){
            startActivity(Intent(this@MainPage, Dashboard::class.java))
            finish()
        }
    }
}
