package good.damn.clientsocket.listeners.service.network

interface HotspotServiceListener {

    @OptIn(ExperimentalUnsignedTypes::class)
    fun onGetHotspotIP(
        ip: UByteArray
    )

}