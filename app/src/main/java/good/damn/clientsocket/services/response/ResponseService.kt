package good.damn.clientsocket.services.response

import android.util.Log
import good.damn.clientsocket.utils.ByteUtils
import java.nio.charset.Charset

class ResponseService {

    companion object {
        private const val TAG = "ResponseService"
        private const val RESPONSE_ID_LIST = 426
        private val CHARSET_ASCII = Charset.forName(
            "US-ASCII"
        )
    }

    private val mResponses: HashMap<
        Int,
        ((ByteArray)->Unit)
    > = HashMap()

    init {
        mResponses[RESPONSE_ID_LIST] = {
            val count = it[4]
            var position = 5

            for (i in 0 until count) {
                val fileNameLength = it[position].toInt()
                position++

                val fileName = String(
                    it,
                    position,
                    fileNameLength,
                    CHARSET_ASCII
                )
                position += fileNameLength
                Log.d(TAG, "FILE_NAME_LIST: $fileName")
            }

        }
    }

    fun decodeResponse(
        response: ByteArray
    ) {
        val responseID = ByteUtils
            .integer(
                response,
                0
            )
        Log.d(TAG, "decodeResponse: $responseID ")
        mResponses[responseID]?.let {
            it(response)
        }
    }
}