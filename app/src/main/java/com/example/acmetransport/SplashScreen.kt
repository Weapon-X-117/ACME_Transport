package com.example.acmetransport

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread

class SplashScreen : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashscreen)

        thread {
            Thread.sleep(8000)
            startActivity(Intent(this@SplashScreen,MainActivity::class.java))
        }
    }

}