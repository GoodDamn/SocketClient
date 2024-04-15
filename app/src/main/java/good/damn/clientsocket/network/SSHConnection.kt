package good.damn.clientsocket.network

import android.util.Log
import good.damn.clientsocket.Application
import good.damn.clientsocket.listeners.network.connection.SSHConnectionListener
import good.damn.clientsocket.utils.CryptoUtils
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class SSHConnection(
    hostIp: String,
    port: Int,
    private val mBuffer: ByteArray
): BaseConnection<SSHConnectionListener>(
    hostIp,
    port
) {

    companion object {
        private const val TAG = "SSHConnection"
    }

    override fun onStartConnection(
        delegate: SSHConnectionListener
    ) {
        val credentials = CryptoUtils
            .sha256(
                delegate.onCredentials()
            )

        val req = delegate
            .onCommand()
            .toByteArray(
                Application.CHARSET_ASCII
            )

        val data = byteArrayOf(
            credentials.size.toByte()
        ) + credentials + byteArrayOf(
            req.size.toByte()
        ) + req

        Thread {
            send(data)
            receive(
                delegate
            )

            Thread.currentThread()
                .interrupt()
        }.start()
    }

    private fun receive(
        delegate: SSHConnectionListener
    ) {
        val socket = DatagramSocket(
            55555
        )

        val receive = DatagramPacket(
            mBuffer,
            mBuffer.size
        )

        Log.d(TAG, "receive: WAITING_RESPONSE")
        socket.receive(
            receive
        )

        Log.d(TAG, "receive: ON_RESPONSE: ${mBuffer.contentToString()}")
        delegate.onResponse(
            mBuffer
        )

        socket.close()
    }

    private fun send(
        data: ByteArray
    ) {

        val socket = DatagramSocket()

        val packet = DatagramPacket(
            data,
            data.size,
            InetAddress.getByName(
                hostIp
            ),
            port
        )

        socket.send(
            packet
        )

        socket.close()
    }

}