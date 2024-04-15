package good.damn.clientsocket.listeners.network.connection

interface SSHConnectionListener {
    fun onCredentials(): String
    fun onCommand(): String
    fun onResponse(
        response: ByteArray
    )
}