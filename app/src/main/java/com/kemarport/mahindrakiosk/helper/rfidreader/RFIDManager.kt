package com.kemarport.mahindrakiosk.helper.rfidreader


import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.example.rfiddemoforkiosk.originalrfid.DeviceCommunication
import com.kemarport.mahindrakiosk.helper.DataReceivedListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class RFIDManager(
    private val context: Context,
    private val dataReceivedListener: DataReceivedListener
) {
    private var dc: DeviceCommunication? = null
    private var epcCountList: HashMap<String, Int> = HashMap()
    private var autoInventoryJob: Job? = null

    init {
        // Initialize RFID communication
        dc = DeviceCommunication(context)
        connectToDevice()
        registerUsbDetachReceiver()
        // Start auto inventory
        startAutoInventory()
    }

    private fun connectToDevice() {
        try {
            val stat = dc!!.connectDevice()
            if (stat == dc!!.noDevice) {
                showToast("Reader Not Connected OR Connected Device Is Not Valid,\n Connect RapidRadio RFID reader to run app.")
            }/* else {
                val permissionIntent = PendingIntent.getBroadcast(
                    context, 0, Intent(getUsbAction(context)),
                    PendingIntent.FLAG_MUTABLE
                )
                dc!!.requestUsbPermission(permissionIntent)
            }*/

            if(stat == dc!!.deviceFoundNoPermission) {
                val permissionIntent = PendingIntent.getBroadcast(
                    context, 0, Intent(getUsbAction(context)),
                    PendingIntent.FLAG_MUTABLE
                )
                val filter = IntentFilter(getUsbAction((context)))
                context.registerReceiver(usbReceiver, filter)
                dc!!.requestUsbPermission(permissionIntent)
            }


            if (stat == dc!!.deviceFound) {
                showToast("Device Connected Successfully")
            }
            // Handle other cases as needed
        } catch (e: Exception) {
            // Handle exceptions
        }
    }

    private fun showToast(message: String) {
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

  /*  private fun startAutoInventory() {
        handler.post {
            GlobalScope.launch(Dispatchers.IO) {
                while (isActive) {
                    delay(1000)
                    autoInventory()
                }
            }
        }
    }*/
    private fun startAutoInventory() {
        autoInventoryJob = GlobalScope.launch(Dispatchers.IO) {
            while (isActive) {
                delay(1000)
                autoInventory()
            }
        }
    }
    private fun autoInventory() {
        val maxCount = 5
        if (epcCountList.isNotEmpty()) {
            with(epcCountList.iterator()) {
                forEach {
                    it.setValue(it.value + 1)
                    if (it.value >= maxCount) {
                        remove()
                        // update UI Here
                        updateList()
                    }
                    Log.d("READERepcCountListfrom1st if", epcCountList.toString())
                }

            }
        }

        val allEpc = dc!!.prepareAndExecuteInventoryCommand()
        Log.d("READER", "Inventory Done")
        if (!((allEpc.size == 1 && allEpc[0] == "") || allEpc.isEmpty())) {
            if (epcCountList.isEmpty()) {
                for (epc in allEpc) {
                    epcCountList[epc] = 0
                    // update UI here
                    updateList()
                    Log.d("READERepcCountListfrom if", epcCountList.toString())
                }
            } else {
                for (epc in allEpc) {
                    if (epcCountList.containsKey(epc)) {
                        epcCountList[epc] = 0
                    } else {
                        epcCountList[epc] = 0
                        // update UI here
                        updateList()
                    }
                }
                Log.d("READERepcCountListfrom else", epcCountList.toString())
            }
        }
    }

    private fun updateList() {
        val allEpc = arrayListOf<String>()
        for (epcValue in epcCountList) {
            allEpc.add(epcValue.key)
            dataReceivedListener.onDataReceived(epcValue.key)
        }
    }

    private fun registerUsbDetachReceiver() {
        val detachFilter = IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(usbDetachReceiver, detachFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            context.registerReceiver(usbDetachReceiver, detachFilter)
        }
    }

    private val usbDetachReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                stopAutoInventory()
                dc?.disconnectDevice()
            }
        }
    }

    private fun stopAutoInventory() {
        autoInventoryJob?.cancel()
        autoInventoryJob = null
    }
    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (getUsbAction(context) == action) {
                synchronized(this) {
                    val device: UsbDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                    } else {
                        @Suppress("DEPRECATION") intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    }

                    if (!intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        showToast("User Denied Permission to connect with Usb Device!")
                    } else {
                        // call method to set up device communication
                        dc?.usbDevice = device
                        showToast("Device connected successfully")
                    }
                }
            }
        }
    }

    fun getUsbAction(context: Context): String {
        return context.packageName + ".action.USB_PERMISSION"
    }

    private fun releaseUsb() {
        // Your existing USB release logic
        try {
            context.unregisterReceiver(usbReceiver)
            context.unregisterReceiver(usbDetachReceiver)
        } catch (ex: Exception) {
            Log.d("DEBUG", "receivers not registered yet")
        }
    }

    fun onDestroy() {
        // Release resources
        releaseUsb()
        // Any other cleanup logic
    }
}
/* working
class RFIDManager(
    private val context: Context,
    private val dataReceivedListener: DataReceivedListener
) {
    private var dc: DeviceCommunication? = null
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private var epcCountList: HashMap<String, Int> = HashMap()

    init {
        // Initialize RFID communication
        dc = DeviceCommunication(context)
        connectToDevice()
        registerUsbDetachReceiver()
        // Start auto inventory
        startAutoInventory()
    }

    private fun connectToDevice() {
        try{
            val stat = dc!!.connectDevice()
            if (stat == dc!!.noDevice) {
                val permissionIntent = PendingIntent.getBroadcast(
                    context, 0, Intent(getUsbAction(context)),
                    PendingIntent.FLAG_MUTABLE
                )
                val filter = IntentFilter(getUsbAction((context)))
                context.registerReceiver(usbReceiver, filter)
                dc!!.requestUsbPermission(permissionIntent)
            } else {
                val permissionIntent = PendingIntent.getBroadcast(
                    context, 0, Intent(getUsbAction(context)),
                    PendingIntent.FLAG_MUTABLE
                )
                dc!!.requestUsbPermission(permissionIntent)
            }
            if(stat == dc!!.deviceFound) {

                */
