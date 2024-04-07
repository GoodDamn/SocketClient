package good.damn.clientsocket.network

import android.util.Log
import good.damn.clientsocket.Application
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class DhcpConnection
: BaseConnection<Any>(
    "192.168.31.255",
    68
) {

    companion object {
        private const val TAG = "DhcpConnection"
    }

    override fun onStartConnection(
        delegate: Any
    ) {

        Thread {

            val buf = Application.BUFFER_300
            val addr = InetAddress.getByName(
                hostIp
            )

            val socket = DatagramSocket()
            val packet = DatagramPacket(
                buf,
                buf.size,
                addr,
                port
            )

            Log.d(TAG, "onStartConnection: SEND_TO_DHCP:")
            socket.send(
                packet
            )

            val receivePacket = DatagramPacket(
                buf,
                buf.size
            )

            Log.d(TAG, "onStartConnection: WAITING_DHCP_RESPONSE:")
            socket.receive(
                receivePacket
            )

            Log.d(TAG, "onStartConnection: SOCKET_RECEIVE: ${buf.contentToString()}")

            Thread.currentThread()
                .interrupt()
        }.start()

    }
}