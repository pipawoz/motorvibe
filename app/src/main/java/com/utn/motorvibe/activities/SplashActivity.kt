package com.utn.motorvibe.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.utn.motorvibe.R
import com.utn.motorvibe.activities.LoginActivity

class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT : Long = 3000  //3 secs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.splash_fragment)

        Handler().postDelayed(
            {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }, SPLASH_TIME_OUT)
    }

}