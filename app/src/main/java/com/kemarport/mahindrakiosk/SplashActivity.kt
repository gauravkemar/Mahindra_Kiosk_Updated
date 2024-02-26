package com.kemarport.mahindrakiosk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatDelegate
import com.kemarport.mahindrakiosk.helper.Constants
import com.kemarport.mahindrakiosk.helper.Utils
import com.kemarport.mahindrakiosk.home.KioskHomeFlippinActivity
import com.kemarport.mahindrakiosk.login.LoginActivity

class SplashActivity : AppCompatActivity() {
    private val SPLASH_SCREEN_TIME_OUT = 1000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Handler().postDelayed({
            if (Utils.getSharedPrefsBoolean(this@SplashActivity, Constants.KEY_ISLOGGEDIN, false)) {
                if(Utils.getSharedPrefsBoolean(this@SplashActivity, Constants.KEY_ISLOGGEDIN, true)) {
                    startActivity(Intent(this@SplashActivity, KioskHomeFlippinActivity::class.java))
                    finish()
                }
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }
        }, SPLASH_SCREEN_TIME_OUT)
    }
}
