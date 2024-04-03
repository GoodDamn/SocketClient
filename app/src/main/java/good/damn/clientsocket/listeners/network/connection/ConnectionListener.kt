package good.damn.clientsocket.listeners.network.connection

import androidx.annotation.WorkerThread
import java.net.Socket
import java.nio.charset.Charset

interface ConnectionListener
: BaseConnectionListener {

    @WorkerThread
    fun onConnected(
        socket: Socket
    )

}