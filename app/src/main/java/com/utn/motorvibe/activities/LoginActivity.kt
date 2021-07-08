package com.utn.motorvibe.activities

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.utn.motorvibe.R

class LoginActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 3000  //3 secs
    private val LOGGED_USER = "false"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_fragment)

        //val sharedPref : SharedPreferences = getSharedPreferences(LOGGED_USER, MODE_PRIVATE)

        Handler().postDelayed(
            {

                setContentView(R.layout.activity_login)
                //startActivity(Intent(this, MainActivity::class.java))
                //finish()
            }, SPLASH_TIME_OUT
        )
    }

}