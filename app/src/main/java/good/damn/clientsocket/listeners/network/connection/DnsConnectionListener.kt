package good.damn.clientsocket.listeners.network.connection

import androidx.annotation.WorkerThread

interface DnsConnectionListener {

    fun onRequestDomain(): String

    fun onRawResponse(
        response: ByteArray
    )

    fun onDebugResponse(
        response: String
    )

    fun onGetIP(
        domain: String,
        ip: String
    )

}