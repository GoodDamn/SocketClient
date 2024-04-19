package good.damn.clientsocket.network

import good.damn.clientsocket.listeners.network.connection.ConnectionListener
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class SSLShareConnection(
    hostIp: String
): BaseConnection<ConnectionListener>(
    hostIp,
    4443
) {

    override fun onStartConnection(
        delegate: ConnectionListener
    ) {
        Thread {

            val socket = SSLContext
                .getDefault()
                .socketFactory
                .createSocket(
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