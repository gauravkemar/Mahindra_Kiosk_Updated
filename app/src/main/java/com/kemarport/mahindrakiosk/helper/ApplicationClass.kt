package com.kemarport.mahindrakiosk.helper

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kemarport.mahindrakiosk.helper.rfidreader.RFIDStartManager

class ApplicationClass : Application() {
    private val _receivedData = MutableLiveData<String?>()
    val receivedData: LiveData<String?>
        get() = _receivedData

    fun clearReceivedData() {
        _receivedData.postValue(null)
    }
    override fun onCreate() {
        super.onCreate()

        RFIDStartManager.setDataListener(this, object : DataReceivedListener {
            override fun onDataReceived(data: String) {
                _receivedData.postValue(data)
            }
        })

        RFIDStartManager.startRFIDCommunication()
    }
}
