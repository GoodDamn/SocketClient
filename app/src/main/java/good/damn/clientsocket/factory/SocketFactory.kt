package good.damn.clientsocket.factory

import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress

class SocketFactory {

    companion object {

        fun createDatagram(
            reuse: Boolean = false,
            port: Int
        ): DatagramSocket {

            val socket = DatagramSocket()
            socket.reuseAddress = reuse
            val soc = InetSocketAddress(port)
            if (!socket.isBound) {
                socket.bind(soc)
            }
            return socket
        }

    }

}