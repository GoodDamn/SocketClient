package good.damn.clientsocket.listeners.service.network

interface HotspotServiceListener {

    fun onGetHotspotIP(
        ip: ByteArray
    )

}