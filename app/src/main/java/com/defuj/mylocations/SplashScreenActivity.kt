package com.defuj.mylocations

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        object : CountDownTimer(3000,1000){
            override fun onFinish() {
                startActivity(Intent(this@SplashScreenActivity,MainActivity::class.java))
            }

            override fun onTick(millisUntilFinished: Long) {

            }
        }.start()
    }
}
