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

    /*private fun input(
        data: ByteArray
    ): Boolean {

        if (data.size < 2) {
            return false
        }

        val charset = Charset
            .forName("UTF-8")

        when (data[0].toInt()) {
            1 -> { // F (File)
                val nameSize = data[1].toUByte()
                val fileName = String(data,
                    2,
                    nameSize.toInt(),
                    charset)

                onGetFile(
                    data,
                    nameSize.toInt()+2,
                    fileName)
            }
            2 -> { // T (Text)
                val msgSize = data[1].toUByte()
                val msg = String(data,
                    2,
                    msgSize.toInt(),
                    charset)
                onGetText(msg)
            }

            71 -> { // G (GET) http
                onHttpGet()
            }
        }

        return true
    }*/

    abstract fun onStartConnection(
        delegate: DELEGATE
    )
}