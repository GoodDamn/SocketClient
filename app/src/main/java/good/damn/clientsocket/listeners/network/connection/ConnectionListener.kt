package good.damn.clientsocket.listeners.network.connection

import androidx.annotation.WorkerThread
import java.net.Socket
import java.nio.charset.Charset

interface ConnectionListener {

    @WorkerThread
    fun onConnected(
        socket: Socket
    )

    @WorkerThread
    fun onRequest(): ByteArray

    @WorkerThread
    fun onResponse(
        response: ByteArray
    )
}