package good.damn.clientsocket.utils

import good.damn.clientsocket.Application
import java.security.MessageDigest

class CryptoUtils {
    companion object {

        private val mDigestSha256 = MessageDigest
            .getInstance("SHA-256")

        fun sha256(
            input: String
        ): ByteArray {
            mDigestSha256.reset()
            return mDigestSha256.digest(
                input.toByteArray(
                    Application.CHARSET_ASCII
                )
            )
        }
    }
}