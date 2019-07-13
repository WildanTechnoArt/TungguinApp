package com.hyperdev.tungguin.ui.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hyperdev.tungguin.R

class SplashScreen : AppCompatActivity() {

    private lateinit var getTypeAction: String
    private lateinit var getTypeId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //Membuat fungsi thread
        val thread = object : Thread() {
            override fun run() {
                try {
                    //Pending selama 2 detik
                    sleep(1500)
                } catch (ex: InterruptedException) {
                    ex.printStackTrace()
                } finally {

                    if (intent.extras != null) {
                        getTypeAction = intent?.extras?.get("type").toString()
                        getTypeId = intent?.extras?.get("type_id").toString()
                        when (getTypeAction) {
                            "top_up_success" -> {
                                startActivity(Intent(this@SplashScreen, HistoryActivity::class.java))
                                finish()
                            }
                            "top_up_expired" -> {
                                startActivity(Intent(this@SplashScreen, HistoryActivity::class.java))
                                finish()
                            }
                            "order_finish" -> {
                                val intent = Intent(this@SplashScreen, TestimoniActivity::class.java)
                                intent.putExtra("sendOrderID", getTypeId)
                                startActivity(intent)
                                finish()
                            }
                            "payment_order_success" -> {
                                val intent = Intent(this@SplashScreen, DetailOrderActivity::class.java)
                                intent.putExtra("sendOrderID", getTypeId)
                                startActivity(intent)
                                finish()
                            }
                            "payment_order_expired" -> {
                                val intent = Intent(this@SplashScreen, DetailOrderActivity::class.java)
                                intent.putExtra("sendOrderID", getTypeId)
                                startActivity(intent)
                                finish()
                            }
                            "new_order_message" -> {
                                val intent = Intent(this@SplashScreen, ChatActivity::class.java)
                                intent.putExtra("sendOrderID", getTypeId)
                                startActivity(intent)
                                finish()
                            }
                            "designerFound" -> {
                                val intent = Intent(this@SplashScreen, DetailOrderActivity::class.java)
                                intent.putExtra("sendOrderID", getTypeId)
                                startActivity(intent)
                                finish()
                            }
                            else -> {
                                startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                                finish()
                            }
                        }
                    } else {
                        startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                        finish()
                    }
                }
            }
        }
        //Menjalankan thread
        thread.start()
    }
}
