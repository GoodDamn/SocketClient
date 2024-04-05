package good.damn.clientsocket.network

import android.util.Log
import good.damn.clientsocket.Application
import good.damn.clientsocket.listeners.network.connection.ConnectionListener
import good.damn.clientsocket.utils.NetworkUtils
import java.net.InetSocketAddress
import java.net.Socket

class OwnConnection(
    hostIp: String
): BaseConnection<ConnectionListener>(
    hostIp,
    8080
) {

    override fun onStartConnection(
        delegate: ConnectionListener
    ) {
        Thread {
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

            out.write(delegate.onRequest())
            out.flush()

            val inputData = NetworkUtils
                .readBytes(
                    inp,
                    Application.BUFFER_MB
                )

            Log.d("Connectable:", "connectToHost: SIZE: ${inputData.size}")

            inp.close()
            out.close()
            socket.close()

            delegate.onResponse(
                inputData
            )

            Thread.currentThread()
                .interrupt()
        }.start()
    }
}