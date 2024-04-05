package good.damn.clientsocket.shareProtocol

data class ShareModelFile(
    val fileSize: Int,
    val file: ByteArray,
    val filePosition: Int
)