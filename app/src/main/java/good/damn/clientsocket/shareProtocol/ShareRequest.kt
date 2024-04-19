package good.damn.clientsocket.shareProtocol

class ShareRequest(
    private val method: ByteArray,
    private val body: ByteArray
) {
    companion object {
        private const val SHARE_PROTOCOL: Byte = 0
    }

    fun toByteArray(): ByteArray {
        return byteArrayOf(
            SHARE_PROTOCOL,
            method.size.toByte()
        ) + method + body
    }
}