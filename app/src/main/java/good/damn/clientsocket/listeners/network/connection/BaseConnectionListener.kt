package good.damn.clientsocket.listeners.network.connection

import androidx.annotation.WorkerThread

interface BaseConnectionListener {

    @WorkerThread
    fun onRequest(): ByteArray

    @WorkerThread
    fun onResponse(
        response: ByteArray
    )
}