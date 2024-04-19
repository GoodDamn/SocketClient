package good.damn.clientsocket.services.response

import android.util.Log
import good.damn.clientsocket.Application
import good.damn.clientsocket.listeners.network.service.ResponseServiceListener
import good.damn.clientsocket.shareProtocol.ShareModelFile
import good.damn.clientsocket.shareProtocol.ShareModelListString
import good.damn.clientsocket.utils.ByteUtils
import good.damn.clientsocket.utils.FileUtils
import java.nio.charset.Charset
import java.util.LinkedList

class ResponseService {

    companion object {
        private const val TAG = "ResponseService"
        private const val RESPONSE_ID_LIST = 426
        private const val RESPONSE_ID_GET_FILE = 410
        private const val RESPONSE_ID_MESSAGE = 1
        private const val RESPONSE_ID_MESSAGE16 = 2
    }

    var delegate: ResponseServiceListener? = null

    private val mResponses: HashMap<
        Int,
        ((ByteArray)->Unit)
    > = HashMap()

    init {

        mResponses[RESPONSE_ID_MESSAGE16] = {
            val msgLen = ByteUtils.short(
                it,
                4
            )

            delegate?.onModelResponse(
                String(
                    it,
                    6,
                    msgLen,
                    Application.CHARSET_ASCII
                )
            )
        }

        mResponses[RESPONSE_ID_MESSAGE] = {
            val msgLen = it[4]
                .toInt()

            delegate?.onModelResponse(
                String(
                    it,
                    5,
                    msgLen,
                    Application.CHARSET_ASCII
                )
            )
        }

        mResponses[RESPONSE_ID_GET_FILE] = {
            val fileSize = ByteUtils
                .integer(it,4)

            delegate?.onModelResponse(
                ShareModelFile(
                    fileSize,
                    it,
                    8
                )
            )
        }

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
                    Application.CHARSET_ASCII
                )
                position += fileNameLength

                fileNames.add(
                    fileName
                )

                Log.d(TAG, "FILE_NAME_LIST: $fileName")
            }

            delegate?.onModelResponse(
                ShareModelListString(
                    fileNames
                )
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