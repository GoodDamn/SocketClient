package good.damn.clientsocket.utils

class ByteUtils {

    companion object {
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
            off: Int
        ): Int {
            return (buf[off].toInt() and 0xff shl 24) or
                    (buf[off+1].toInt() and 0xff shl 16) or
                    (buf[off+2].toInt() and 0xff shl 8) or
                    (buf[off+3].toInt() and 0xff)
        }

    }
}