package com.kemarport.mahindrakiosk.unloadingstart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kemarport.mahindrakiosk.R
import com.kemarport.mahindrakiosk.databinding.ActivityUnloadingStartBinding
import com.kemarport.mahindrakiosk.dockleveler.DockLevelerActivity
import com.kemarport.mahindrakiosk.home.adapter.HomeDockAdapter
import com.kemarport.mahindrakiosk.login.LoginActivity
import com.kemarport.mahindrakiosk.repository.KioskRepository
import com.kemarport.mahindrakiosk.unloadingstart.viewmodel.UnloadingStartViewModel
import com.kemarport.mahindrakiosk.unloadingstart.viewmodel.UnloadingStartViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UnloadingStartActivity : AppCompatActivity() {
    lateinit var binding: ActivityUnloadingStartBinding
    private lateinit var viewModel: UnloadingStartViewModel
    ///
    private var dockListRecycler: RecyclerView? = null
    private var dockRecyclerAdapter: HomeDockAdapter? = null
   // private var dockRecyclerAdapter: UnloadingStartAdapter?=null
    //lateinit var dockQueueArrayModel: ArrayList<HomeDockSupplierModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_unloading_start)
        dockListRecycler = binding!!.dockQueueRc
        binding.buttonToggleGroup1.check(binding.unloadingStartBtn1.getId())
   /*     val homeDockSupplierArray = arrayOf(
            UnloadingDockQueueModel("MH03CQ1663"),
            UnloadingDockQueueModel("MH04AB1234"),
            UnloadingDockQueueModel("MH05GJ5733")
        )*/

        val languages = resources.getStringArray(R.array.leveler)

        val kioskRepository = KioskRepository()
        val viewModelProviderFactory =
            UnloadingStartViewModelFactory(application, kioskRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[UnloadingStartViewModel::class.java]

        /*val homeDockSupplierArray = arrayOf(
            HomeDockSupplierModel("MH03CQ1663", "Sudeep Enterprise"),
            HomeDockSupplierModel("MH04AB1234", "Harshada Enterprise"),
            HomeDockSupplierModel("MH05GJ5733", "Gaurav Enterprise")
        )*/

/*        dockQueueArrayModel=ArrayList()
        dockQueueArrayModel.addAll(homeDockSupplierArray)*/

       // setRCDockSupplier(dockQueueArrayModel)

        binding.cancelBtn.setOnClickListener {
            finish()
        }
        binding.logoutBtn.setOnClickListener {
            startActivity(Intent(this@UnloadingStartActivity, LoginActivity::class.java))
            finish()
        }
        binding.settingsBtn.setOnClickListener {
            startActivity(Intent(this@UnloadingStartActivity, DockLevelerActivity::class.java))
        }

        val spinner = findViewById<Spinner>(R.id.spinnerApp)
        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, languages)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
        binding.tvDateTime.setText(getCurrentDateTime())

    }
   /* private fun setRCDockSupplier(homeDockArrayModel: ArrayList<HomeDockSupplierModel>) {
        dockRecyclerAdapter = HomeDockAdapter()
        dockRecyclerAdapter?.setDockList(
            homeDockArrayModel,
            this@UnloadingStartActivity,
            R.drawable.truck_parking_icon
        )

        dockListRecycler!!.adapter = dockRecyclerAdapter
        binding.dockQueueRc.layoutManager = LinearLayoutManager(this)
       *//* dockRecyclerAdapter!!.setOnItemClickListener {
            startActivity(Intent(this,UnloadingStartActivity::class.java))
        }*//*
    }*/
    fun getCurrentDateTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}