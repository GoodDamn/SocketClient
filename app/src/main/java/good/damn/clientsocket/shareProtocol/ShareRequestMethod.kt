package good.damn.clientsocket.shareProtocol

import java.nio.charset.Charset

class ShareRequestMethod(
    private val method: String
): ShareByteArray() {

    override fun toByteArray(): ByteArray {
        return method.toByteArray(
            Charset.forName("US-ASCII")
        )
    }

}