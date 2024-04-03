package good.damn.clientsocket.listeners.network.connection

import android.util.Log
import androidx.annotation.WorkerThread
import good.damn.clientsocket.utils.NetworkUtils
import java.net.InetSocketAddress
import java.net.Socket

interface Connectable: NetworkInputListener {

    @WorkerThread
    fun onConnected(socket: Socket)

    @WorkerThread
    fun onSendBytes(): ByteArray

    @WorkerThread
    fun onSendTextBytes(): ByteArray

    @WorkerThread
    fun onSendTypeResponse(): Int

    fun connectToHost(
        ip: String,
        port: Int,
        buffer: ByteArray
    ) {
        Thread {
            val socket = Socket()
            val address = InetSocketAddress(ip,port)
            socket.connect(address,5000)

            onConnected(socket)

            val out = socket.getOutputStream()
            val inp = socket.getInputStream()

            val text = onSendTextBytes()

            out.write(onSendTypeResponse())
            out.write(text.size)
            out.write(text)
            out.write(onSendBytes())
            out.flush()

            val inputData = NetworkUtils
                .readBytes(inp, buffer)

            Log.d("Connectable:", "connectToHost: SIZE: ${inputData.size}")

            inp.close()
            out.close()
            socket.close()

            input(inputData)

        }.start()
    }
}