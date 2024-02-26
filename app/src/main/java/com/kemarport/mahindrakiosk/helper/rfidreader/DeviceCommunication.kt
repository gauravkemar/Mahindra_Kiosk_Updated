package com.example.rfiddemoforkiosk.originalrfid

import android.app.PendingIntent
import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import android.hardware.usb.UsbRequest
import android.widget.Toast
import com.kemarport.mahindrakiosk.helper.rfidreader.Utils
import java.util.Arrays


class DeviceCommunication constructor(context : Context) {

    private val context : Context
    private val usbManager : UsbManager
    var usbDevice : UsbDevice? = null
    private var readConnection : UsbDeviceConnection? = null
    private var writeConnection : UsbDeviceConnection? = null
    private var usbInterface : UsbInterface? = null

    //connection states
    val noDevice = 0
    val deviceFoundNoPermission = 1
    val deviceFound = 2
    private val utils = Utils()


    private var command = ByteArray(64) {0}
    private var response = ByteArray(64) {0}

    init {
        this.context = context
        this.usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    }

    private fun getReader() : UsbDevice? {
        var usbDevice : UsbDevice? = null
        val deviceList: HashMap<String, UsbDevice> = usbManager.deviceList
        deviceList.values.forEach { device ->
            if(device.vendorId == 6017 && device.productId == 3088) {
                usbDevice = device
                return@forEach
            }
        }
        return usbDevice
    }

    fun requestUsbPermission(pendingIntent: PendingIntent) {
        if(!usbManager.hasPermission(usbDevice))
            usbManager.requestPermission(usbDevice, pendingIntent)
    }

    fun connectDevice() : Int {
        usbDevice = getReader()
        if(usbDevice == null)
            return noDevice
        if(!usbManager.hasPermission(usbDevice))
            return deviceFoundNoPermission
        return deviceFound
    }

    fun disconnectDevice() : Boolean {
        if(usbDevice != null && readConnection != null && writeConnection != null && usbInterface != null) {
            readConnection?.releaseInterface(usbInterface)
            writeConnection?.releaseInterface(usbInterface)
            readConnection?.close()
            writeConnection?.close()
            usbDevice = null
        }
        return true
    }

