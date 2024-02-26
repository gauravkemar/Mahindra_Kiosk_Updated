package com.kemarport.mahindrakiosk.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.kemarport.mahindrakiosk.R
import com.kemarport.mahindrakiosk.databinding.ActivityKioskHomeFlippinBinding
import com.kemarport.mahindrakiosk.databinding.ManualDockFunctionOptionsDialogueBinding
import com.kemarport.mahindrakiosk.dockleveler.DockLevelerActivity
import com.kemarport.mahindrakiosk.helper.*
import com.kemarport.mahindrakiosk.home.adapter.HomeDockAdapter
import com.kemarport.mahindrakiosk.home.adapter.HomeLevelerAdapter
import com.kemarport.mahindrakiosk.home.adapter.HomeParkingAdapter
import com.kemarport.mahindrakiosk.home.adapter.HomeRgpAdapter
import com.kemarport.mahindrakiosk.home.model.HomeLevelerModel
import com.kemarport.mahindrakiosk.home.model.RgpModel
import com.kemarport.mahindrakiosk.home.model.dock.*
import com.kemarport.mahindrakiosk.home.model.leveler.LevelerListResponse
import com.kemarport.mahindrakiosk.home.model.parking.ParkingListResponse
import com.kemarport.mahindrakiosk.home.model.unloading.VehicleUnloadingRequest
import com.kemarport.mahindrakiosk.home.viewmodel.KioskHomeViewModel
import com.kemarport.mahindrakiosk.home.viewmodel.KioskHomeViewModelFactory
import com.kemarport.mahindrakiosk.repository.KioskRepository
import com.kemarport.mahindrakiosk.unloadingconfirmation.UnloadingConfirmationActivity
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.*
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.*

//,TCP.DataReceivedListener

class KioskHomeFlippinActivity : AppCompatActivity() {

    lateinit var binding: ActivityKioskHomeFlippinBinding

    //api binding
    private lateinit var viewModel: KioskHomeViewModel

    //for unloading begin
    lateinit var listOflevelerForVehicleModel: ArrayList<ListofLevelerResponse>
    lateinit var listOfPendingDockForVehicleModel: ArrayList<PendingDock>

    ///
    private var dockListRecycler: RecyclerView? = null
    private var dockRecyclerAdapter: HomeDockAdapter? = null
    lateinit var listOfDockReportingVehicleModel: ArrayList<ListOfDockReportingVehicleResponseItem>


    ///LEVELER
    private var levelerListRecycler: RecyclerView? = null
    private var levelerRecyclerAdapter: HomeLevelerAdapter? = null
    lateinit var homeLevelerArrayModel: ArrayList<HomeLevelerModel>
    lateinit var listOfLevelerModel: ArrayList<LevelerListResponse>


    //RGP
    private var rgpListRecycler: RecyclerView? = null
    private var rgpRecyclerAdapter: HomeRgpAdapter? = null
    lateinit var rgpArrayModel: ArrayList<RgpModel>
    lateinit var listOfRGPVehicle: ArrayList<LevelerListResponse>

    //PARKING LIST
    private var parkingQueueRecycler: RecyclerView? = null
    private var parkingListAdapter: HomeParkingAdapter? = null
    lateinit var listOfParking: ArrayList<ParkingListResponse>
    lateinit var homeParkingQueue: ArrayList<HomeDockSupplierModel>


    var isFront = true
    private lateinit var progress: ProgressDialog

    lateinit var pushUpIn: Animation
    lateinit var pushDownOut: Animation
    lateinit var pushDownIn: Animation
    lateinit var pushUpOut: Animation
    var lastPosition = -1
    private var swipeItemTouchHelper: ItemTouchHelper? = null
    private var selectedLevelerId: Int? = null
    private var selectedDockId: Int? = null
    private var selectedRfid: String = ""
    private var token: String? = ""
    private var userId: String? = ""
    private var userRFID: String? = ""
    private var dockNo: String? = ""
    private var shopNo: String? = ""
    private var Id: String? = ""
    private lateinit var session: SessionManager
    private lateinit var userDetails: HashMap<String, Any?>
    var btn1Flag = true
    var btn2Flag = false
    var btn3Flag = false

