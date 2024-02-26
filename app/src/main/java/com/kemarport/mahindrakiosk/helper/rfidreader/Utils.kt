package com.kemarport.mahindrakiosk.helper.rfidreader


import java.util.Locale

class Utils {

    //method to calculate and add crc to bytes of command to be sent
    fun addCrc(data: ByteArray): ByteArray {
        var crc = 0xFFFF
        var bits: Byte
        var bytes: Byte = 1
        while (bytes <= data[1]) {
            crc =
                crc xor (data[bytes.toInt()].toInt() and 0xFF) //line which made beep command run correctly.
            bits = 0
            while (bits < 8) {
                crc = if (crc and 0x8000 == 0x8000) crc shl 1 xor 0x1021 else crc shl 1
                bits++
            }
            bytes++
        }
        crc = crc.inv()
        data[data[1] + 1] = (crc shr 8).toByte()
        data[data[1] + 2] = crc.toByte()
        val dataWithCrc = ByteArray(64)
        System.arraycopy(data, 1, dataWithCrc, 0, data.size - 1)
        return dataWithCrc
    }

    //function to convert byte array to hex string
    fun bytesToHexString(src: ByteArray?, offset: Int, length: Int): String? {
        val stringBuilder = StringBuilder()
        return try {
            if (src == null || src.isEmpty()) {
                return null
            }
            for (i in offset until length) {
                val v = src[i].toInt() and 0xFF
                val hv = Integer.toHexString(v)
                if (hv.length == 1) {
                    stringBuilder.append(0)
                }
                stringBuilder.append(hv)
            }
            stringBuilder.toString().uppercase(Locale.getDefault())
        } catch (ex: Exception) {
            null
        }
    }
}