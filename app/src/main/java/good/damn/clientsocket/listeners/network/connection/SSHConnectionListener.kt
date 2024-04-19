package good.damn.clientsocket.listeners.network.connection

interface SSHConnectionListener {

    fun onDebugConnection(
        msg: String
    )

    fun onStartConnection()
    fun onCredentials(): String
    fun keyRSA(): ByteArray
    fun onCommandArgs(): Array<String>
    fun onResponse(
        response: ByteArray
    )
    fun onEndConnection()
}