package good.damn.clientsocket.network

import android.os.Handler
import android.os.Looper
import android.util.Log
import good.damn.clientsocket.utils.ByteUtils
import good.damn.clientsocket.utils.NetworkUtils
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.Inet4Address
import java.net.InetAddress
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import kotlin.math.log

class DnsConnection(
    host: String,
    port: Int
) {
    private val mHost = host
    private val mPort = port
    private val mCharset = Charset.forName("UTF-8")
    private val mIPBuffer = ByteArray(8)
    private val mainThread = Handler(Looper.getMainLooper())

    companion object {
        private const val TAG = "DnsConnection"
    }

    fun connect(
        domain: String,
        onGetDomainIPPort: (String, Int) -> Unit
    ) {

        Thread {
            val baDomain = domain
                .toByteArray(mCharset)

            val socket = DatagramSocket()

            val request = byteArrayOf(
                0,15, // 2b - ID
                0,0, // 16(-bit) flags
                0,0, // 2b - Number of questions
                0,0, // 2b - number of answers
                0,0, // 2b - number of authority RRs
                0,0  // 2b - number of additional RRs
            )

            val toAddress = InetAddress
                .getByName(mHost)

            val packet = DatagramPacket(
                request,
                0,
                request.size,
                toAddress,
                53 // DNS-port
            )

            Log.d(TAG, "connect: UDP send ${toAddress.hostName} $mHost")

            socket.send(packet)

            val receiveBuffer = ByteArray(4096)

            val receivePacket = DatagramPacket(
                receiveBuffer,
                receiveBuffer.size
            )

            Log.d(TAG, "connect: UDP-DNS Receive")

            socket.receive(
                receivePacket
            )

            Log.d(TAG, "connect: UDP-DNS RECEIVED: PROCESSING")

            val flag1 = Integer.toBinaryString(
                receiveBuffer[2].toInt()
            ).substring(24)
            val flag2 = Integer.toBinaryString(
                receiveBuffer[3].toInt()
            ).substring(24)

            Log.d(TAG, "connect: UDP-DNS FLAGS: $flag1 $flag2")

            mainThread.run {

            }

            Thread.currentThread()
                .interrupt()
        }.start()
    }

}