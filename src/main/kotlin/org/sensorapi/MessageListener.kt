package org.sensorapi

import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortEvent
import com.fazecast.jSerialComm.SerialPortMessageListener

class MessageListener: SerialPortMessageListener {
    override fun getListeningEvents(): Int {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED
    }

    override fun serialEvent(event: SerialPortEvent?) {
        if (event == null) {
            return
        }

        val delimitedMessage = event!!.receivedData
        print(delimitedMessage.decodeToString())
        context?.send(delimitedMessage.decodeToString())
    }

    override fun getMessageDelimiter(): ByteArray {
        return byteArrayOf(0x0A)
    }

    override fun delimiterIndicatesEndOfMessage(): Boolean {
        return true
    }

}