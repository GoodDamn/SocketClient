package good.damn.clientsocket.services.response

import android.util.Log
import good.damn.clientsocket.utils.ByteUtils

class ResponseService {

    companion object {
        private const val TAG = "ResponseService"
        private const val RESPONSE_ID_LIST = 426
    }

    private val mResponses: HashMap<
        Int,
        ((ByteArray)->Unit)
    > = HashMap()

    init {
        mResponses[RESPONSE_ID_LIST] = {
            Log.d(TAG, "RESPONSE_LIST: ${it.contentToString()}")
        }
    }

    fun decodeResponse(
        response: ByteArray
    ) {
        mResponses[ByteUtils
            .integer(response,0)
        ]?.let {
            it(response)
        }
    }
}