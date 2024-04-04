package good.damn.clientsocket.services.response

import android.util.Log
import good.damn.clientsocket.listeners.network.service.ResponseServiceListener
import good.damn.clientsocket.utils.ByteUtils
import java.nio.charset.Charset
import java.util.LinkedList

class ResponseService {

    companion object {
        private const val TAG = "ResponseService"
        private const val RESPONSE_ID_LIST = 426
        private val CHARSET_ASCII = Charset.forName(
            "US-ASCII"
        )
    }

    var delegate: ResponseServiceListener? = null

    private val mResponses: HashMap<
        Int,
        ((ByteArray)->Unit)
    > = HashMap()

    init {
        mResponses[RESPONSE_ID_LIST] = {
            val count = it[4].toInt()
            var position = 5

            val fileNames = ArrayList<String>(
                count
            )

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

                fileNames.add(
                    fileName
                )

                Log.d(TAG, "FILE_NAME_LIST: $fileName")
            }

            delegate?.onModelResponse(
                fileNames
            )
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