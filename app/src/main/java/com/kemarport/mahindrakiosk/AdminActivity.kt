package com.kemarport.mahindrakiosk

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.kemarport.mahindrakiosk.databinding.ActivityAdminBinding
import com.kemarport.mahindrakiosk.helper.Constants
import com.kemarport.mahindrakiosk.helper.SessionManager
import com.kemarport.mahindrakiosk.helper.Utils
import com.kemarport.mahindrakiosk.login.LoginActivity
import java.util.HashMap

class AdminActivity : AppCompatActivity() {
    lateinit var binding:ActivityAdminBinding
    var btn1Flag = true
    var btn2Flag = false

    private lateinit var session: SessionManager
    private lateinit var userDetails: HashMap<String, Any?>
    private var baseUrl: String? = ""
    private var baseUrlType: Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_admin)
        session = SessionManager(this)
        userDetails = session.getUserDetails()
        baseUrl = userDetails[Constants.SET_BASE_URL].toString()

        baseUrlType = userDetails[Constants.BASE_URL_TYPE] as Boolean
        binding.setBaseUrlSubmitBtn.setOnClickListener {
            checkinputUrl()
        }
        binding.radioGroupUrl.check(binding.radioBtn1.id)

        binding.edField2.setHint("Please set URL With Http/Https://")
        binding.radioGroupUrl.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioBtn1 -> {
                    btn1Flag = true
                    btn2Flag = false
                    if(baseUrlType)
                    {
                        if(baseUrl!=null)
                        {
                            binding.edField2.setText(baseUrl)
                            baseUrl=baseUrl!!.replace("/Service/api/", "")
                        }

                        else
                        {
                            binding.edField2.setHint("Please set URL With Http/Https://")
                            binding.edField2.setText("")
                        }
                    }
                    else
                    {
                        binding.edField2.setHint("Please set URL With Http/Https://")
                        binding.edField2.setText("")
                    }
                }
                R.id.radioBtn2 -> {
                    btn1Flag = false
                    btn2Flag = true
                    if(!baseUrlType) {
                        if (baseUrl != null)
                        {
                            binding.edField2.setText(baseUrl)

                        }

                        else {
                            binding.edField2.setHint("Domain URL www.example.com..")
                            binding.edField2.setText("")
                        }
                    }
                    else
                    {
                        binding.edField2.setHint("Domain URL www.example.com..")
                        binding.edField2.setText("")
                    }
                }
            }
        }
        setDefaultEdText()


    }
    private fun setDefaultEdText(){
        if(btn1Flag  )
        {
            if(baseUrlType)
            {
                if(baseUrl!=null)
                {
                    binding.edField2.setText(baseUrl)
                    baseUrl=baseUrl!!.replace("/Service/api/", "")
                }
                else
                {
                    binding.edField2.setHint("Please set URL With Http/Https://")
                    binding.edField2.setText("")
                }

            }
            else
            {  binding.edField2.setHint("Please set URL With Http/Https://")
                binding.edField2.setText("")
            }

        }
        else if(btn2Flag )
        {
            if(!baseUrlType)
            {
                if(baseUrl!=null)
                    binding.edField2.setText(baseUrl)
                else
                { binding.edField2.setHint("Domain URL www.example.com..")
                    binding.edField2.setText("")
                }

            }
            else
            {
                binding.edField2.setHint("Domain URL www.example.com..")
                binding.edField2.setText("")
            }
        }
    }
    private fun checkinputUrl() {
        val url: String = binding.edField2.getText().toString().trim()
        if (url.isEmpty() || url.equals("")) {
            binding.edField2.setError("Please enter ip address")
        } else {
            if(btn1Flag)
            {
                Utils.setSharedPrefs(this@AdminActivity, Constants.SET_BASE_URL, "$url/Service/api/")
                Utils.setSharedPrefsBoolean(this@AdminActivity, Constants.BASE_URL_TYPE, true)
                showCustomDialogFinish(
                    this@AdminActivity,
                    "Base Url Updated. Changes will take place after Re-Login"
                )
            }
            else if(btn2Flag)
            {
                Utils.setSharedPrefs(this@AdminActivity, Constants.SET_BASE_URL, url)
                Utils.setSharedPrefsBoolean(this@AdminActivity, Constants.BASE_URL_TYPE, false)
                showCustomDialogFinish(
                    this@AdminActivity,
                    "Base Url Updated. Changes will take place after Re-Login"
                )
            }

        }
    }

    fun showCustomDialogFinish(context: Context?, message: String?) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("OK") { dialogInterface, i ->
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
            .show()
    }
}