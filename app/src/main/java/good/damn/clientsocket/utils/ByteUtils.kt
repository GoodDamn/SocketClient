package good.damn.clientsocket.utils

class ByteUtils {

    companion object {
        val TAG = "ByteUtils"

        fun integer(i: Int): UByteArray {
            return ubyteArrayOf(
                ((i shr 24) and 0xff).toUByte(),
                ((i shr 16) and 0xff).toUByte(),
                ((i shr 8) and 0xff).toUByte(),
                (i and 0xff).toUByte()
            )
        }

        fun integer(
            buf: ByteArray,
            off: Int
        ): Int {
            return (buf[off].toInt() and 0xff) or
                    ((buf[off+1].toInt() shr 8)) or
                    ((buf[off+2].toInt() shr 16)) or
                    ((buf[off+3].toInt() shr 24))
        }

    }
}