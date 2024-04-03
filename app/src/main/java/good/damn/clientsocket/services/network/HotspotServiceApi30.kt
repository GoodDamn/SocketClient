package good.damn.clientsocket.services.network

import android.content.Context
import android.net.*
import androidx.annotation.RequiresApi
import good.damn.clientsocket.listeners.service.network.HotspotServiceListener
import good.damn.clientsocket.services.BaseService

@RequiresApi(30)
class HotspotServiceApi30(
    context: Context
): BaseService<HotspotServiceListener>(context) {

    private val mConnectivityManager: ConnectivityManager
    private val mNetworkRequest: NetworkRequest
    private val mCallback: ConnectivityManager.NetworkCallback

    init {
        mConnectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        mNetworkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        mCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onLinkPropertiesChanged(
                network: Network,
                link: LinkProperties
            ) {
                val dhcp = link.dhcpServerAddress?.address ?: ByteArray(0)
                delegate?.onGetHotspotIP(
                    dhcp
                )
                super.onLinkPropertiesChanged(network, link)
            }
        }
    }

    override fun start() {
        mConnectivityManager.requestNetwork(
            mNetworkRequest,
            mCallback
        )
        mConnectivityManager.registerNetworkCallback(
            mNetworkRequest,
            mCallback
        )
    }
}