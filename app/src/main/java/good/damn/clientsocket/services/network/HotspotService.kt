package good.damn.clientsocket.services.network

import android.content.Context
import android.net.*
import android.net.wifi.WifiManager
import android.util.Log
import good.damn.clientsocket.listeners.service.network.HotspotServiceListener
import good.damn.clientsocket.services.BaseService
import good.damn.clientsocket.utils.ByteUtils
import java.net.InetAddress
import java.nio.ByteOrder

@Deprecated("dhcpInfo of WifiManager class is deprecated")
class HotspotService(
    context: Context
): BaseService(context) {

    companion object {
        private const val TAG = "HotspotService"
    }

    var delegate: HotspotServiceListener? = null

    private val mWifiManager: WifiManager

    init {
        // may causes memory leak
        mWifiManager = context.applicationContext.getSystemService(
           Context.WIFI_SERVICE
        ) as WifiManager
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun start() {

        val dhcp = mWifiManager.dhcpInfo
        val ipDhcp = dhcp.gateway

        if (ipDhcp == 0) {
            delegate?.onGetHotspotIP(
                UByteArray(0)
            )
            return
        }

        val ip = ByteUtils.integer(
            if (ByteOrder.nativeOrder()
                    .equals(ByteOrder.LITTLE_ENDIAN)
            ) Integer.reverseBytes(ipDhcp)
            else ipDhcp
        )

        delegate?.onGetHotspotIP(
            ip
        )
    }

}