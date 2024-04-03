package good.damn.clientsocket.network.hotspot

import android.content.Context
import android.net.*
import androidx.annotation.RequiresApi

@RequiresApi(30)
class HotspotServiceApi30(
    val context: Context
): ConnectivityManager.NetworkCallback() {

    private val mConnectivityManager: ConnectivityManager
    private val mNetworkRequest: NetworkRequest

    init {
        mConnectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        mNetworkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
    }

    override fun onLinkPropertiesChanged(
        network: Network,
        link: LinkProperties
    ) {
        val dhcp = link.dhcpServerAddress?.address ?: ByteArray(0)
        //onGetIP("${dhcp[0]}.${dhcp[1]}.${dhcp[2]}.${dhcp[3]}")
        super.onLinkPropertiesChanged(network, link)
    }

    fun start() {
        mConnectivityManager.requestNetwork(
            mNetworkRequest,
            this
        )
        mConnectivityManager.registerNetworkCallback(
            mNetworkRequest,
            this
        )
    }
}