package good.damn.clientsocket.network

import android.util.Log
import good.damn.clientsocket.listeners.network.connection.ConnectionListener
import good.damn.clientsocket.services.BaseService
import good.damn.clientsocket.utils.NetworkUtils
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.charset.Charset

abstract class BaseConnection<DELEGATE>(
    val hostIp: String,
    val port: Int
) {
    fun start(
        delegate: DELEGATE
    ) {
        onStartConnection(
            delegate
        )
    }

    abstract fun onStartConnection(
        delegate: DELEGATE
    )
}