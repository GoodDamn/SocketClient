package good.damn.clientsocket.shareProtocol

import good.damn.clientsocket.Application

class ShareRequestBodyArgs(
    args: List<String>,
    offset: Int = 0
): ShareRequestBody(
    args[offset].toByteArray(
        Application.CHARSET_ASCII
    )
)