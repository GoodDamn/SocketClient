package good.damn.clientsocket.network

import android.util.Log
import good.damn.clientsocket.Application
import good.damn.clientsocket.listeners.network.connection.ConnectionListener
import good.damn.clientsocket.utils.NetworkUtils
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException

class ShareConnection(
    hostIp: String
): BaseConnection<ConnectionListener>(
    hostIp,
    8080
) {

    companion object {
        private const val TAG = "ShareConnection"

        fun observe(
            delegate: ConnectionListener,
            out: OutputStream,
            inp: InputStream
        ) {
            out.write(delegate.onRequest())
            out.flush()

            val inputData = NetworkUtils
                .readBytes(
                    inp,
                    Application.BUFFER_MB
                )

            Log.d(TAG, "observe: SIZE: ${inputData.size}")

            inp.close()
            out.close()

            delegate.onResponse(
                inputData
            )
        }

    }

    override fun onStartConnection(
        delegate: ConnectionListener
    ) {
            Thread {
                try {
                    val socket = Socket()
                    val address = InetSocketAddress(
                        hostIp,
                        port
                    )

                    socket.connect(
                        address,
                        5000
                    )

                    delegate.onConnected(socket)

                    val out = socket.getOutputStream()
                    val inp = socket.getInputStream()

                    observe(
                        delegate,
                        out,
                        inp
                    )

                } catch (e: java.lang.Exception) {
                    delegate.onDebugMessage(
                        e.message ?: "Exception"
                    )
                }

                Thread.currentThread()
                    .interrupt()
            }.start()
    }
}