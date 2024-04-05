package good.damn.clientsocket.shareProtocol

import good.damn.clientsocket.Application
import good.damn.clientsocket.utils.ByteUtils

class ShareRequestBodyArgs(
    args: List<String>,
    offset: Int = 0
): ShareRequestBody(
    ByteUtils.stringList(
        args,
        Application.CHARSET_ASCII,
        offset
    )
)