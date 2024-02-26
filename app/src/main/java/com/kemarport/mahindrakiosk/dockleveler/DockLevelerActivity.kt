package com.kemarport.mahindrakiosk.dockleveler

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kemarport.mahindrakiosk.R
import com.kemarport.mahindrakiosk.databinding.ActivityDockLevelerBinding
import com.kemarport.mahindrakiosk.databinding.CustomDialogRfidTcpBinding
import com.kemarport.mahindrakiosk.databinding.DockLevelerManualSupervisorCheckBinding
import com.kemarport.mahindrakiosk.dockleveler.adapter.DockLevelerAdapter
import com.kemarport.mahindrakiosk.dockleveler.model.CurrentDockLevelerResponse
import com.kemarport.mahindrakiosk.dockleveler.model.CurrentDockLevelerUpdate
import com.kemarport.mahindrakiosk.dockleveler.viewmodel.DockLevelerViewModel
import com.kemarport.mahindrakiosk.dockleveler.viewmodel.DockLevelerViewModelFactory
import com.kemarport.mahindrakiosk.helper.ApplicationClass
import com.kemarport.mahindrakiosk.helper.Constants
import com.kemarport.mahindrakiosk.helper.Resource
import com.kemarport.mahindrakiosk.helper.SessionManager
import com.kemarport.mahindrakiosk.home.KioskHomeFlippinActivity
import com.kemarport.mahindrakiosk.repository.KioskRepository
import kotlinx.coroutines.*
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.*

class DockLevelerActivity : AppCompatActivity() {
    private lateinit var progress: ProgressDialog


    ///Databinding for the view with the viewmodel
    lateinit var binding: ActivityDockLevelerBinding
    private lateinit var viewModel: DockLevelerViewModel
    private var dockListRecycler: RecyclerView? = null
    private var dockRecyclerAdapter: DockLevelerAdapter? = null
    lateinit var homeDockArrayModel: ArrayList<CurrentDockLevelerResponse>
    lateinit var updateDockLevelerUpdate: ArrayList<CurrentDockLevelerUpdate>

    //custom dialog (not is use)
    var tcpSettingDialog: Dialog? = null
    lateinit var customDialogRfidTcpBinding: CustomDialogRfidTcpBinding

    //get values from session
    private lateinit var userDetails: HashMap<String, Any?>
    private lateinit var session: SessionManager
    private var userRfid: String? = ""
    private var shopNo: String? = ""
    private var dockNo: String? = ""
    private var token: String? = ""
    private var userName: String? = ""

    //used for data time
    var coroutineScopeForCurrentTime = CoroutineScope(Dispatchers.Main)

