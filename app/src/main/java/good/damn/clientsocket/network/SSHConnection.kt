package good.damn.clientsocket.network

import android.util.Log
import good.damn.clientsocket.Application
import good.damn.clientsocket.listeners.network.connection.SSHConnectionListener
import good.damn.clientsocket.utils.ByteUtils
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

    private var mSocketReceive: DatagramSocket? = null
    private var mSocketSend: DatagramSocket? = null

    override fun onStartConnection(
        delegate: SSHConnectionListener
    ) {
        delegate.onStartConnection()
        mSocketReceive?.close()
        mSocketSend?.close()

        val req = delegate
            .onCommandArgs()

        if (req.isEmpty() || req[0].isEmpty()) {
            delegate.onDebugConnection(
                "Command with args are emtpy"
            )
            return
        }

        val credentials = CryptoUtils
            .sha256(
                delegate.onCredentials()
            )

        val rsaKey = delegate.keyRSA()

        val reqBytes = req
            .flatMap {
                val s = it.toByteArray(
                    Application.CHARSET_ASCII
                )
                (byteArrayOf(
                    s.size.toByte()
                ) + s).asIterable()
            }

        var data = if (rsaKey.isNotEmpty())
                byteArrayOf(
                    1
                ) + ByteUtils.short(rsaKey.size) + rsaKey
            else ByteArray(0)

        data += byteArrayOf(
            credentials.size.toByte()
        ) + credentials + byteArrayOf(
            req.size.toByte()
        ) + reqBytes

        delegate.onDebugConnection(
            "Sending data"
        )

        Thread {
            delegate.onDebugConnection(
                "Waiting response"
            )

            receive(
                delegate
            )
            Thread.currentThread()
                .interrupt()
        }.start()

        Thread {
            send(data)
            delegate.onDebugConnection(
                "Data sent"
            )
            Thread.currentThread()
                .interrupt()
        }.start()
    }

    private fun receive(
        delegate: SSHConnectionListener
    ) {
        mSocketReceive = DatagramSocket(
            55555
        )

        val receive = DatagramPacket(
            mBuffer,
            mBuffer.size
        )

        Log.d(TAG, "receive: WAITING_RESPONSE")
        mSocketReceive?.receive(
            receive
        )

        Log.d(TAG, "receive: ON_RESPONSE: ${mBuffer[0]}")
        delegate.onResponse(
            mBuffer
        )

        mSocketReceive?.close()
        mSocketReceive = null
    }

    private fun send(
        data: ByteArray
    ) {

        mSocketSend = DatagramSocket()

        val packet = DatagramPacket(
            data,
            data.size,
            InetAddress.getByName(
                hostIp
            ),
            port
        )

        mSocketSend?.send(
            packet
        )

        mSocketSend?.close()
        mSocketSend = null
    }

}