package com.kemarport.mahindrakiosk.unloadingconfirmation


import android.app.ProgressDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.kemarport.mahindrakiosk.R
import com.kemarport.mahindrakiosk.databinding.ActivityUnloadingConfirmationBinding
import com.kemarport.mahindrakiosk.dockleveler.DockLevelerActivity
import com.kemarport.mahindrakiosk.helper.*
import com.kemarport.mahindrakiosk.home.KioskHomeFlippinActivity
import com.kemarport.mahindrakiosk.home.model.unloading.RGPTransactionRequest
import com.kemarport.mahindrakiosk.home.model.unloading.VehicleUnloadingRequest
import com.kemarport.mahindrakiosk.repository.KioskRepository
import com.kemarport.mahindrakiosk.unloadingconfirmation.viewmodel.UnloadingConfirmationViewModel
import com.kemarport.mahindrakiosk.unloadingconfirmation.viewmodel.UnloadingConfirmationViewModelFactory
import kotlinx.coroutines.*
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.*


class UnloadingConfirmationActivity : AppCompatActivity() {
    lateinit var binding: ActivityUnloadingConfirmationBinding
    lateinit var checkSwitch: SwitchCompat
    private lateinit var viewModel: UnloadingConfirmationViewModel
    private var token: String? = ""
    private var userId: String? = ""
    private var userRfid: String? = ""
    private var Id: String? = ""
    private var rfidDataOnTap: String? = ""
    private var shopNo: String? = ""
    private var dockNo: String? = ""
    private lateinit var session: SessionManager
    private lateinit var userDetails: HashMap<String, Any?>
    var switchFlag = false
    var unloadByMatadiSwitch = false
    var isSafetyComplianceSwitch = false
    private lateinit var progress: ProgressDialog

    //from intent
    private var leverlerId: String? = ""
    private var leverlerName: String? = ""
    private var vrn: String? = ""
    private var unloadingStartTime: String? = ""
    private var totalUnloadingTime: String? = ""
    private var supplierName: String? = ""
    var expectedMaterialUnloadingTime: Int? = null
    private var jobmilestoneId: String? = ""
    private var overTimeRegion: String? = ""
    private var receivedQuantityType: String? = ""
    private var isUpdate: Boolean? = null
    private var isOverTime: Boolean? = null

    private var IsSafetyCompliance: Boolean? = null
    private var IsUnloadedbyMathadi: Boolean? = null
    private val applicationApp: ApplicationClass by lazy { application as ApplicationClass }

    var btn3Normal = false
    var btn2Excess = false
    var btn1Shortage = false

    private lateinit var customKeyboard: CustomKeyboard