    //working code for the Ethernet RFID Reader
    private val applicationApp: ApplicationClass by lazy { application as ApplicationClass }
    var flag = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dock_leveler)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //Initializing variables
        dockListRecycler = binding!!.dockQueueRc
        tcpSettingDialog = Dialog(this)
        progress = ProgressDialog(this)
        progress.setMessage("Loading...")
        session = SessionManager(this)
        customDialogRfidTcpBinding = CustomDialogRfidTcpBinding.inflate(getLayoutInflater());
        userDetails = session.getUserDetails()
        userRfid = userDetails[Constants.USER_RFID].toString()
        shopNo = userDetails[Constants.PARENT_LOCATION_CODE].toString()
        dockNo = userDetails[Constants.LOCATION_NAME].toString()
        token = userDetails[Constants.KEY_JWT_TOKEN].toString()
        userName = userDetails[Constants.KEY_USER_NAME].toString()
        binding.tvSupervisorName.setText(userName)
        binding.tvDockNo.setText(dockNo)
        binding.tvShopNo.setText(shopNo)
        startCurrentDateTime()
        val kioskRepository = KioskRepository()
        val viewModelProviderFactory =
            DockLevelerViewModelFactory(application, kioskRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[DockLevelerViewModel::class.java]
        homeDockArrayModel = ArrayList()
        updateDockLevelerUpdate = ArrayList()
        binding.logoutBtn.setOnClickListener {
            session.showAlertLogOut(this@DockLevelerActivity)
        }
        binding.cancelBtn.setOnClickListener {
            startMainActivity()
        }
        getListOfDockLeveler()

        viewModel.getDockLevelerMutableLiveData.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    //hideProgressBar()
                    response.data?.let { resultResponse ->
                        if (resultResponse.size > 0) {
                            //sets the recyclerview with items from the api response
                            homeDockArrayModel.clear()
                            homeDockArrayModel.addAll(resultResponse)
                            setRCDockLeveler(homeDockArrayModel)

                            applicationApp.receivedData.observe(this) { data ->
                                if (data != null) {
                                    println("Received data in DockLeveler: $data")
                                    verifySupervisorId(data)
                                }
                            }
                        }

                    }
                }
                is Resource.Error -> {
                    // hideProgressBar()
                    response.message?.let { resultResponse ->
                        //Toasty.error(this, resultResponse, Toasty.LENGTH_SHORT).show()
                        //prints the error
                        session.sweetAlertFailure(this, resultResponse)

                        //Checks if the session has expired
                        if (resultResponse == "Unauthorized" || resultResponse == "Authentication token expired" ||
                            resultResponse == Constants.CONFIG_ERROR
                        ) {
                            session.showCustomDialog(
                                "Session Expired",
                                "Please re-login to continue",
                                this@DockLevelerActivity
                            )
                        }
                    }
                }
                is Resource.Loading -> {
                    // showProgressBar()
                }
                else -> {
                    //
                }
            }

        }
        viewModel.updateCurrentDockLevelerMutableLiveData.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { resultResponse ->
                        // Toasty.success(this, resultResponse.responseMessage, Toasty.LENGTH_SHORT).show()

                        //Navigate to mainActivity After updating the levelers
                        session.sweetAlertSuccessNavigateToMainActivity(
                            this,
                            resultResponse.responseMessage
                        )
                        //startMainActivity()
                    }
                }
                is Resource.Error -> {
                    //hideProgressBar()
                    response.message?.let { resultResponse ->
                        //Toasty.error(this, resultResponse, Toasty.LENGTH_SHORT).show()
                        session.sweetAlertFailure(this, resultResponse)
                        if (resultResponse == "Unauthorized" || resultResponse == "Authentication token expired" ||
                            resultResponse == Constants.CONFIG_ERROR
                        ) {
                            session.showCustomDialog(
                                "Session Expired",
                                "Please re-login to continue",
                                this@DockLevelerActivity
                            )
                        }
                    }
                }
                is Resource.Loading -> {
                    // showProgressBar()
                }
                else -> {
                    //
                }
            }

        }
    }
    private fun verifySupervisorId(data: String) {
        val color = Color.parseColor("#00a651") // Replace with your desired color
        val grey = Color.parseColor("#80D9D9D9") // Replace with your desired color
        if (flag) {
            if (data != null && userRfid != null) {
                if (data.equals(userRfid, ignoreCase = true)) {
                    binding.imgSupervisor.setImageResource(R.drawable.right_icon)
                    ViewCompat.setBackgroundTintList(
                        binding.submitBtn,
                        ColorStateList.valueOf(color)
                    )
                    binding.submitBtn.isEnabled = true

                } else {
                    //Toasty.error(this,"Supervisor Id Mismatch", Toasty.LENGTH_SHORT).show()
                    //1 session.sweetAlertFailure(this,"Supervisor Id Mismatch")

                    session.sweetAlertFailure(this@DockLevelerActivity, "Supervisor Id Mismatch")
                    ViewCompat.setBackgroundTintList(
                        binding.submitBtn,
                        ColorStateList.valueOf(grey)
                    )
                    binding.submitBtn.isEnabled = false
                }
            }
        } else {
            session.sweetAlertFailure(this@DockLevelerActivity, "Please perform action")
        }

    }

    private fun setRCDockLeveler(dockLevelerArrayModel: ArrayList<CurrentDockLevelerResponse>) {
        dockRecyclerAdapter = DockLevelerAdapter()
        dockRecyclerAdapter?.setDockLevelerList(dockLevelerArrayModel, this@DockLevelerActivity)
        dockListRecycler!!.adapter = dockRecyclerAdapter
        binding.dockQueueRc.layoutManager = LinearLayoutManager(this)

        //checks if there is any changes in the Radio button and then only allow to Submit
        dockRecyclerAdapter?.onItemStateChanged = { position, isActive ->
            homeDockArrayModel[position].isActive = isActive
            val postList = ArrayList(
                homeDockArrayModel.map { dock ->
                    CurrentDockLevelerUpdate(dock.isActive, dock.levelerId)
                }
            )
            flag = true
            val drawableInImageView = binding.imgSupervisor.drawable
            val desiredDrawableResource = R.drawable.supervisorid

            if (drawableInImageView != null && drawableInImageView.constantState == resources.getDrawable(
                    desiredDrawableResource
                )?.constantState
            ) {
                binding.submitBtn.setOnClickListener {
                    updateLevelerStatus(postList)
                }
            } else {
                //println("The drawable in the ImageView does not match the desired drawable.")
                // session.sweetAlertFailure(this,"Supervisor Id Mismatch!!")
            }

        }
    }

    private fun getListOfDockLeveler() {
        //API get list of levelers
        try {
            viewModel.getDockLevelerList(
                token!!,
                Constants.baseUrl2,
                getDeviceIPAddress() ?: "0.0.0.0"
            )
        } catch (e: Exception) {
            //Toasty.error(this, e.printStackTrace().toString(), Toasty.LENGTH_SHORT).show()
            session.sweetAlertFailure(this, e.printStackTrace().toString())
        }
    }

    //Update Api Call
    private fun updateLevelerStatus(postList: ArrayList<CurrentDockLevelerUpdate>) {
        try {
            Log.d("DockLevelerList", postList.toString())
            viewModel.updateCurrentDockLeveler(
                token!!,
                Constants.baseUrl2,
                postList

            )
        } catch (e: Exception) {
            //Toasty.error(this, e.printStackTrace().toString(), Toasty.LENGTH_SHORT).show()
            session.sweetAlertFailure(this, e.printStackTrace().toString())
        }
    }


    override fun onPause() {
        super.onPause()
        applicationApp.clearReceivedData()
        coroutineScopeForCurrentTime.cancel()
    }

    override fun onStop() {
        super.onStop()
        coroutineScopeForCurrentTime.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        applicationApp.clearReceivedData()
        coroutineScopeForCurrentTime.cancel()
    }

    //gets the ip address of the device
    fun getDeviceIPAddress(): String? {
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address.isSiteLocalAddress) {
                        return address.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startMainActivity()
    }

    private fun startMainActivity() {
        startActivity(Intent(this@DockLevelerActivity, KioskHomeFlippinActivity::class.java))
        finish()
    }

    //Displays the current date and time at Top of the page
    private fun startCurrentDateTime() {
        coroutineScopeForCurrentTime.launch {
            try {
                while (isActive) {
                    if (isActive) {
                        binding.tvDateTime.setText(getCurrentDateTime())
                    }
                    delay(1000)
                }
            } catch (e: Exception) {
                Log.e("ErrorFromSetting", e.toString())
            }

        }
    }
    fun getCurrentDateTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    override fun onStart() {
        super.onStart()
        startCurrentDateTime()
    }
}

//not working
/*    private fun setEventFilterDialog() {
        tcpSettingDialog!!.setContentView(customDialogRfidTcpBinding.root)
        tcpSettingDialog!!.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this@DockLevelerActivity,
                android.R.color.transparent
            )
        )
        tcpSettingDialog!!.getWindow()?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        customDialogRfidTcpBinding.closeDialogue.setOnClickListener {
            tcpSettingDialog!!.cancel()
        }
        customDialogRfidTcpBinding.radioGroupTcp.check(customDialogRfidTcpBinding.radioBtn1.getId())

        customDialogRfidTcpBinding.radioGroupTcp.setOnCheckedChangeListener { buttonView, selected ->
            if (selected == customDialogRfidTcpBinding.radioBtn1.getId()) {
                customDialogRfidTcpBinding.tvTcpStatus.text = "Connected"
            } else if (selected == customDialogRfidTcpBinding.radioBtn2.getId()) {
                customDialogRfidTcpBinding.tvTcpStatus.text = "Disconnected"
            }
        }

        tcpSettingDialog!!.setCancelable(true)



        tcpSettingDialog!!.show()
    }*/