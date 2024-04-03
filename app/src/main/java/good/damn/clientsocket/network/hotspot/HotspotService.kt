package good.damn.clientsocket.network.hotspot

import android.content.Context
import android.net.*
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import good.damn.clientsocket.utils.ByteUtils
import java.net.InetAddress
import java.nio.ByteOrder

class HotspotService(
    val context: Context
) {

    companion object {
        private const val TAG = "HotspotService"
    }

    private val mServiceManager: Any

    init {
        mServiceManager = context.getSystemService(
            if (isR())
                Context.CONNECTIVITY_SERVICE
            else Context.WIFI_SERVICE
        )

    }

    fun start() {

        if (isR()) {

            val manager = mServiceManager
                as ConnectivityManager

            val request = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()

            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onLinkPropertiesChanged(
                    network: Network,
                    link: LinkProperties
                ) {
                    val dhcp = link.dhcpServerAddress?.address ?: ByteArray(0)
                    //onGetIP("${dhcp[0]}.${dhcp[1]}.${dhcp[2]}.${dhcp[3]}")
                    super.onLinkPropertiesChanged(network, link)
                }

            }

            manager.requestNetwork(request, callback)
            manager.registerNetworkCallback(request, callback)

            return
        }

        val manager = mServiceManager
            as WifiManager

        val dhcp = manager.dhcpInfo

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

    private fun isR(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }

}