    // private var isSwiping = false
    //var tcp = TCP()
    private val applicationApp: ApplicationClass by lazy { application as ApplicationClass }
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null
    var coroutineScopeForCurrentTime = CoroutineScope(Dispatchers.Main)
    private var isSwipingEnabled = true

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_kiosk_home_flippin)

  /*      manualDockFunctionOptionsDialogueBinding =
            ManualDockFunctionOptionsDialogueBinding.inflate(getLayoutInflater());
        dockReportingDialog = Dialog(this)
        manualDockFunctionOptionsDialogueBinding.tapId.requestFocus()


        manualDockFunctionOptionsDialogueBinding.tapId.inputType = InputType.TYPE_NULL*/
        binding.buttonToggleGroup1.check(binding.unloadingStartBtn1.getId())
        dockListRecycler = binding!!.dockQueueRc
        parkingQueueRecycler = binding!!.parkingQueueRc
        rgpListRecycler = binding!!.rcRgp
        progress = ProgressDialog(this)
        progress.setMessage("Loading...")
        session = SessionManager(this)
        userDetails = session.getUserDetails()
        token = userDetails[Constants.KEY_JWT_TOKEN].toString()
        userId = userDetails[Constants.KEY_USER_NAME].toString()
        userRFID = userDetails[Constants.USER_RFID].toString()
        shopNo = userDetails[Constants.PARENT_LOCATION_CODE].toString()
        dockNo = userDetails[Constants.LOCATION_NAME].toString()
        Id = userDetails[Constants.USER_ID].toString()
        startCurrentDateTime()
        binding.tvDockNo.setText(dockNo)
        binding.tvShopNo.setText(shopNo)
        binding.tvSupervisorName.setText(userId)

        Toasty.Config.getInstance()
            .setGravity(Gravity.CENTER)
            .apply()

        val kioskRepository = KioskRepository()
        val viewModelProviderFactory =
            KioskHomeViewModelFactory(application, kioskRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[KioskHomeViewModel::class.java]

        levelerListRecycler = binding!!.levelerRc

        listOfDockReportingVehicleModel = ArrayList()
        listOfLevelerModel = ArrayList()
        listOfRGPVehicle = ArrayList()
        listOfParking = ArrayList()
        listOflevelerForVehicleModel = ArrayList()
        listOfPendingDockForVehicleModel = ArrayList()
        homeLevelerArrayModel = ArrayList()
        rgpArrayModel = ArrayList()
        homeParkingQueue = ArrayList()
        dockRecyclerAdapter = HomeDockAdapter()
        parkingListAdapter = HomeParkingAdapter()
        levelerRecyclerAdapter = HomeLevelerAdapter()
        rgpRecyclerAdapter = HomeRgpAdapter()

        binding.mcvDockQueue.setCardBackgroundColor(resources.getColor(R.color.lighter_blue))
        binding.tvDockQueue.setTextColor(resources.getColor(R.color.white))
        binding.tvDockSuplierName.setTextColor(resources.getColor(R.color.blue))

        binding.settingsBtn.setOnClickListener {
            startActivity(Intent(this@KioskHomeFlippinActivity, DockLevelerActivity::class.java))
        }

        //binding.tvDateTime.setText(getCurrentDateTime())
        pushUpIn =
            AnimationUtils.loadAnimation(applicationContext, R.animator.push_up_in)
        pushDownOut =
            AnimationUtils.loadAnimation(applicationContext, R.animator.push_down_out)
        pushDownIn =
            AnimationUtils.loadAnimation(applicationContext, R.animator.push_down_in)
        pushUpOut =
            AnimationUtils.loadAnimation(applicationContext, R.animator.push_up_out)
        setDeafaultPg()
        binding.cancelBtn.setOnClickListener {
            if (!isFront) {
                binding.cardviewRight.startAnimation(pushUpIn).apply {
                    binding.cardviewRight.visibility = View.VISIBLE
                }
                binding.cardviewRightBackSide.startAnimation(pushUpOut).apply {
                    binding.cardviewRightBackSide.visibility = View.INVISIBLE
                }
                isFront = true
                dockRecyclerAdapter!!.highlightItem(-1)

                binding.buttonToggleGroup1.check(binding.unloadingStartBtn1.getId())
                btn1Flag = true
                btn2Flag = false
                btn3Flag = false

                selectedLevelerId = null
                selectedDockId = null
            }
        }

        println("DEVICE IP:-" + getDeviceIPAddress())
        callListApi()
        viewModel.validateDockReporting.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    //hideProgressBar()
                    callListApi()
                    /*dockReportingDialog!!.dismiss()
                    manualDockFunctionOptionsDialogueBinding.tapId.setText("")*/
                    response.data?.let { resultResponse ->
                        if (resultResponse.responseMessage.equals("WRONG LOCATION")) {
                            session.sweetAlertFailure(
                                this@KioskHomeFlippinActivity,
                                resultResponse.responseMessage
                            )
                        } else {
                            session.sweetAlertSuccess(
                                this@KioskHomeFlippinActivity,
                                resultResponse.responseMessage
                            )
                        }
                    }
                }

                is Resource.Error -> {
                    //hideProgressBar()
                    //dockReportingDialog!!.dismiss()
                    response.message?.let { resultResponse ->
                        // Toasty.error(this, resultResponse, Toasty.LENGTH_SHORT).show()
                        session.sweetAlertFailure(this@KioskHomeFlippinActivity, resultResponse)
                        //manualDockFunctionOptionsDialogueBinding.tapId.setText("")
                        if (resultResponse == "Unauthorized" || resultResponse == "Authentication token expired" ||
                            resultResponse == Constants.CONFIG_ERROR
                        ) {
                            session.showCustomDialog(
                                "Session Expired",
                                "Please re-login to continue",
                                this@KioskHomeFlippinActivity
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
        viewModel.getListOfDockReportingVehicleMutableLiveData.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.homeSwipeRefresh.isRefreshing = false
                    //   hideProgressBar()
                    listOfDockReportingVehicleModel.clear()
                    response.data?.let { resultResponse ->
                        if (resultResponse.size > 0) {
                            var searchTx = binding.edSearch.text.toString()
                            listOfDockReportingVehicleModel.addAll(resultResponse)
                            if (searchTx.isEmpty()) {
                                setDockQueueList(listOfDockReportingVehicleModel)
                                binding.tvDockQueueCount.setText(resultResponse.size.toString())
                            } else {
                                binding.edSearch.addTextChangedListener(object : TextWatcher {
                                    override fun onTextChanged(
                                        s: CharSequence?,
                                        start: Int,
                                        before: Int,
                                        count: Int
                                    ) {
                                        val searchText = s.toString().trim().toLowerCase()
                                        if (searchText != null && searchText.isNotEmpty()) {
                                            val filteredDockReportingListList =
                                                ArrayList(listOfDockReportingVehicleModel.filter {
                                                    it.vrn?.toLowerCase()!!.contains(searchText)
                                                })
                                            setDockQueueList(filteredDockReportingListList)
                                        } else {
                                            setDockQueueList(listOfDockReportingVehicleModel)
                                        }
                                    }

                                    override fun beforeTextChanged(
                                        s: CharSequence?,
                                        start: Int,
                                        count: Int,
                                        after: Int
                                    ) {
                                    }

                                    override fun afterTextChanged(s: Editable?) {}
                                })
                            }
                        } else {
                            binding.tvDockQueueCount.setText("0")
                        }

                    }
                }

                is Resource.Error -> {
                    binding.homeSwipeRefresh.isRefreshing = false
                    //hideProgressBar()
                    response.message?.let { resultResponse ->
                        if (resultResponse == "Unauthorized" || resultResponse == "Authentication token expired" ||
                            resultResponse == Constants.CONFIG_ERROR
                        ) {
                            session.showCustomDialog(
                                "Session Expired",
                                "Please re-login to continue",
                                this@KioskHomeFlippinActivity
                            )
                        }
                    }
                }

                is Resource.Loading -> {
                    ///showProgressBar()
                }

                else -> {
                    //
                }
            }

        }
        viewModel.getCurrentParkingVehicleMutableLiveData.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.homeSwipeRefresh.isRefreshing = false
                    listOfParking.clear()
                    // hideProgressBar()
                    response.data?.let { resultResponse ->
                        if (resultResponse.size > 0) {
                            var searchTx = binding.edSearch.text.toString()
                            listOfParking.addAll(resultResponse)
                            if (searchTx.isEmpty()) {
                                setParkingQueue(listOfParking)
                                binding.tvDockSuplierNameCount.setText(resultResponse.size.toString())
                            } else {
                                binding.edSearch.addTextChangedListener(object : TextWatcher {
                                    override fun onTextChanged(
                                        s: CharSequence?,
                                        start: Int,
                                        before: Int,
                                        count: Int
                                    ) {
                                        val searchText = s.toString().trim().toLowerCase()
                                        if (searchText != null && searchText.isNotEmpty()) {
                                            val filteredParkingList =
                                                ArrayList(listOfParking.filter {
                                                    it.vrn!!.toLowerCase().contains(searchText)
                                                })
                                            setParkingQueue(filteredParkingList)
                                        } else {
                                            setParkingQueue(listOfParking)
                                        }
                                    }

                                    override fun beforeTextChanged(
                                        s: CharSequence?,
                                        start: Int,
                                        count: Int,
                                        after: Int
                                    ) {
                                    }

                                    override fun afterTextChanged(s: Editable?) {}
                                })
                            }

                        } else {
                            binding.tvDockSuplierNameCount.setText("0")
                        }

                    }
                }

                is Resource.Error -> {
                    binding.homeSwipeRefresh.isRefreshing = false
                    //hideProgressBar()
                    response.message?.let { resultResponse ->
                        // Toasty.error(this, resultResponse, Toasty.LENGTH_SHORT).show()
                        // session.sweetAlertFailure(this@KioskHomeFlippinActivity,resultResponse)
                        if (resultResponse == "Unauthorized" || resultResponse == "Authentication token expired" ||
                            resultResponse == Constants.CONFIG_ERROR
                        ) {
                            session.showCustomDialog(
                                "Session Expired",
                                "Please re-login to continue",
                                this@KioskHomeFlippinActivity
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
        viewModel.getCurrentLevelerListMutableLiveData.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    listOfLevelerModel.clear()
                    binding.homeSwipeRefresh.isRefreshing = false
                    // hideProgressBar()
                    response.data?.let { resultResponse ->
                        if (resultResponse.size > 0) {
                            listOfLevelerModel.addAll(resultResponse)
                            setRCLeveler(listOfLevelerModel)
                        }
                    }
                }

                is Resource.Error -> {
                    binding.homeSwipeRefresh.isRefreshing = false
                    //hideProgressBar()
                    response.message?.let { resultResponse ->
                        //Toasty.error(this, resultResponse, Toasty.LENGTH_SHORT).show()
                        //   session.sweetAlertFailure(this,resultResponse)
                        if (resultResponse == "Unauthorized" || resultResponse == "Authentication token expired" ||
                            resultResponse == Constants.CONFIG_ERROR
                        ) {
                            session.showCustomDialog(
                                "Session Expired",
                                "Please re-login to continue",
                                this@KioskHomeFlippinActivity
                            )
                        }

                    }
                }

                is Resource.Loading -> {
                    /// showProgressBar()
                }

                else -> {
                    //
                }
            }

        }
        viewModel.getCurrentRGPListMutableLiveData.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.homeSwipeRefresh.isRefreshing = false
                    // hideProgressBar()
                    listOfRGPVehicle.clear()
                    response.data?.let { resultResponse ->
                        if (resultResponse.size > 0) {
                            listOfRGPVehicle.addAll(resultResponse)
                            setRCRgp(listOfRGPVehicle)
                            binding.tvRgp.setText("RGP Loading (${resultResponse.size})")
                        } else {
                            binding.tvRgp.setText("RGP Loading (0)")
                        }
                    }
                }

                is Resource.Error -> {
                    binding.homeSwipeRefresh.isRefreshing = false
                    //hideProgressBar()
                    response.message?.let { resultResponse ->
                        //Toasty.error(this, resultResponse, Toasty.LENGTH_SHORT).show()
                        // session.sweetAlertFailure(this,resultResponse)
                        if (resultResponse == "Unauthorized" || resultResponse == "Authentication token expired" ||
                            resultResponse == Constants.CONFIG_ERROR
                        ) {
                            session.showCustomDialog(
                                "Session Expired",
                                "Please re-login to continue",
                                this@KioskHomeFlippinActivity
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
        viewModel.unloadingBeginMutableLiveData.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    //hideProgressBar()
                    listOfDockReportingVehicleModel.clear()
                    callListApi()

                    session.sweetAlertSuccess(this, "Unloading Begin Successful")
                    response.data?.let { resultResponse ->
                        if (!isFront) {
                            binding.cardviewRight.startAnimation(pushUpIn).apply {
                                binding.cardviewRight.visibility = View.VISIBLE
                            }
                            binding.cardviewRightBackSide.startAnimation(pushUpOut).apply {
                                binding.cardviewRightBackSide.visibility = View.INVISIBLE
                            }
                            isFront = true
                            dockRecyclerAdapter!!.highlightItem(-1)
                            dockRecyclerAdapter!!.removeItem(-1)
                            binding.buttonToggleGroup1.check(binding.unloadingStartBtn1.getId())
                            btn1Flag = true
                            btn2Flag = false
                            btn3Flag = false
                            selectedLevelerId = null
                            selectedDockId = null
                        }
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
                                this@KioskHomeFlippinActivity
                            )
                        }
                    }
                }

                is Resource.Loading -> {
                    //showProgressBar()
                }

                else -> {
                    //
                }
            }

        }

        binding.mcvDockQueue.setOnClickListener {
            getListOfDockReportingVehicle()
            binding.dockQueueRc.visibility = View.VISIBLE
            binding.parkingQueueRc.visibility = View.GONE
            binding.mcvDockQueue.setCardBackgroundColor(resources.getColor(R.color.lighter_blue))
            binding.tvDockQueue.setTextColor(resources.getColor(R.color.white))
            binding.tvDockSuplierName.setTextColor(resources.getColor(R.color.blue))
            binding.mcvDockParking.setCardBackgroundColor(resources.getColor(R.color.white))

            if (!isFront) {
                binding.cardviewRight.startAnimation(pushUpIn).apply {
                    binding.cardviewRight.visibility = View.VISIBLE
                }
                binding.cardviewRightBackSide.startAnimation(pushUpOut).apply {
                    binding.cardviewRightBackSide.visibility = View.INVISIBLE
                }
                isFront = true
                dockRecyclerAdapter!!.highlightItem(-1)
            }
        }
        binding.mcvDockParking.setOnClickListener {
            getListOfParkingVehicle()
            binding.dockQueueRc.visibility = View.GONE
            binding.parkingQueueRc.visibility = View.VISIBLE
            binding.tvDockSuplierName.setTextColor(resources.getColor(R.color.white))
            binding.tvDockQueue.setTextColor(resources.getColor(R.color.blue))
            binding.mcvDockParking.setCardBackgroundColor(resources.getColor(R.color.lighter_blue))
            binding.mcvDockQueue.setCardBackgroundColor(resources.getColor(R.color.white))
            if (!isFront) {
                binding.cardviewRight.startAnimation(pushUpIn).apply {
                    binding.cardviewRight.visibility = View.VISIBLE
                }
                binding.cardviewRightBackSide.startAnimation(pushUpOut).apply {
                    binding.cardviewRightBackSide.visibility = View.INVISIBLE
                }
                isFront = true
            }
            dockRecyclerAdapter!!.highlightItem(-1)
        }
        binding.buttonToggleGroup1.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.unloading_start_btn1 -> {
                    btn1Flag = true
                    btn2Flag = false
                    btn3Flag = false
                }

                R.id.unloading_start_btn2 -> {
                    btn1Flag = false
                    btn2Flag = true
                    btn3Flag = false
                }

                R.id.unloading_start_btn3 -> {
                    btn1Flag = false
                    btn2Flag = false
                    btn3Flag = true
                }
            }
        }

        binding.logoutBtn.setOnClickListener {
            //session.logoutUser()
            session.showAlertLogOut(this@KioskHomeFlippinActivity)
            /*startActivity(Intent(this, LoginActivity::class.java))
            finish()*/
        }
        binding.submitBtn.setOnClickListener {
            unloadingBegin()
        }
        binding.homeSwipeRefresh.setOnRefreshListener { callListApi() }

        applicationApp.receivedData.observe(this) { data ->
            // Handle received data here
            if (data != null) {
                println("Received data in MainActivity: $data")
                dockReportingApi(data)
            }
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
            } catch (ee: Exception) {
                Log.e("ErrorFromCurrent", ee.toString())
            }

        }
    }

    private fun setDeafaultPg() {
        if (isFront) {
            binding.cardviewRight.visibility = View.VISIBLE
            binding.cardviewRightBackSide.visibility = View.INVISIBLE
        }
    }

    private fun callListApi() {
        getListOfDockReportingVehicle()
        getListOfParkingVehicle()
        getListOfLeveler()
        getCurrentRGPList()
    }

    override fun onStart() {
        super.onStart()
        performPeriodicTasks()
        startCurrentDateTime()
    }

    override fun onStop() {
        super.onStop()
        stopPeriodicTasks()
        coroutineScopeForCurrentTime.cancel()
    }

    private fun stopPeriodicTasks() {
        job?.cancel() // Cancel the periodic task job when the activity is not visible
    }

    private fun dockReportingApi(data: String) {
        try {
            viewModel.getIpValidation(
                token!!,
                Constants.baseUrl2,
                DockReportingRequest(data, getDeviceIPAddress() ?: "0.0.0.0", "", "")
            )
            //manualDockFunctionOptionsDialogueBinding.tapId.setText("")
        } catch (e: Exception) {
            session.sweetAlertFailure(this, e.printStackTrace().toString())
        }
    }

    private fun getCurrentRGPList() {
        try {
            viewModel.getCurrentRGPList(
                token!!,
                Constants.baseUrl2,
                /*getDeviceIPAddress() ?: "0.0.0.0"*/
                getDeviceIPAddress() ?: "0.0.0.0"
            )
        } catch (e: Exception) {
            //Toasty.error(this, e.printStackTrace().toString(), Toasty.LENGTH_SHORT).show()
            session.sweetAlertFailure(this, e.printStackTrace().toString())
        }
    }

    private fun getListOfDockReportingVehicle() {
        try {
            viewModel.getListOfDockReportingVehicleMutableLiveData(
                token!!,
                Constants.baseUrl2,
                getDeviceIPAddress() ?: "0.0.0.0"
            )
        } catch (e: Exception) {
            //Toasty.error(this, e.printStackTrace().toString(), Toasty.LENGTH_SHORT).show()
            session.sweetAlertFailure(this, e.printStackTrace().toString())
        }
    }

    private fun getListOfParkingVehicle() {
        try {
            viewModel.getCurrentParkingVehicle(
                token!!,
                Constants.baseUrl2,
                getDeviceIPAddress() ?: "0.0.0.0"
            )
        } catch (e: Exception) {
            //Toasty.error(this, e.printStackTrace().toString(), Toasty.LENGTH_SHORT).show()
            session.sweetAlertFailure(this, e.printStackTrace().toString())
        }
    }

    private fun getListOfLeveler() {
        try {
            viewModel.getCurrentLevelerListVehicle(
                token!!,
                Constants.baseUrl2,
                getDeviceIPAddress() ?: "0.0.0.0"
            )
        } catch (e: Exception) {
            //Toasty.error(this, e.printStackTrace().toString(), Toasty.LENGTH_SHORT).show()
            session.sweetAlertFailure(this, e.printStackTrace().toString())
        }
    }

    private fun setDockQueueList(
        listOfDockReportingVehicleResponseItem:
        ArrayList<ListOfDockReportingVehicleResponseItem>
    ) {

        dockListRecycler!!.adapter = dockRecyclerAdapter
        dockRecyclerAdapter?.setDockList(
            listOfDockReportingVehicleResponseItem,
            this@KioskHomeFlippinActivity,
            R.drawable.truck_icon
        )
        binding.dockQueueRc.layoutManager = LinearLayoutManager(this)

        if (binding.cardviewRightBackSide.visibility == View.VISIBLE) {
            isSwipingEnabled = false
            swipeItemTouchHelper?.attachToRecyclerView(null) // Detach the ItemTouchHelper
        } else {
            isSwipingEnabled = true
            swipeItemTouchHelper =
                ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        if (isSwipingEnabled) {
                            val swipedPosition = viewHolder.adapterPosition
                            if (isFront) {
                                listOflevelerForVehicleModel.clear()
                                listOfPendingDockForVehicleModel.clear()
                                lastPosition = swipedPosition
                                dockRecyclerAdapter!!.highlightItem(-1)
                                dockRecyclerAdapter!!.highlightItem(swipedPosition)
                                binding.cardviewRight.startAnimation(pushDownOut).apply {
                                    binding.cardviewRight.visibility = View.INVISIBLE
                                }
                                binding.cardviewRightBackSide.startAnimation(pushDownIn).apply {
                                    binding.cardviewRightBackSide.visibility = View.VISIBLE
                                }
                                isFront = false
                            } else {
                                dockRecyclerAdapter!!.highlightItem(lastPosition)
                            }
                        } else {
                            session.sweetAlertFailure(
                                this@KioskHomeFlippinActivity,
                                "Please complete current transaction"
                            )
                        }
                    }

                    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
                        return defaultValue * 1.5f
                    }

                    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                        return 0.2f // Set the threshold here (0.0 to 1.0)
                    }
                })

            swipeItemTouchHelper!!.attachToRecyclerView(binding.dockQueueRc) // Attach it back
            dockRecyclerAdapter!!.setOnItemClickListener {

                binding.tvTruck.setText(it.vrn)
                if ( it.supplier.toString() != "null" ) {
                    binding.tvSupplier.setText(it.supplier.toString())
                    Log.e("suppliername",it.supplier.toString() )

                } else {
                    binding.tvSupplier.setText("NA")
                }

                binding.tvReportTime.setText(it.dockReportingTime)
                it.listofLevelerResponse?.let { it1 -> listOflevelerForVehicleModel.addAll(it1) }
                it.pendingDock?.let { it1 -> listOfPendingDockForVehicleModel.addAll(it1) }

                if (it.isGreenChannel == true)
                    binding.imgChennal.setImageDrawable(resources.getDrawable(R.drawable.ic_green_channel))
                else
                    binding.imgChennal.visibility = View.INVISIBLE
                setSpinnerForVehicleLevelerOrDock()
            }
        }

    }

    private fun setSpinnerForVehicleLevelerOrDock() {
        binding.clLeveler.visibility = View.VISIBLE
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOflevelerForVehicleModel.map { it.levelerName })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerApp.adapter = adapter
        binding.spinnerApp.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                selectedLevelerId = listOflevelerForVehicleModel[position].levelerId
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        binding.unloadingStartBtn1.setOnClickListener {
            binding.clLeveler.visibility = View.VISIBLE
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                listOflevelerForVehicleModel.map { it.levelerName })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerApp.adapter = adapter

            binding.spinnerApp.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    selectedLevelerId = listOflevelerForVehicleModel[position].levelerId
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
        binding.unloadingStartBtn2.setOnClickListener {
            binding.clLeveler.visibility = View.VISIBLE
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                listOfPendingDockForVehicleModel.map { it.dockName })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerApp.adapter = adapter

            binding.spinnerApp.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    selectedDockId = listOfPendingDockForVehicleModel[position].jobmilestone
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
        binding.unloadingStartBtn3.setOnClickListener {
            binding.clLeveler.visibility = View.GONE
        }
    }
    private fun unloadingBegin() {
        try {
            val vrn = binding.tvTruck.text.toString()
            when {
                btn1Flag && selectedLevelerId != null -> {
                    viewModel.unloadingBegin(
                        token!!,
                        Constants.baseUrl2,
                        VehicleUnloadingRequest(
                            getDeviceIPAddress() ?: "0.0.0.0",
                            selectedLevelerId.toString(),
                            "BEGIN",
                            null,
                            "CONTINUE",
                            null,
                            null,
                            Id!!.toInt(),
                            0,
                            vrn,
                            false,
                            false,
                            null,
                            "",
                            false,
                            false
                        )
                    )
                }
                btn1Flag -> {
                    session.sweetAlertFailure(
                        this@KioskHomeFlippinActivity,
                        "No Leveler is Available \n wait for some time."
                    )
                }
                btn2Flag && selectedDockId != null -> {
                    viewModel.unloadingBegin(
                        token!!,
                        Constants.baseUrl2,
                        VehicleUnloadingRequest(
                            getDeviceIPAddress() ?: "0.0.0.0",
                            null,
                            "BEGIN",
                            selectedDockId.toString(),
                            "SKIP",
                            null,
                            null,
                            Id!!.toInt(),
                            0,
                            vrn,
                            false,
                            false,
                            null,
                            "",
                            false,
                            false
                        )
                    )
                }
                btn2Flag -> {
                    session.sweetAlertFailure(
                        this@KioskHomeFlippinActivity,
                        "No Dock is Available!!."
                    )
                }
                btn3Flag -> {
                    viewModel.unloadingBegin(
                        token!!,
                        Constants.baseUrl2,
                        VehicleUnloadingRequest(
                            getDeviceIPAddress() ?: "0.0.0.0",
                            null,
                            "BEGIN",
                            null,
                            "PARKING",
                            null,
                            null,
                            Id!!.toInt(),
                            0,
                            vrn,
                            false,
                            false,
                            null,
                            "",
                            false,
                            false
                        )
                    )
                }
            }
        } catch (e: Exception) {
            session.sweetAlertFailure(this, e.printStackTrace().toString())
        }
    }


    private fun performPeriodicTasks() {
        job = coroutineScope.launch {
            while (true) {
                try {
                    getCurrentRGPList()
                    getListOfDockReportingVehicle()
                    getListOfParkingVehicle()
                    getListOfLeveler()
                } catch (e: Exception) {
                    // Handle errors as needed
                }

                // Delay for 5 seconds before running the tasks again
                delay(15000)
            }
        }
    }

    private fun setParkingQueue(parkingListResponse: ArrayList<ParkingListResponse>) {

        parkingListAdapter?.setParkingList(
            parkingListResponse,
            this@KioskHomeFlippinActivity,
            R.drawable.truck_parking_icon
        )
        parkingQueueRecycler!!.adapter = parkingListAdapter
        binding.parkingQueueRc.layoutManager = LinearLayoutManager(this)


    }

    private fun setRCLeveler(homeLevelerArrayModel: ArrayList<LevelerListResponse>) {

        levelerRecyclerAdapter?.setLevelerList(homeLevelerArrayModel, this@KioskHomeFlippinActivity)
        levelerListRecycler!!.adapter = levelerRecyclerAdapter
        binding.levelerRc.layoutManager = LinearLayoutManager(this)
        levelerRecyclerAdapter!!.setOnItemClickListener {
            var intent =
                Intent(this@KioskHomeFlippinActivity, UnloadingConfirmationActivity::class.java)
            intent.putExtra("levelerId", it.leverlerId)
            intent.putExtra("leverlerName", it.leverlerName.toString())
            intent.putExtra("vrn", it.vrn)
            intent.putExtra("unloadingStartTime", it.unloadingStartTime)
            intent.putExtra("supplierName", it.supplierName.toString())
            intent.putExtra(
                "expectedMaterialUnloadingTime",
                it.expectedMaterialUnloadingTime
            )
            intent.putExtra("jobmilestoneId", it.jobmilestoneId)
            intent.putExtra("IsUpdate", false)
            intent.putExtra("expectedMaterialUnloadingTime", it.expectedMaterialUnloadingTime)
            intent.putExtra("isOverTime", it.isOverTime)
            startActivity(intent)
            finish()
        }
    }
    private fun setRCRgp(rgpModel: ArrayList<LevelerListResponse>) {
        rgpRecyclerAdapter = HomeRgpAdapter()
        rgpRecyclerAdapter?.setRgpList(rgpModel, this@KioskHomeFlippinActivity)
        rgpListRecycler!!.adapter = rgpRecyclerAdapter
        binding.rcRgp.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rgpRecyclerAdapter!!.setOnItemClickListener {
            var intent =
                Intent(this@KioskHomeFlippinActivity, UnloadingConfirmationActivity::class.java)
            intent.putExtra("levelerId", it.leverlerId)
            intent.putExtra("leverlerName", it.leverlerName.toString())
            intent.putExtra("vrn", it.vrn)
            intent.putExtra("unloadingStartTime", it.unloadingStartTime)
            intent.putExtra("supplierName", it.supplierName.toString())
            intent.putExtra(
                "expectedMaterialUnloadingTime",
                it.expectedMaterialUnloadingTime.toString()
            )
            intent.putExtra("jobmilestoneId", it.jobmilestoneId)
            intent.putExtra("IsUpdate", true)
            intent.putExtra("receivedQuantityType", it.receivedQuantityType)
            startActivity(intent)
            finish()
        }

    }
    fun getCurrentDateTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

