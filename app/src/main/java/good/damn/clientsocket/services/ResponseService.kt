package good.damn.clientsocket.services

import android.util.Log
import good.damn.clientsocket.utils.ByteUtils
import kotlin.math.log

class ResponseService {

    companion object {
        private const val TAG = "ResponseService"

        private const val RESPONSE_ID_LIST = 280
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