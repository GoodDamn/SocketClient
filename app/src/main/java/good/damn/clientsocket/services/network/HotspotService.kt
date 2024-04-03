package good.damn.clientsocket.services.network

import android.content.Context
import android.net.*
import android.net.wifi.WifiManager
import android.util.Log
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

    private val mWifiManager: WifiManager

    init {
        // may causes memory leak
        mWifiManager = context.applicationContext.getSystemService(
           Context.WIFI_SERVICE
        ) as WifiManager
    }

    override fun start() {

        val dhcp = mWifiManager.dhcpInfo
        val ipDhcp = dhcp.gateway

        if (ipDhcp == 0) {
            //onGetIP("")
            return
        }

        val ip = ByteUtils.integer(
            if (ByteOrder.nativeOrder()
                    .equals(ByteOrder.LITTLE_ENDIAN)
            ) Integer.reverseBytes(ipDhcp)
            else ipDhcp
        )
        val gateSt = "${ip[0]}.${ip[1]}.${ip[2]}.${ip[3]}"
        val serverIP = InetAddress.getByName(gateSt)

        Log.d(TAG, "getHotspotIP: $serverIP $ipDhcp")

        //onGetIP("$serverIP".substring(1))
    }

}