package good.damn.clientsocket.listeners.network.connection

interface DhcpConnectionListener {

    fun onRequest(): ByteArray

}