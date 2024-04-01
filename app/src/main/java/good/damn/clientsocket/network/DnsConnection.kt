package good.damn.clientsocket.network

import android.os.Handler
import android.os.Looper
import android.util.Log
import good.damn.clientsocket.utils.ByteUtils
import good.damn.clientsocket.utils.NetworkUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.EOFException
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

            val socket = DatagramSocket()

            /*val request = byteArrayOf(
                0,15, // 2b - ID
                0,0, // 16(-bit) flags
                0,0, // 2b - Number of questions
                0,0, // 2b - number of answers
                0,0, // 2b - number of authority RRs
                0,0  // 2b - number of additional RRs
            )*/

            val baos = ByteArrayOutputStream()
            val dos = DataOutputStream(baos)

            val toAddress = InetAddress
                .getByName(mHost)

            dos.writeShort(0x0015) // ID
            dos.writeShort(0x0100) // Flags
            dos.writeShort(0x0001) // Number of questions
            dos.writeShort(0x0000) // number of answers
            dos.writeShort(0x0000) // number of authority RRs
            dos.writeShort(0x0000) // number of additional RRs

            val domainParts = domain.split("\\.")
            Log.d(TAG, "connect: DOMAIN $domain WITH ${domainParts.size} portions")
            for (part in domainParts) {
                dos.write(part.length)
                dos.write(part.toByteArray(mCharset)) // UTF-8
            }

            dos.writeByte(0x00) // No more parts
            dos.writeShort(0x0001) // Host request (Type = A)
            dos.writeShort(0x0001) // Class 0x01 = IN

            val requestBytes = baos.toByteArray()
            baos.close()

            val packet = DatagramPacket(
                requestBytes,
                0,
                requestBytes.size,
                toAddress,
                53 // DNS-port
            )

            Log.d(TAG, "connect: UDP send ${toAddress.hostName} $mHost")

            socket.send(packet)

            val receiveBuffer = ByteArray(1024)

            val receivePacket = DatagramPacket(
                receiveBuffer,
                receiveBuffer.size
            )

            Log.d(TAG, "connect: UDP-DNS Receive")

            socket.receive(
                receivePacket
            )

            Log.d(TAG, "connect: UDP-DNS RECEIVED: PROCESSING ${receiveBuffer.contentToString()}")

            val inp = DataInputStream(
                ByteArrayInputStream(
                    receiveBuffer
                )
            )

            val requestID = inp.readShort()
            val flags = inp.readShort()
                .toInt()

            val questions = inp.readShort()
            val answers = inp.readShort()
            val auth = inp.readShort()
            val additional = inp.readShort()

            var records = ""

            var recordLen: Byte
            while(true) {
                recordLen = inp.readByte()

                if (recordLen <= 0) {
                    break
                }

                val recBytes = ByteArray(recordLen.toInt())
                inp.read(recBytes)

                records += "RECORD: ${String(recBytes,mCharset)}\n"
            }

            val recordType = inp.readShort()
            val classs = inp.readShort()

            val field = inp.readShort()
            val type = inp.readShort()
            val classType = inp.readShort()
            val ttl = inp.readInt()

            val addressLen = inp.readShort()

            var addressString = ""

            Log.d(TAG, "connect: ADDRESS_LEN: $addressLen")
            
            for (i in 0 until addressLen) {
                try {
                    addressString += "${inp.readByte().toInt() and 0xff}."
                } catch (ex: java.lang.Exception) {
                    Log.d(TAG, "connect: EXCEPTION: $ex")
                    break
                }
            }

            inp.close()
            
            val responseDNS = """
                REQUEST_ID: $requestID
                FLAGS: ${Integer.toBinaryString(flags).substring(16)}
                QUESTIONS: $questions
                ANSWERS: $answers
                AUTHORITY: $auth
                ADDITIONAL: $additional
                $records
                RECORD_TYPE: $recordType
                CLASS: $classs
                FIELD: $field
                TYPE: $type
                CLASS_TYPE: $classType
                TIME TO LIVE: $ttl
                IPv4: $addressString
            """.trimIndent()
            
            mainThread.run {
                onGetDomainIPPort(
                    responseDNS,
                    0
                )
            }

            Thread.currentThread()
                .interrupt()
        }.start()
    }

}