    private var selectedReasonSpinnerString: String? = ""
    var isFirstItemSelected = true
    var coroutineScope = CoroutineScope(Dispatchers.Main)
    var coroutineScopeForCurrentTime = CoroutineScope(Dispatchers.Main)
    lateinit var spinnerItems: MutableList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_unloading_confirmation)
        setupSession()
        setupProgressDialog()
        startCurrentDateTime()
        extractIntentExtras()
        startPeriodicTimeChecking()
        setupSoftInputAndKeyboard()
        setupViewModel()
        checkIfUpdate()
        setupSpinner()
        setupToggleButton()
        setupSwitchListeners()
        setupFocusChangeListeners()
        setupButtonClickListeners()

        checkSwitch = binding.switchYesNo
        applicationApp.receivedData.observe(this) { data ->
            // Handle received data here
            if (data != null) {
                println("Received data in Confirmationactivity: $data")
                verifySupervisor(data)
            }
        }
        viewModel.unloadingBeginMutableLiveData.observe(this)
        { response ->
            when (response) {
                is Resource.Success -> {
                    //hideProgressBar()
                    response.data?.let { resultResponse ->
                        startMainActivity()
                    }
                }

                is Resource.Error -> {
                    // hideProgressBar()
                    response.message?.let { resultResponse ->
                        session.sweetAlertFailure(this, resultResponse)
                        if (resultResponse == "Unauthorized" || resultResponse == "Authentication token expired" ||
                            resultResponse == Constants.CONFIG_ERROR
                        ) {
                            session.showCustomDialog(
                                "Session Expired",
                                "Please re-login to continue",
                                this@UnloadingConfirmationActivity
                            )
                        }
                    }
                }

                is Resource.Loading -> {
                    //ressBar()
                }

                else -> {
                    //
                }
            }
        }
        viewModel.verifySecurityRFIDMutableLive.observe(this)
        { response ->
            when (response) {
                is Resource.Success -> {
                    //hideProgressBar()

                    response.data?.let { resultResponse ->
                        val color = Color.parseColor("#00a651") // Replace with your desired color
                        val grey = Color.parseColor("#80D9D9D9") // Replace with your desired color
                        binding.imgSecurity.setImageResource(R.drawable.right_icon)
                        binding.tvSecurityScanSuccess.visibility = View.VISIBLE
                        binding.switchYesNo.isEnabled = false
                        binding.tvItemValue1.isEnabled = false
                        binding.tvItemValue2.isEnabled = false
                        binding.tvItemValue3.isEnabled = false
                        binding.tvItemValue4.isEnabled = false
                        binding.tvItemValue5.isEnabled = false
                        binding.tvItemValue1.setBackgroundColor(grey)
                        binding.tvItemValue2.setBackgroundColor(grey)
                        binding.tvItemValue3.setBackgroundColor(grey)
                        binding.tvItemValue4.setBackgroundColor(grey)
                        binding.tvItemValue5.setBackgroundColor(grey)
                        val drawableInSuperVisor = binding.imgSupervisor.drawable
                        val SecurityId = R.drawable.right_icon
                        val drawableInSecurity = binding.imgSecurity.drawable
                        val superVisorId = R.drawable.right_icon
                        if (drawableInSuperVisor != null && drawableInSuperVisor.constantState == resources.getDrawable(
                                superVisorId
                            )?.constantState &&
                            drawableInSecurity != null && drawableInSecurity.constantState == resources.getDrawable(
                                SecurityId
                            )?.constantState
                        ) {
                            ViewCompat.setBackgroundTintList(
                                binding.submitBtn,
                                ColorStateList.valueOf(color)
                            )
                            binding.submitBtn.isEnabled = true
                            binding.submitBtn.setOnClickListener {
                                confirmUnloading(Id, resultResponse.responseMessage)
                            }
                        } else {
                            session.sweetAlertFailure(
                                this@UnloadingConfirmationActivity,
                                "Please check the SupervisorId/SecurityId!"
                            )
                        }
                        var pallets = binding.tvItemValue1.text.toString().toIntOrNull() ?: 0
                        var trolleys = binding.tvItemValue2.text.toString().toIntOrNull() ?: 0
                        var bins = binding.tvItemValue3.text.toString().toIntOrNull() ?: 0
                        var ppboxes = binding.tvItemValue4.text.toString().toIntOrNull() ?: 0
                        var others = binding.tvItemValue5.text.toString().toIntOrNull() ?: 0
                        val result = pallets + trolleys + bins + ppboxes + others
                        binding.tvItem26.setText(result.toString())
                    }
                }

                is Resource.Error -> {
                    //hideProgressBar()
                    response.message?.let { resultResponse ->
                        session.sweetAlertFailure(
                            this@UnloadingConfirmationActivity,
                            resultResponse
                        )
                        if (resultResponse == "Unauthorized" || resultResponse == "Authentication token expired" ||
                            resultResponse == Constants.CONFIG_ERROR
                        ) {
                            session.showCustomDialog(
                                "Session Expired",
                                "Please re-login to continue",
                                this@UnloadingConfirmationActivity
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

    private fun setupButtonClickListeners() {
        // Set settingsBtn click listener
        binding.settingsBtn.setOnClickListener {
            startActivity(
                Intent(
                    this@UnloadingConfirmationActivity,
                    DockLevelerActivity::class.java
                )
            )
        }
        // Set cancelBtn click listener
        binding.cancelBtn.setOnClickListener {
            startMainActivity()
        }
        // Set logoutBtn click listener
        binding.logoutBtn.setOnClickListener {
            session.showAlertLogOut(this@UnloadingConfirmationActivity)
        }
        // Set submitBtn click listener
        binding.submitBtn.setOnClickListener {
            confirmUnloading(Id, "")
        }


        binding.switchYesNo.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                switchFlag = isChecked
                binding.mcv3.visibility = View.VISIBLE
                binding.clSecurity.visibility = View.VISIBLE
            } else {
                binding.mcv3.visibility = View.INVISIBLE
                switchFlag = isChecked
                binding.clSecurity.visibility = View.GONE
            }
        }

        binding.settingsBtn.setOnClickListener {
            startActivity(
                Intent(
                    this@UnloadingConfirmationActivity,
                    DockLevelerActivity::class.java
                )
            )
        }
        binding.cancelBtn.setOnClickListener {
            startMainActivity()
        }

        binding.logoutBtn.setOnClickListener {
            /*  session.logoutUser()
              startActivity(Intent(this, LoginActivity::class.java))
              finish()*/
            session.showAlertLogOut(this@UnloadingConfirmationActivity)
            //session.sweetAlertSuccess(this@UnloadingConfirmationActivity,selectedReasonSpinnerString.toString())
        }
        binding.submitBtn.setOnClickListener {
            confirmUnloading(Id, "")
        }
    }

    private fun setupSession() {
        session = SessionManager(this)
        userDetails = session.getUserDetails()
        token = userDetails[Constants.KEY_JWT_TOKEN].toString()
        userId = userDetails[Constants.KEY_USER_NAME].toString()
        userRfid = userDetails[Constants.USER_RFID].toString()
        Id = userDetails[Constants.USER_ID].toString()
        shopNo = userDetails[Constants.PARENT_LOCATION_CODE].toString()
        dockNo = userDetails[Constants.LOCATION_NAME].toString()

        // Set TextView values
        binding.tvDockNo.text = dockNo
        binding.tvShopNo.text = shopNo
    }

    private fun setupProgressDialog() {
        progress = ProgressDialog(this)
        progress.setMessage("Loading...")
    }


    private fun extractIntentExtras() {
        val receivedIntent = intent
        leverlerId = receivedIntent.getStringExtra("leverlerId")
        leverlerName = receivedIntent.getStringExtra("leverlerName")
        vrn = receivedIntent.getStringExtra("vrn")
        unloadingStartTime = receivedIntent.getStringExtra("unloadingStartTime")
        totalUnloadingTime = receivedIntent.getStringExtra("totalUnloadingTime")
        supplierName = receivedIntent.getStringExtra("supplierName")
        expectedMaterialUnloadingTime =
            receivedIntent.getIntExtra("expectedMaterialUnloadingTime", 0)
        jobmilestoneId = receivedIntent.getStringExtra("jobmilestoneId")
        receivedQuantityType = receivedIntent.getStringExtra("receivedQuantityType")
        isUpdate = receivedIntent.getBooleanExtra("IsUpdate", false)
        isOverTime = receivedIntent.getBooleanExtra("isOverTime", false)
        IsSafetyCompliance = receivedIntent.getBooleanExtra("IsSafetyCompliance", false)
        IsUnloadedbyMathadi = receivedIntent.getBooleanExtra("IsUnloadedbyMathadi", false)
        println(expectedMaterialUnloadingTime.toString() + "from activity  " + unloadingStartTime + "unloadingStart")

        // Set TextView values
        binding.tvLeveler.text = leverlerName
        binding.tvSuplier.text = supplierName
        binding.tvTime.text = unloadingStartTime
        binding.tvVehicleNo.text = vrn
        binding.tvTotalUnloadingTimeValue.text = totalUnloadingTime

    }

    private fun setupSoftInputAndKeyboard() {
        setSoftInputFalse()
        val keyboardLayout = layoutInflater.inflate(R.layout.custom_number_keyboard, null)
        customKeyboard = CustomKeyboard(this, keyboardLayout)
    }

    private fun setupViewModel() {
        val kioskRepository = KioskRepository()
        val viewModelProviderFactory =
            UnloadingConfirmationViewModelFactory(application, kioskRepository)
        viewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        )[UnloadingConfirmationViewModel::class.java]
    }

    private fun setupSpinner() {
        val languages = resources.getStringArray(R.array.Languages)
        spinnerItems = mutableListOf(*languages)
        val defaultPosition = spinnerItems.indexOfFirst { it == overTimeRegion }
        val spinner = findViewById<Spinner>(R.id.spinnerApp)

        if (spinner != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerItems)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            if (defaultPosition != -1) {
                spinner.setSelection(defaultPosition)
                spinner.isEnabled = false
            } else {
                spinner.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View, position: Int, id: Long
                    ) {

                        if (spinnerItems.contains("चयन करें (Select Reason)")) {
                            //spinnerItems.remove("चयन करें (Select Reason)")
                            isFirstItemSelected = false
                        }
                        selectedReasonSpinnerString = spinnerItems[position]
                        Toast.makeText(
                            this@UnloadingConfirmationActivity,
                            getString(R.string.selected_item) + " " +
                                    "" + languages[position], Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Handle nothing selected event if needed
                        if (!isFirstItemSelected) {
                            // Add "चयन करें" back to the data source (spinnerItems list)
                            if (!spinnerItems.contains("चयन करें (Select Reason)")) {
                                spinnerItems.add(0, "चयन करें (Select Reason)")
                            }
                            isFirstItemSelected = true
                            selectedReasonSpinnerString = "चयन करें (Select Reason)"
                        }
                    }
                }
            }
        }
    }

    private fun setupToggleButton() {
        binding.buttonToggleGroup1.check(binding.btn1.id)

        // Set ToggleButton listener
        binding.buttonToggleGroup1.setOnCheckedChangeListener { group, checkedId ->
            handleToggleButtonCheckedChange(checkedId)
        }
    }

    private fun handleToggleButtonCheckedChange(checkedId: Int) {
        when (checkedId) {
            R.id.btn1 -> {
                btn3Normal = true
                btn2Excess = false
                btn1Shortage = false
            }

            R.id.btn2 -> {
                btn3Normal = false
                btn2Excess = true
                btn1Shortage = false
            }

            R.id.btn3 -> {
                btn3Normal = false
                btn2Excess = false
                btn1Shortage = true
            }
        }
    }

    private fun setupSwitchListeners() {
        // Set switchYesNo listener
        binding.switchYesNo.setOnCheckedChangeListener { _, isChecked ->
            handleSwitchYesNoCheckedChange(isChecked)
        }

        // Set switchSafetyComplianceYesNo listener
        binding.switchSafetyComplianceYesNo.setOnCheckedChangeListener { _, isChecked ->
            isSafetyComplianceSwitch = isChecked
        }

        // Set switchUnloadedByMathadiYesNo listener
        binding.switchUnloadedByMathadiYesNo.setOnCheckedChangeListener { _, isChecked ->
            unloadByMatadiSwitch = isChecked
        }
    }

    private fun handleSwitchYesNoCheckedChange(isChecked: Boolean) {
        switchFlag = isChecked
        binding.mcv3.visibility = if (isChecked) View.VISIBLE else View.INVISIBLE
        binding.clSecurity.visibility = if (isChecked) View.VISIBLE else View.GONE
    }

    private fun setupFocusChangeListeners() {
        // Set focus change listeners for TextViews
        setOnFocusChangeListener(binding.tvItemValue1, 145)
        setOnFocusChangeListener(binding.tvItemValue2, 195)
        setOnFocusChangeListener(binding.tvItemValue3, 245)
        setOnFocusChangeListener(binding.tvItemValue4, 295)
        setOnFocusChangeListener(binding.tvItemValue5, 350)
    }

    private fun setOnFocusChangeListener(editText: EditText, height: Int) {
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                customKeyboard.showAt(editText, height, this)
            } else {
                customKeyboard.dismiss()
            }
        }

        editText.setOnClickListener {
            editText.requestFocus()
            customKeyboard.showAt(editText, height, this)
        }
    }

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
                // session.sweetAlertFailure(this@UnloadingConfirmationActivity,"Time Exception-${e.printStackTrace().toString()}")
                Log.e("ErrorFromSetting", e.toString())
            }

        }
    }

    fun calculateExpectedUnloadingTime(startTime: String, expectedMinutes: Int): Long {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val inputFormat2 = SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.getDefault())
        val startDate = inputFormat.parse(startTime)
        val currentTime = Date()
        // Calculate the expected end time by adding the expected minutes
        val expectedEndTime = Date(startDate.time + (expectedMinutes * 60 * 1000))

        // Calculate the time difference in milliseconds
        return expectedEndTime.time - currentTime.time
    }

    private fun startPeriodicTimeChecking() {
        coroutineScope.launch {
            try {
                if (unloadingStartTime?.isNotEmpty() == true) {
                    while (isActive) {
                        // in minutes
                        val timeDifference = unloadingStartTime?.let {
                            expectedMaterialUnloadingTime?.let { it1 ->
                                calculateExpectedUnloadingTime(
                                    it,
                                    it1.toInt()
                                )
                            }
                        }
                        if (timeDifference != null) {
                            if (timeDifference <= 0) {
                                // The expected unloading time has passed, take action here
                                // For example, show a notification or perform some other task
                                // You can replace this with your desired action
                                val exceededTime =
                                    -timeDifference // Calculate exceeded time (in milliseconds)
                                binding.tvTotalUnloadingTimeValue.text = formatTime(exceededTime)
                                binding.clSpinner.visibility = View.VISIBLE
                                binding.tvTotalUnloadingTimeValue.setTextColor(Color.RED)
                                println(exceededTime.toString() + "from if")

                            } else {
                                // The expected unloading time has not yet passed
                                // You can update UI or perform other tasks here if needed
                                binding.clSpinner.visibility = View.INVISIBLE
                                val currentTimeDifference =
                                    timeDifference // Calculate exceeded time (in milliseconds)
                                binding.tvTotalUnloadingTimeValue.text =
                                    formatTime(currentTimeDifference)
                                binding.tvTotalUnloadingTimeValue.setTextColor(Color.BLUE)
                                println(currentTimeDifference.toString() + "from else")
                            }
                        }
                        // Delay for 1 second before the next iteration
                        delay(1000)
                    }
                }
            } catch (e: Exception) {
                Log.e("ErrorFromConfirm", e.toString())
            }

        }
    }


    fun formatTime(timeInMillis: Long): String {
        val hours = timeInMillis / (1000 * 60 * 60)
        val minutes = (timeInMillis % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = (timeInMillis % (1000 * 60)) / 1000

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun setSoftInputFalse() {
        binding.tvItemValue1.showSoftInputOnFocus = false
        binding.tvItemValue2.showSoftInputOnFocus = false
        binding.tvItemValue3.showSoftInputOnFocus = false
        binding.tvItemValue4.showSoftInputOnFocus = false
        binding.tvItemValue5.showSoftInputOnFocus = false
        textMaxLength(binding.tvItemValue1, 4)
        textMaxLength(binding.tvItemValue2, 4)
        textMaxLength(binding.tvItemValue3, 4)
        textMaxLength(binding.tvItemValue4, 4)
        textMaxLength(binding.tvItemValue5, 4)
    }

    fun textMaxLength(editText: EditText, length: Int) {
        editText.filters = arrayOf(InputFilter.LengthFilter(length))
    }

    private fun checkIfUpdate() {
        if (isUpdate == true) {
            with(binding) {
                switchYesNo.isChecked = true
                switchFlag = true
                switchYesNo.isEnabled = false
                mcv3.visibility = View.VISIBLE
                clSecurity.visibility = View.VISIBLE
                imgSupervisor.setImageResource(R.drawable.right_icon)
                tvScanSuccess.visibility = View.VISIBLE

                buttonToggleGroup1.isEnabled = false
                btn1.isEnabled = false
                btn2.isEnabled = false
                btn3.isEnabled = false

                when (receivedQuantityType) {
                    "Excess" -> buttonToggleGroup1.check(btn2.id)
                    "Normal" -> buttonToggleGroup1.check(btn1.id)
                    "Shortage" -> buttonToggleGroup1.check(btn3.id)
                }
                with(switchSafetyComplianceYesNo) {
                    isChecked = IsSafetyCompliance == true
                    isEnabled = false
                }

                with(switchUnloadedByMathadiYesNo) {
                    isChecked = IsUnloadedbyMathadi == true
                    isEnabled = false
                }
            }
        } else {
            with(binding) {
                switchYesNo.isEnabled = true
                mcv3.visibility = View.INVISIBLE
                clSecurity.visibility = View.GONE
            }
        }
    }

    fun getCurrentDateTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun startMainActivity() {
        startActivity(
            Intent(
                this@UnloadingConfirmationActivity,
                KioskHomeFlippinActivity::class.java
            )
        )
        finish()
    }

    private fun confirmUnloading(Id: String?, securityId: String) {
        try {
            var edOther = binding.tvItem5.text.toString().trim()
            val drawableInSecurity = binding.imgSecurity.drawable
            val rightIcon = R.drawable.right_icon
            var pallets = binding.tvItemValue1.text.toString()
            var trolleys = binding.tvItemValue2.text.toString()
            var bins = binding.tvItemValue3.text.toString()
            var ppboxes = binding.tvItemValue4.text.toString()
            var others = binding.tvItemValue5.text.toString()

            val rgpProductList = listOf(
                RGPTransactionRequest(jobmilestoneId!!.toInt(), "Pallet", pallets),
                RGPTransactionRequest(jobmilestoneId!!.toInt(), "Trolleys", trolleys),
                RGPTransactionRequest(jobmilestoneId!!.toInt(), "Bins", bins),
                RGPTransactionRequest(jobmilestoneId!!.toInt(), "PP Boxes", ppboxes),
                RGPTransactionRequest(jobmilestoneId!!.toInt(), edOther, others),
            )
            if (isUpdate == false) {
                if (binding.clSpinner.visibility == View.VISIBLE) {
                    if (selectedReasonSpinnerString != null && selectedReasonSpinnerString != "चयन करें (Select Reason)") {
                        if (switchFlag == true) {
                            if (pallets.isNotEmpty() || trolleys.isNotEmpty() || bins.isNotEmpty() || ppboxes.isNotEmpty() || others.isNotEmpty()) {
                                if (drawableInSecurity != null && drawableInSecurity.constantState == resources.getDrawable(
                                        rightIcon
                                    )?.constantState
                                    || securityId != null || !securityId.equals("") || securityId.toInt() != 0
                                ) {
                                    viewModel.unloadingComplete(
                                        token!!,
                                        Constants.baseUrl2,
                                        VehicleUnloadingRequest(
                                            getDeviceIPAddress() ?: "0.0.0.0",
                                            leverlerId, "COMPLETE", "", "CONTINUE",
                                            rgpProductList, securityId.toInt(),///might crash
                                            Id!!.toInt(), 0, vrn, true, false,
                                            when {
                                                btn3Normal -> "Normal"
                                                btn2Excess -> "Excess"
                                                btn1Shortage -> "Shortage"
                                                else -> ""
                                            },
                                            selectedReasonSpinnerString,
                                            isSafetyComplianceSwitch,
                                            unloadByMatadiSwitch
                                        )
                                    )
                                } else {
                                    session.sweetAlertFailure(
                                        this@UnloadingConfirmationActivity,
                                        "Please scan the Security ID"
                                    )
                                }
                            } else {

                                viewModel.unloadingComplete(
                                    token!!, Constants.baseUrl2, VehicleUnloadingRequest(
                                        getDeviceIPAddress() ?: "0.0.0.0",
                                        leverlerId, "COMPLETE", "", "CONTINUE",
                                        rgpProductList, null, Id!!.toInt(), 0, vrn,
                                        true, false,
                                        when {
                                            btn3Normal -> "Normal"
                                            btn2Excess -> "Excess"
                                            btn1Shortage -> "Shortage"
                                            else -> ""
                                        },
                                        selectedReasonSpinnerString,
                                        isSafetyComplianceSwitch,
                                        unloadByMatadiSwitch
                                    )
                                )

                            }
                        } else {


                            viewModel.unloadingComplete(
                                token!!,
                                Constants.baseUrl2,
                                VehicleUnloadingRequest(
                                    getDeviceIPAddress() ?: "0.0.0.0", leverlerId,
                                    "COMPLETE", "", "CONTINUE", null,
                                    null, Id!!.toInt(), 0, vrn, false, false,
                                    when {
                                        btn3Normal -> "Normal"
                                        btn2Excess -> "Excess"
                                        btn1Shortage -> "Shortage"
                                        else -> ""
                                    }, selectedReasonSpinnerString,
                                    isSafetyComplianceSwitch,
                                    unloadByMatadiSwitch
                                )
                            )

                        }
                    } else {
                        session.sweetAlertFailure(
                            this@UnloadingConfirmationActivity,
                            "Please select Reason"
                        )
                    }
                } else {
                    if (switchFlag == true) {
                        if (pallets.isNotEmpty() || trolleys.isNotEmpty() || bins.isNotEmpty() || ppboxes.isNotEmpty() || others.isNotEmpty()) {
                            if (drawableInSecurity != null && drawableInSecurity.constantState == resources.getDrawable(
                                    rightIcon
                                )?.constantState
                                || securityId != null || !securityId.equals("") || securityId.toInt() != 0
                            ) {
                                viewModel.unloadingComplete(
                                    token!!,
                                    Constants.baseUrl2,
                                    VehicleUnloadingRequest(
                                        getDeviceIPAddress() ?: "0.0.0.0", leverlerId,
                                        "COMPLETE", "", "CONTINUE",
                                        rgpProductList, securityId.toInt(),///might crash
                                        Id!!.toInt(), 0, vrn,
                                        true, false,
                                        when {
                                            btn3Normal -> "Normal"
                                            btn2Excess -> "Excess"
                                            btn1Shortage -> "Shortage"
                                            else -> ""
                                        }, "",
                                        isSafetyComplianceSwitch,
                                        unloadByMatadiSwitch
                                    )
                                )
                            } else {
                                session.sweetAlertFailure(
                                    this@UnloadingConfirmationActivity,
                                    "Please scan the Security ID"
                                )
                            }
                        } else {
                            viewModel.unloadingComplete(
                                token!!,
                                Constants.baseUrl2,
                                VehicleUnloadingRequest(
                                    getDeviceIPAddress() ?: "0.0.0.0", leverlerId,
                                    "COMPLETE", "", "CONTINUE",
                                    rgpProductList, null, Id!!.toInt(),
                                    0, vrn, true, false,
                                    when {
                                        btn3Normal -> "Normal"
                                        btn2Excess -> "Excess"
                                        btn1Shortage -> "Shortage"
                                        else -> ""
                                    }, "",
                                    isSafetyComplianceSwitch,
                                    unloadByMatadiSwitch
                                )
                            )
                        }
                    } else {
                        viewModel.unloadingComplete(
                            token!!,
                            Constants.baseUrl2,
                            VehicleUnloadingRequest(
                                getDeviceIPAddress() ?: "0.0.0.0", leverlerId,
                                "COMPLETE", "", "CONTINUE",
                                null, null, Id!!.toInt(),
                                0, vrn, false, false,
                                when {
                                    btn3Normal -> "Normal"
                                    btn2Excess -> "Excess"
                                    btn1Shortage -> "Shortage"
                                    else -> ""
                                }, "",
                                isSafetyComplianceSwitch,
                                unloadByMatadiSwitch
                            )
                        )
                    }
                }
            } else {
                if (binding.clSpinner.visibility == View.VISIBLE) {
                    if (selectedReasonSpinnerString != null && selectedReasonSpinnerString != "चयन करें (Select Reason)") {
                        if (pallets.isNotEmpty() || trolleys.isNotEmpty() || bins.isNotEmpty() || ppboxes.isNotEmpty()) {
                            viewModel.unloadingComplete(
                                token!!,
                                Constants.baseUrl2,
                                VehicleUnloadingRequest(
                                    getDeviceIPAddress() ?: "0.0.0.0", leverlerId,
                                    "COMPLETE", "", "CONTINUE",
                                    rgpProductList, securityId.toInt(), Id!!.toInt(), 0, vrn,
                                    true, true,
                                    when {
                                        btn3Normal -> "Normal"
                                        btn2Excess -> "Excess"
                                        btn1Shortage -> "Shortage"
                                        else -> ""
                                    }, selectedReasonSpinnerString,
                                    isSafetyComplianceSwitch,
                                    unloadByMatadiSwitch
                                )
                            )
                        } else {
                            session.sweetAlertFailure(
                                this@UnloadingConfirmationActivity,
                                "Please Fill the required product details"
                            )
                        }
                    } else {
                        session.sweetAlertFailure(
                            this@UnloadingConfirmationActivity,
                            "Please select Reason"
                        )
                    }
                } else {
                    if (pallets.isNotEmpty() || trolleys.isNotEmpty() || bins.isNotEmpty() || ppboxes.isNotEmpty()) {
                        viewModel.unloadingComplete(
                            token!!,
                            Constants.baseUrl2,
                            VehicleUnloadingRequest(
                                getDeviceIPAddress() ?: "0.0.0.0", leverlerId,
                                "COMPLETE", "", "CONTINUE",
                                rgpProductList, securityId.toInt(), Id!!.toInt(), 0, vrn,
                                true, true,
                                when {
                                    btn3Normal -> "Normal"
                                    btn2Excess -> "Excess"
                                    btn1Shortage -> "Shortage"
                                    else -> ""
                                }, "",
                                isSafetyComplianceSwitch,
                                unloadByMatadiSwitch
                            )
                        )
                    } else {
                        session.sweetAlertFailure(
                            this@UnloadingConfirmationActivity,
                            "Please Fill the required product details"
                        )
                    }
                }
            }
        } catch (e: Exception) {
            session.sweetAlertFailure(
                this@UnloadingConfirmationActivity,
                e.printStackTrace().toString()
            )
        }
    }

    private fun verifySupervisor(data: String) {
        val color = Color.parseColor("#00a651")
        val grey = Color.parseColor("#80D9D9D9")
        val rightCheckIcon = R.drawable.right_icon
        val drawableInSuperVisor = binding.imgSupervisor.drawable
        try {
            if (isUpdate == false) {
                if (switchFlag) {
                    handleNonUpdateSwitchFlag(
                        data,
                        rightCheckIcon,
                        drawableInSuperVisor,
                        color,
                        grey
                    )
                } else {
                    handleNonUpdateNoSwitchFlag(data, color)
                }
            } else {
                handleUpdateCase(data, grey)
            }
        } catch (e: Exception) {
            session.sweetAlertFailure(
                this@UnloadingConfirmationActivity,
                e.printStackTrace().toString()
            )
        }
    }

    private fun handleNonUpdateSwitchFlag(
        data: String,
        rightCheckIcon: Int,
        drawableInSuperVisor: Drawable?,
        color: Int,
        grey: Int
    ) {
        var edOther = binding.tvItem5.text.toString().trim()
        var edOtherValue = binding.tvItemValue5.text.toString().trim()
        val drawableInSecurity = binding.imgSecurity.drawable
        val itemValues = listOf(
            binding.tvItemValue1.text.toString(),
            binding.tvItemValue2.text.toString(),
            binding.tvItemValue3.text.toString(),
            binding.tvItemValue4.text.toString(),
            binding.tvItemValue5.text.toString()
        )
        if (itemValues.any { it.isNotEmpty() }) {
            if (data.equals(userRfid, ignoreCase = true)) {
                disableButtons()
                setImageResourceForSupervisor(R.drawable.right_icon)
                showScanSuccessView()
                setBackgroundTintForSubmitBtn(color)
                binding.submitBtn.isEnabled = true // Enable the Submit button
                binding.switchYesNo.isEnabled = false // Disable the Yes/No switch
            } else if (drawableInSuperVisor != null && drawableInSuperVisor.constantState == resources.getDrawable(
                    rightCheckIcon
                )?.constantState
            ) {
                if ((edOther.isNotEmpty() && edOtherValue.isEmpty()) || (edOther.isEmpty() && edOtherValue.isNotEmpty())) {
                    runOnUiThread {
                        session.sweetAlertFailure(
                            this@UnloadingConfirmationActivity,
                            "Please fill the required details!"
                        )
                    }
                } else {
                    if (drawableInSecurity != null && drawableInSecurity.constantState == resources.getDrawable(
                            rightCheckIcon
                        )?.constantState
                    ) {
                        println("Already Verified")
                        runOnUiThread {
                            session.sweetAlertFailure(
                                this@UnloadingConfirmationActivity,
                                "Already Verified!"
                            )
                        }
                    } else {
                        viewModel.verifySecurityRFID(token!!, Constants.baseUrl2, data, "")
                        ViewCompat.setBackgroundTintList(
                            binding.submitBtn,
                            ColorStateList.valueOf(grey)
                        )
                    }
                }
            } else {
                session.sweetAlertFailure(
                    this@UnloadingConfirmationActivity,
                    "Please Scan Supervisor ID!"
                )
            }
        } else {
            if (data.equals(userRfid, ignoreCase = true)) {
                disableButtons()
                setImageResourceForSupervisor(R.drawable.right_icon)
                showScanSuccessView()
                setBackgroundTintForSubmitBtn(color)
                binding.submitBtn.isEnabled = true // Enable the Submit button
                binding.switchYesNo.isEnabled = false // Disable the Yes/No switch
            } else if (itemValues.all { it.isEmpty() }) {
                session.sweetAlertFailure(
                    this@UnloadingConfirmationActivity,
                    "Please fill the details!"
                )
            } else {
                session.sweetAlertFailure(
                    this@UnloadingConfirmationActivity,
                    "Supervisor Not Verified"
                )
            }
        }
    }

    private fun handleNonUpdateNoSwitchFlag(data: String, color: Int) {
        if (data.equals(userRfid, ignoreCase = true)) {
            disableButtons()
            setImageResourceForSupervisor(R.drawable.right_icon)
            showScanSuccessView()
            setBackgroundTintForSubmitBtn(color)
            binding.submitBtn.isEnabled = true // Enable the Submit button
            binding.switchYesNo.isEnabled = false // Disable the Yes/No switch
        } else {
            session.sweetAlertFailure(this@UnloadingConfirmationActivity, "Supervisor Not Verified")
        }
    }

    private fun handleUpdateCase(data: String, grey: Int) {
        val rightCheckIcon = R.drawable.right_icon
        val drawableInSecurity = binding.imgSecurity.drawable
        var edOther = binding.tvItem5.text.toString().trim()
        var edOtherValue = binding.tvItemValue5.text.toString().trim()
        if (data.equals(userRfid, ignoreCase = true)) {
            println("Already Verified")
        } else if (drawableInSecurity != null && drawableInSecurity.constantState == resources.getDrawable(
                rightCheckIcon
            )?.constantState
        ) {
            println("Already Verified")
        } else {
            if ((edOther.isNotEmpty() && edOtherValue.isEmpty()) || (edOther.isEmpty() && edOtherValue.isNotEmpty())) {
                runOnUiThread {
                    session.sweetAlertFailure(
                        this@UnloadingConfirmationActivity,
                        "Please fill the required details!"
                    )
                }
            } else {
                if (drawableInSecurity != null && drawableInSecurity.constantState == resources.getDrawable(
                        rightCheckIcon
                    )?.constantState
                ) {
                    println("Already Verified")
                    runOnUiThread {
                        session.sweetAlertFailure(
                            this@UnloadingConfirmationActivity,
                            "Already Verified!"
                        )
                    }
                } else {
                    viewModel.verifySecurityRFID(token!!, Constants.baseUrl2, data, "")
                    ViewCompat.setBackgroundTintList(
                        binding.submitBtn,
                        ColorStateList.valueOf(grey)
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        startPeriodicTimeChecking()
        startCurrentDateTime()
    }

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

    override fun onPause() {
        super.onPause()
        applicationApp.clearReceivedData()
        coroutineScopeForCurrentTime.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        applicationApp.clearReceivedData()
        coroutineScope.cancel()
        coroutineScopeForCurrentTime.cancel()
    }

    override fun onStop() {
        super.onStop()
        coroutineScope.cancel()
        coroutineScopeForCurrentTime.cancel()
    }

    fun refreshIcons() {
        val grey = Color.parseColor("#80D9D9D9")
        if (isUpdate == true) {
            binding.imgSecurity.setImageResource(R.drawable.securityid)
            binding.tvSecurityScanSuccess.visibility = View.GONE
            ViewCompat.setBackgroundTintList(
                binding.submitBtn,
                ColorStateList.valueOf(grey)
            )
            binding.submitBtn.isEnabled = false
            binding.switchYesNo.isEnabled = false
        } else {
            binding.imgSupervisor.setImageResource(R.drawable.supervisorid)
            binding.imgSecurity.setImageResource(R.drawable.securityid)
            binding.tvSecurityScanSuccess.visibility = View.GONE
            binding.tvScanSuccess.visibility = View.GONE
            ViewCompat.setBackgroundTintList(
                binding.submitBtn,
                ColorStateList.valueOf(grey)
            )
            binding.submitBtn.isEnabled = false
            binding.switchYesNo.isEnabled = true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startMainActivity()
    }

    private fun disableButtons() {
        binding.buttonToggleGroup1.isEnabled = false
        binding.btn1.isEnabled = false
        binding.btn2.isEnabled = false
        binding.btn3.isEnabled = false
    }

    private fun setImageResourceForSupervisor(resourceId: Int) {
        binding.imgSupervisor.setImageResource(resourceId)
    }

    private fun showScanSuccessView() {
        binding.tvScanSuccess.visibility = View.VISIBLE
    }

    private fun setBackgroundTintForSubmitBtn(color: Int) {
        ViewCompat.setBackgroundTintList(binding.submitBtn, ColorStateList.valueOf(color))
    }
}