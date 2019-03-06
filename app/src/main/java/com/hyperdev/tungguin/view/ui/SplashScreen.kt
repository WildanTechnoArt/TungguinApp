package com.hyperdev.tungguin.view.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.hyperdev.tungguin.R

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //Membuat fungsi thread
        val thread = object : Thread(){
            override fun run() {
                try{
                    //Pending selama 2 detik
                    sleep(1500)
                }catch (ex: InterruptedException){
                    ex.printStackTrace()
                }finally {
                    //Setelah 2 detik berakhir maka akan masuk pada halaman Login
                    startActivity(Intent(this@SplashScreen, MainPage::class.java))
                    finish()
                }
            }
        }
        //Menjalankan thread
        thread.start()
    }
}
