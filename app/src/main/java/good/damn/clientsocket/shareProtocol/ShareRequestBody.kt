package good.damn.clientsocket.shareProtocol

open class ShareRequestBody(
    private val content: ByteArray
): ShareByteArray() {

    override fun toByteArray(): ByteArray {
        return content
    }

}