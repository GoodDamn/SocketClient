package good.damn.clientsocket.shareProtocol

class ShareRequest(
    private val method: ByteArray,
    private val body: ByteArray
) {
    fun toByteArray(): ByteArray {
        return method.plus(
            body
        )
    }
}