/* Toast.makeText(
                      context,
                     "Device Connected Successfully",
                     Toast.LENGTH_LONG
                 ).show()*//*

            }
            // Handle other cases as needed
        }
        catch (e:Exception)
        {

        }

    }

    private fun startAutoInventory() {
        runnable = object : Runnable {
            override fun run() {
                autoInventory()
                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(runnable!!, 0)
    }

    private fun autoInventory() {
        val maxCount = 5
        if (epcCountList.isNotEmpty()) {
            with(epcCountList.iterator()) {
                forEach {
                    it.setValue(it.value + 1)
                    if (it.value >= maxCount) {
                        remove()
                        // update UI Here
                        updateList()
                    }
                }
            }
        }

        val allEpc = dc!!.prepareAndExecuteInventoryCommand()
        Log.d("READER", "Inventory Done")
        if (!((allEpc.size == 1 && allEpc[0] == "") || allEpc.isEmpty())) {
            if (epcCountList.isEmpty()) {
                for (epc in allEpc) {
                    epcCountList[epc] = 0
                    // update UI here
                    updateList()
                }
            } else {
                for (epc in allEpc) {
                    if (epcCountList.containsKey(epc)) {
                        epcCountList[epc] = 0
                    } else {
                        epcCountList[epc] = 0
                        // update UI here
                        updateList()
                    }
                }
            }
        }
    }

    private fun updateList() {
        val allEpc = arrayListOf<String>()
        for (epcValue in epcCountList) {
            allEpc.add(epcValue.key)
            dataReceivedListener.onDataReceived(epcValue.key)
        }
    }

    private fun registerUsbDetachReceiver() {
        val detachFilter = IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(usbDetachReceiver, detachFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            context.registerReceiver(usbDetachReceiver, detachFilter)
        }
    }

    private val usbDetachReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                stopAutoInventory()
                dc?.disconnectDevice()
            }
        }
    }

    private fun stopAutoInventory() {
        handler.removeCallbacks(runnable!!)
    }

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (getUsbAction(context) == action) {
                synchronized(this) {
                    val device: UsbDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                    } else {
                        @Suppress("DEPRECATION") intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    }

                    if (!intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        Toast.makeText(context, "User Denied Permission to connect with Usb Device !", Toast.LENGTH_LONG).show()
                    } else {
                        // call method to set up device communication
                        dc?.usbDevice = device
                        Toast.makeText(context, "Device connected successfully", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun getUsbAction(context: Context): String {
        return context.packageName + ".action.USB_PERMISSION"
    }

    private fun releaseUsb() {
        // Your existing USB release logic
        try {
            context.unregisterReceiver(usbReceiver)
            context.unregisterReceiver(usbDetachReceiver)
        } catch (ex: Exception) {
            Log.d("DEBUG", "receivers not registered yet")
        }
    }

    fun onDestroy() {
        // Release resources
        releaseUsb()
        // Any other cleanup logic
    }
}*/
