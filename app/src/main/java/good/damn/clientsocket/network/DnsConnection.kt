package good.damn.clientsocket.network

import android.os.Handler
import android.os.Looper
import android.util.Log
import good.damn.clientsocket.Application
import good.damn.clientsocket.listeners.network.connection.DnsConnectionListener
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
    host: String
): BaseConnection<DnsConnectionListener>(
    host,
    53
) {
    companion object {
        private const val TAG = "DnsConnection"
    }

    override fun onStartConnection(
        delegate: DnsConnectionListener
    ) {
        Thread {
            val socket = DatagramSocket()

            val baos = ByteArrayOutputStream()
            val dos = DataOutputStream(baos)

            val toAddress = InetAddress
                .getByName(hostIp)

            dos.writeShort(0x0015) // ID
            dos.writeShort(0x0100) // Flags
            dos.writeShort(0x0001) // Number of questions
            dos.writeShort(0x0000) // number of answers
            dos.writeShort(0x0000) // number of authority RRs
            dos.writeShort(0x0000) // number of additional RRs

            val domain = delegate.onRequestDomain()
            val domainParts = domain.split("\\.".toRegex())
            Log.d(TAG, "connect: DOMAIN $domain WITH ${domainParts.size} portions")
            for (part in domainParts) {
                dos.write(part.length)
                dos.write(part.toByteArray(
                    Application.CHARSET
                )) // UTF-8
            }

            dos.writeByte(
                0x00 // No more parts
            )
            dos.writeShort(
                0x0001  // Host request (Type = A)
            )
            dos.writeShort(
                0x0001  // Class 0x01 = IN
            )

            val requestBytes = baos.toByteArray()
            baos.close()

            val packet = DatagramPacket(
                requestBytes,
                0,
                requestBytes.size,
                toAddress,
                port
            )

            Log.d(TAG, "connect: UDP send ${toAddress.hostName} $hostIp")

            socket.send(
                packet
            )

            val receiveBuffer = ByteArray(1024)

            val receivePacket = DatagramPacket(
                receiveBuffer,
                receiveBuffer.size
            )

            Log.d(TAG, "connect: UDP-DNS Receive")

            socket.receive(
                receivePacket
            )

            delegate.onRawResponse(
                receiveBuffer
            )

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
            while (true) {
                recordLen = inp.readByte()

                if (recordLen <= 0) {
                    break
                }

                val recBytes = ByteArray(recordLen.toInt())
                inp.read(recBytes)

                records += "\nRECORD: ${String(
                    recBytes,
                    Application.CHARSET
                )}"
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
                } catch (ex: EOFException) {
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

            delegate.onDebugResponse(
                responseDNS
            )

            Application.ui {
                delegate.onGetIP(
                    domain,
                    addressString
                )
            }

            Thread.currentThread()
                .interrupt()
        }.start()
    }

}