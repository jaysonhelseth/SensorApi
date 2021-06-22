package org.sensorapi

import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortEvent
import com.fazecast.jSerialComm.SerialPortMessageListener
import io.javalin.Javalin
import io.javalin.websocket.WsConnectContext
import io.javalin.websocket.WsMessageContext
import kotlin.system.exitProcess

var context: WsConnectContext? = null

fun main(args: Array<String>) {
    val device = setupDevice()

    val app = Javalin.create().events { event ->
        event.serverStopped { stop(device) }
        event.serverStartFailed { stop(device) }
    }

    app.start()
    app.get("/") { ctx -> ctx.result("Hello World") }
    app.get("/start") { ctx ->
        ctx.html(Html.getPage("/test.html"))
        //ctx.html({ }.javaClass.getResource("test.html").readText())
    }

    app.ws("/ws") { ws ->
        ws.onConnect { ctx ->
            context = ctx
            println("connected")
        }
        ws.onClose { ctx ->
            println("client closed")
            app.stop()
        }
        ws.onMessage { ctx -> println(ctx.message()) }
    }
}

private fun stop(device: SerialPort) {
    device.removeDataListener()
    device.closePort()
}

private fun setupDevice(): SerialPort {
    var device: SerialPort? = null
    var ports = SerialPort.getCommPorts()
    val name = "ttyUSB0" // macOS: "cu.usbserial-0001"

    for (port in ports) {
        println(port.systemPortName)

        if (port.systemPortName == name) {
            device = port
            break
        }
    }
    if (device == null) {
        println("Could not find any devices to connect to. Exiting.")
        exitProcess(1)
    }

    device.openPort()

    val listener = MessageListener()
    device.addDataListener(listener)

    return device
}