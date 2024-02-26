package com.kemarport.mahindrakiosk.helper.rfidreader


import android.content.Context
import com.kemarport.mahindrakiosk.helper.DataReceivedListener


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object RFIDStartManager {
    private var dataListener: DataReceivedListener? = null
    private var rfidCommunicationJob: Job? = null
    lateinit var contexts:Context
    fun setDataListener(  context: Context,listener: DataReceivedListener) {
        contexts=context
        dataListener = listener
    }
    fun removeDataListener() {
        dataListener = null
    }
    fun startRFIDCommunication() {
        val listener = dataListener
        if (listener != null) {
            rfidCommunicationJob = CoroutineScope(Dispatchers.IO).launch {
                RFIDManager(contexts, listener)
            }
        }
    }
    fun stopRFIDCommunication() {
        rfidCommunicationJob?.cancel()
        rfidCommunicationJob = null
    }
}