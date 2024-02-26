package com.kemarport.mahindrakiosk.home

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kemarport.mahindrakiosk.login.LoginActivity
import com.kemarport.mahindrakiosk.R
import com.kemarport.mahindrakiosk.databinding.ActivityKioskHomeBinding
import com.kemarport.mahindrakiosk.dockleveler.DockLevelerActivity
import com.kemarport.mahindrakiosk.helper.Constants
import com.kemarport.mahindrakiosk.helper.Resource
import com.kemarport.mahindrakiosk.home.adapter.HomeDockAdapter
import com.kemarport.mahindrakiosk.home.adapter.HomeLevelerAdapter
import com.kemarport.mahindrakiosk.home.model.HomeLevelerModel
import com.kemarport.mahindrakiosk.home.model.dock.DockReportingRequest
import com.kemarport.mahindrakiosk.home.viewmodel.KioskHomeViewModel
import com.kemarport.mahindrakiosk.home.viewmodel.KioskHomeViewModelFactory
import com.kemarport.mahindrakiosk.repository.KioskRepository
import com.kemarport.mahindrakiosk.unloadingconfirmation.UnloadingConfirmationActivity
import com.kemarport.mahindrakiosk.unloadingstart.UnloadingStartActivity
import es.dmoral.toasty.Toasty
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class KioskHomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityKioskHomeBinding

    //api binding
    private lateinit var viewModel: KioskHomeViewModel

    ///
    private var dockListRecycler: RecyclerView? = null
    private var dockRecyclerAdapter: HomeDockAdapter? = null
    //lateinit var homeDockArrayModel: ArrayList<HomeDockSupplierModel>

    ///
    private var levelerListRecycler: RecyclerView? = null
    private var levelerRecyclerAdapter: HomeLevelerAdapter? = null
    lateinit var homeLevelerArrayModel: ArrayList<HomeLevelerModel>

    private lateinit var progress: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_kiosk_home)
        dockListRecycler = binding!!.dockQueueRc
        progress = ProgressDialog(this)
        progress.setMessage("Loading...")

        val kioskRepository = KioskRepository()
        val viewModelProviderFactory =
            KioskHomeViewModelFactory(application, kioskRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[KioskHomeViewModel::class.java]



     /*   val homeDockSupplierArray = arrayOf(
            HomeDockSupplierModel("MH03CQ1663", "Sudeep Enterprise"),
            HomeDockSupplierModel("MH04AB1234", "Harshada Enterprise"),
            HomeDockSupplierModel("MH05GJ5733", "Gaurav Enterprise")
        )
*/
        levelerListRecycler = binding!!.levelerRc
        val homeLevelerArray = arrayOf(
            HomeLevelerModel("Leveler 1A", "MH04DK1876", "10:00 AM", "John Doe", Color.RED),
            HomeLevelerModel("Leveler 2A", "MH03CQ1663", "11:30 AM", "Jane Smith",Color.GREEN),
            HomeLevelerModel("Leveler 3B", "MH04AB1234", "1:45 PM", "Mike Johnson",Color.BLUE),
            HomeLevelerModel("Leveler 4B", "MH05GJ5733","1:45 PM", "Mike Tyson",Color.DKGRAY)
        )

      /*  homeDockArrayModel = ArrayList()
        homeLevelerArrayModel = ArrayList()

        homeDockArrayModel.addAll(homeDockSupplierArray)
        homeLevelerArrayModel.addAll(homeLevelerArray)*/

       /* binding.startUnloadBtn.setOnClickListener {
            startActivity(Intent(this@KioskHomeActivity, UnloadingStartActivity::class.java))
        }*/
        //setRCDockSupplier(homeDockArrayModel)
        //setRCLeveler(homeLevelerArrayModel)

        binding.logoutBtn.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
        binding.settingsBtn.setOnClickListener {
            startActivity(Intent(this@KioskHomeActivity, DockLevelerActivity::class.java))
        }
        /* binding.loginBtn.setOnClickListener {
             startActivity(Intent(this@KioskHomeActivity, LoginActivity::class.java))
         }


         binding.settingsBtn.setOnClickListener {
             startActivity(Intent(this@KioskHomeActivity, DockLevelerActivity::class.java))
         }

         binding.logoutBtn.setOnClickListener {
             startActivity(Intent(this@KioskHomeActivity, UnloadingConfirmationActivity::class.java))
         }*/
       // binding.cardviewRight.cameraDistance = 8000 * scale
        validateKioskIp()

        viewModel.validateDockReporting.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        Toasty.success(this, response.data.responseMessage, Toasty.LENGTH_SHORT).show()
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }

        }
        binding.tvDateTime.setText(getCurrentDateTime())
    }
    private fun validateKioskIp()
    {
        //Toasty.success(this, getDeviceIPAddress()?:"No Ip Found", Toasty.LENGTH_SHORT).show()
        //viewModel.getIpValidation(t,Constants.baseUrl, DockReportingRequest("156544445445455454854",getDeviceIPAddress()?:"0.0.0.0"))

    }


  /*  private fun swipeFun()
    {
        ItemTouchHelper(object :ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }

        }).attachToRecyclerView(binding.dockQueueRc)
    }*/

 /*   private fun setRCDockSupplier(homeDockArrayModel: ArrayList<HomeDockSupplierModel>) {
        dockRecyclerAdapter = HomeDockAdapter()
        dockRecyclerAdapter?.setDockList(
            homeDockArrayModel,
            this@KioskHomeActivity,
            R.drawable.truck_parking_icon
        )

        dockListRecycler!!.adapter = dockRecyclerAdapter
        binding.dockQueueRc.layoutManager = LinearLayoutManager(this)


       *//* ItemTouchHelper(object :ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                startNextActivity()
            }


        }).attachToRecyclerView(binding.dockQueueRc)*//*
        dockRecyclerAdapter!!.setOnItemClickListener {
            startNextActivity()
        }
    }*/
    private fun startNextActivity()
    {
        startActivity(Intent(this,UnloadingStartActivity::class.java))
    }

  /*  private fun setRCLeveler(homeLevelerArrayModel: ArrayList<HomeLevelerModel>) {
        levelerRecyclerAdapter = HomeLevelerAdapter()
        levelerRecyclerAdapter?.setLevelerList(homeLevelerArrayModel, this@KioskHomeActivity)
        levelerListRecycler!!.adapter = levelerRecyclerAdapter
        binding.levelerRc.layoutManager = LinearLayoutManager(this)
        levelerRecyclerAdapter!!.setOnItemClickListener {
            startActivity(Intent(this,UnloadingConfirmationActivity::class.java))
        }
    }*/
    fun getCurrentDateTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
    fun showProgressBar() {
        progress.show()
    }

    fun hideProgressBar() {
        progress.cancel()
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
}