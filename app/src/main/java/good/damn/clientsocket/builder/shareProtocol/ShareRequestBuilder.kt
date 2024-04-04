package good.damn.clientsocket.builder.shareProtocol

import good.damn.clientsocket.shareProtocol.ShareRequestMethod
import good.damn.clientsocket.shareProtocol.ShareRequest
import good.damn.clientsocket.shareProtocol.ShareRequestBody

class ShareRequestBuilder {

    private var method: ShareRequestMethod? = null
    private var mShareRequestBody: ShareRequestBody? = null

    fun setBody(
        body: ShareRequestBody
    ): ShareRequestBuilder {
        mShareRequestBody = body
        return this
    }

    fun setMethod(
        shareMethod: ShareRequestMethod
    ): ShareRequestBuilder {
        method = shareMethod
        return this
    }

    fun build(): ShareRequest? {
        if (method == null) {
            return null
        }

        if (mShareRequestBody == null) {
            return null
        }

        return ShareRequest(
            method!!.toByteArray(),
            mShareRequestBody!!.toByteArray()
        )
    }

}