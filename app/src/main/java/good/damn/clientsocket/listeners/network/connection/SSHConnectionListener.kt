package good.damn.clientsocket.listeners.network.connection

interface SSHConnectionListener {
    fun onCredentials(): String
    fun keyRSA(): ByteArray
    fun onCommandArgs(): Array<String>
    fun onResponse(
        response: ByteArray
    )
}