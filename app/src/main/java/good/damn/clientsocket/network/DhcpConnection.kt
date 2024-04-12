package good.damn.clientsocket.network

import android.util.Log
import good.damn.clientsocket.Application
import good.damn.clientsocket.listeners.network.connection.DhcpConnectionListener
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class DhcpConnection
: BaseConnection<DhcpConnectionListener>(
    "255.255.255.255",
    8080
) {

    companion object {
        private const val TAG = "DhcpConnection"
    }

    override fun onStartConnection(
        delegate: DhcpConnectionListener
    ) {

        Thread {
            val addr = InetAddress.getByName(
                hostIp
            )

            val data = delegate.onRequest()

            val socket = DatagramSocket()
            socket.broadcast = true
            val packet = DatagramPacket(
                data,
                data.size,
                addr,
                port
            )

            Log.d(TAG, "onStartConnection: SEND_TO_DHCP:")
            socket.send(
                packet
            )

            socket.close()
            /*val receivePacket = DatagramPacket(
                buf,
                buf.size
            )

            Log.d(TAG, "onStartConnection: WAITING_DHCP_RESPONSE:")
            socket.receive(
                receivePacket
            )

            Log.d(TAG, "onStartConnection: SOCKET_RECEIVE: ${buf.contentToString()}")

            socket.close()*/
            Thread.currentThread()
                .interrupt()
        }.start()

    }
}