    private fun transReceive(dataToWrite: ByteArray) : ByteArray {
        var receivedData = ByteArray(64)
        //connecting device to write by passing endpoint no. 1
        //Toast.MakeText(context, "Connecting for write...", ToastLength.Long).Show();
        //connecting device to write by passing endpoint no. 1
        //Toast.MakeText(context, "Connecting for write...", ToastLength.Long).Show();
        if (usbDevice != null) {
            usbInterface = usbDevice!!.getInterface(0)
            try {
                writeConnection = usbManager.openDevice(usbDevice)
                var epOut = usbInterface!!.getEndpoint(1)
                try {
                    val usbRequest = UsbRequest()
                    val claimSuccess = writeConnection!!.claimInterface(usbInterface, true)
                    val initSuccess = usbRequest.initialize(writeConnection, epOut)
                    if (claimSuccess && initSuccess) {

                        //Toast.MakeText(context, "Reader Connection Established Successfully !", ToastLength.Long).Show();
                        //code for data transfer
                        val isSuccess: Int = writeCommand(writeConnection!!, epOut, dataToWrite)
                        if (isSuccess >= 0) {
                            //Toast.MakeText(context, "Command Sent Successfully, " + isSuccess + " Bytes Sent.", ToastLength.Long).Show();
                        } else {
                            //Toast.MakeText(context, "Error in sending Command", ToastLength.Long).Show();
                        }
                        //closing connection
                        writeConnection!!.close()
                    } else {
                        //Toast.MakeText(context, "Error in connecting device...  ClaimSuccess - " + claimSuccess + ",  InitSuccess - " + initSuccess, ToastLength.Long).Show();
                    }
                } finally {
                    epOut = null
                }
            } catch (ex: Exception) {
                Toast.makeText(context, "Error in writing - " + ex.message, Toast.LENGTH_LONG).show()
                Toast.makeText(context, "Cause - " + Arrays.toString(ex.stackTrace), Toast.LENGTH_LONG).show()
            }
            //for(int i = 0; i < dataToWritten.Length; i++) { Toast.MakeText(context, "data - " + dataToWritten[i], ToastLength.Long).Show(); }
            if (dataToWrite[0].toInt() == 0x03 && dataToWrite[1].toInt() == 0xFF) {
                return byteArrayOf(1)
            } //this line is added to avoid exception while restarting reader
            //creating delay - tmp

            //for (int t = 0; t <= 1000; t++) ;

            //reading response of command after writingno
            receivedData[0] = 0
            usbInterface = usbDevice!!.getInterface(0)
            try {
                readConnection = usbManager.openDevice(usbDevice)
                var epIn = usbInterface!!.getEndpoint(0)
                try {
                    while (receivedData[0] < 5) receivedData = readResponse(readConnection, epIn)
                    readConnection!!.close()
                } finally {
                    epIn = null
                }
            } catch (ex: Exception) {
                Toast.makeText(context, "Error in writing - " + ex.message, Toast.LENGTH_LONG).show()
                Toast.makeText(context, "Cause - " + Arrays.toString(ex.stackTrace), Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context,
                "Device is null, operations not possible on device", Toast.LENGTH_LONG).show()
        }
        return receivedData
    }

    //function to write data to device
    private fun writeCommand(
        usbConnection1: UsbDeviceConnection,
        epOUT1: UsbEndpoint,
        dataForWrite: ByteArray
    ): Int {
        return usbConnection1.bulkTransfer(epOUT1, dataForWrite, dataForWrite.size, 0)
    }
    //function to read data from device
    private fun readResponse(usbConnection2: UsbDeviceConnection?, epIN2: UsbEndpoint): ByteArray {
        val packetSize = epIN2.maxPacketSize
        val bytes = ByteArray(packetSize)
        val result = usbConnection2?.bulkTransfer(epIN2, bytes, packetSize, 50)
        return if (result!! < 0) ByteArray(64) else bytes
    }

    fun prepareAndExecuteInventoryCommand() : List<String> {
        val allEpc : ArrayList<String> = arrayListOf()
        try {
            command[0] = 0x00
            command[1] = 0x03
            command[2] = 0x50
            command[3] = 0x02
            command = utils.addCrc(command) //calling function to add CRC
            response = transReceive(command) //calling function to send command to reader and receive response.
            if(response[3] == (0x01).toByte() && response[4] == (0xFB).toByte() && response[5] == (0).toByte()) {
                command = ByteArray(64) {0}
                response = ByteArray(64) {0}
                return listOf("")
            }
            if(response[3] != (0x01).toByte() && response[4] != (0xFB).toByte() && response[5] > 0) {
                var epcLenPosition = 6
                for (i in 1..response[5]) {
                    val epcByte = ByteArray(response[epcLenPosition].toInt())
                    for (j in 0 until response[epcLenPosition]) {
                        epcLenPosition++
                        epcByte[j] = response[epcLenPosition]
                    }
                    epcLenPosition++
                    utils.bytesToHexString(epcByte, 0, epcByte.size)?.let { allEpc.add(it) }
                }
                prepareAndExecuteBeepCommand()
            }
        }
        catch (e: Exception) {
            Toast.makeText(context, "Error - " + e.message, Toast.LENGTH_LONG).show()
            Toast.makeText(context, "Error - " + Arrays.toString(e.stackTrace), Toast.LENGTH_LONG).show()
        }
        command = ByteArray(64) {0}
        response = ByteArray(64) {0}
        return allEpc
    }
    private fun prepareAndExecuteBeepCommand() {
        command[0] = 0x00
        command[1] = 0x03
        command[2] = 0xF0.toByte()
        command[3] = 0x05
        command = utils.addCrc(command) //calling function to add CRC
        response = transReceive(command) //calling function to send command to reader and receive response.
        command = ByteArray(64) {0}
        response = ByteArray(64) {0}
    }

}