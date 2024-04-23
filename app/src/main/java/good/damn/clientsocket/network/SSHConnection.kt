package good.damn.clientsocket.network

import android.util.Log
import good.damn.clientsocket.Application
import good.damn.clientsocket.factory.SocketFactory
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
        const val PORT_LOOP = 55556
        const val PORT_RECEIVE = 55555

        private val mSocketReceiveLoop = DatagramSocket(
            PORT_LOOP
        )

        private val mSocketReceive = DatagramSocket(
            PORT_RECEIVE
        )
    }

    private val mBufferLoop = ByteArray(255)

    private var mSocketSend: DatagramSocket? = null

    override fun onStartConnection(
        delegate: SSHConnectionListener
    ) {
        delegate.onStartConnection()

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
            delegate.onDebugConnection(
                "RECEIVE_LOOP"
            )
            receiveLoop(
                delegate
            )
        }.start()

        Thread {
            send(data)
            delegate.onDebugConnection(
                "Data sent"
            )
        }.start()
    }

    private fun receive(
        delegate: SSHConnectionListener
    ) {

        val receive = DatagramPacket(
            mBuffer,
            mBuffer.size
        )

        Log.d(TAG, "receive: WAITING_RESPONSE")
        mSocketReceive.receive(
            receive
        )

        delegate.onResponse(
            mBuffer
        )
    }

    private fun receiveLoop(
        delegate: SSHConnectionListener
    ) {
        val packet = DatagramPacket(
            mBufferLoop,
            mBufferLoop.size
        )

        while (true) {
            Log.d(TAG, "receiveLoop: SOCKET: $mSocketReceiveLoop")
            mSocketReceiveLoop.receive(
                packet
            )

            Log.d(TAG, "receiveLoop: CHECK: ${mBufferLoop[0]}")
            
            if (mBufferLoop[0].toInt() == -1) {
                Log.d(TAG, "receiveLoop: END")
                delegate.onStreamResponse(
                    "EOS"
                )
                // eop (end of packet)
                break
            }

            Log.d(TAG, "receiveLoop: RESPONSE")
            delegate.onStreamResponse(
                String(
                    mBufferLoop,
                    Application.CHARSET
                )
            )

        }

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