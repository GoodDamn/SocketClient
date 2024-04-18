package good.damn.clientsocket.network

import good.damn.clientsocket.listeners.network.connection.ConnectionListener
import javax.net.ssl.SSLSocketFactory

class SSLShareConnection(
    hostIp: String
): BaseConnection<ConnectionListener>(
    hostIp,
    443
) {

    override fun onStartConnection(
        delegate: ConnectionListener
    ) {
        Thread {

            val factory = SSLSocketFactory
                .getDefault()

            val socket = factory.createSocket(
                hostIp,
                port
            )

            val inp = socket.getInputStream()
            val out = socket.getOutputStream()

            ShareConnection.observe(
                delegate, out, inp
            )

            socket.close()
            Thread.currentThread()
                .interrupt()
        }.start()
    }

}