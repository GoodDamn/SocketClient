package good.damn.clientsocket.listeners.network.service

interface HotspotServiceListener {

    fun onGetHotspotIP(
        ip: ByteArray
    )

}