/*private fun unloadingBegin() {
    try {
        var vrn = binding.tvTruck.getText()
        if (btn1Flag == true) {
            if (selectedLevelerId != null) {
                //session.sweetAlertSuccess(this@KioskHomeFlippinActivity,selectedLevelerId.toString())
                viewModel.unloadingBegin(
                    token!!,
                    Constants.baseUrl2,
                    VehicleUnloadingRequest(
                        getDeviceIPAddress() ?: "0.0.0.0",
                        selectedLevelerId.toString(),
                        "BEGIN",
                        null,
                        "CONTINUE",
                        null,
                        null,
                        Id!!.toInt(),
                        0,
                        vrn.toString(),
                        false,
                        false, null, ""
                    )
                )
            }
            else {
                session.sweetAlertFailure(
                    this@KioskHomeFlippinActivity,
                    "No Leveler is Available \n wait for some time."
                )
            }
            //Toasty.success(this, selectedLevelerId.toString(), Toasty.LENGTH_SHORT).show()

        }
        else if (btn2Flag == true) {
            //Toasty.success(this, selectedLevelerId.toString(), Toasty.LENGTH_SHORT).show()
            if (selectedDockId != null) {
                viewModel.unloadingBegin(
                    token!!,
                    Constants.baseUrl2,
                    VehicleUnloadingRequest(
                        getDeviceIPAddress() ?: "0.0.0.0",
                        null,
                        "BEGIN",
                        selectedDockId.toString(),
                        "SKIP",
                        null,
                        null,
                        Id!!.toInt(),
                        0,
                        vrn.toString(),
                        false,
                        false, null, ""
                    )
                )
            }
            else {
                session.sweetAlertFailure(
                    this@KioskHomeFlippinActivity,
                    "No Dock is Available!!."
                )
            }


        }
        else if (btn3Flag == true) {
            //Toasty.success(this, "$vrn=VRN ", Toasty.LENGTH_SHORT).show()
            viewModel.unloadingBegin(
                token!!,
                Constants.baseUrl2,
                VehicleUnloadingRequest(
                    getDeviceIPAddress() ?: "0.0.0.0",
                    null,
                    "BEGIN",
                    null,
                    "PARKING",
                    null,
                    null,
                    Id!!.toInt(),
                    0,
                    vrn.toString(),
                    false,
                    false, null, ""
                )
            )
        }
    } catch (e: Exception) {
        //Toasty.error(this, e.printStackTrace().toString(), Toasty.LENGTH_SHORT).show()
        session.sweetAlertFailure(this, e.printStackTrace().toString())
    }
}*/