package good.damn.clientsocket.utils

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset

class ByteUtils {

    companion object {

        fun stringList(
            list: List<String>,
            charset: Charset,
            offset: Int = 0
        ): ByteArray {
            val baos = ByteArrayOutputStream()

            val factCount = list.size - offset

            if (factCount == 0) {
                return ByteArray(0)
            }

            baos.write( // List count (0-255)
                factCount
            )

            for (i in offset until list.size) {
                val bytes = list[i].toByteArray(
                    charset
                )

                baos.write( // Length (0-255)
                    bytes.size
                )

                baos.write( // Content
                    bytes
                )
            }

            val result = baos.toByteArray()
            baos.close()

            return result
        }

        fun short(
            buf: ByteArray,
            off: Int
        ): Int {
            return (buf[off].toInt() and 0xff shl 8) or
            (buf[off+1].toInt() and 0xff)
        }

        fun short(i: Int): ByteArray {
            return byteArrayOf(
                ((i shr 8) and 0xff).toByte(),
                (i and 0xff).toByte()
            )
        }

        fun integer(i: Int): ByteArray {
            return byteArrayOf(
                ((i shr 24) and 0xff).toByte(),
                (((i shr 16) and 0xff).toByte()),
                ((i shr 8) and 0xff).toByte(),
                (i and 0xff).toByte()
            )
        }

        fun integer(
            buf: ByteArray,
            off: Int = 0
        ): Int {
            return (buf[off].toInt() and 0xff shl 24) or
                    (buf[off+1].toInt() and 0xff shl 16) or
                    (buf[off+2].toInt() and 0xff shl 8) or
                    (buf[off+3].toInt() and 0xff)
        }

    }
}