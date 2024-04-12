package good.damn.clientsocket.activities

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import good.damn.clientsocket.listeners.view.ClientViewListener
import good.damn.clientsocket.network.DhcpConnection
import good.damn.clientsocket.utils.ByteUtils
import good.damn.clientsocket.views.ClientView
import java.net.InetAddress
import java.util.Objects

class DhcpActivity
    : AppCompatActivity() {

    companion object {
        private const val TAG = "DhcpActivity"
    }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )

        getBroadcastAddress()

        val connection = DhcpConnection()
        val a = Any()

        connection.start(
            a
        )

    }

    private fun getBroadcastAddress(): InetAddress {
        val wifi = applicationContext.getSystemService(
            Context.WIFI_SERVICE
        ) as WifiManager

        val info = wifi.dhcpInfo

        val broadcast = (info.ipAddress and info.netmask) or info.netmask.inv()
        Log.d(TAG, "getBroadcastAddress: ${info.ipAddress}_____${info.netmask}_____${info.netmask.inv()}")

        val ip = ByteUtils
            .integer(broadcast)

        val inet = InetAddress.getByAddress(
            ip
        )

        Log.d(TAG, "getBroadcastAddress: IP: ${ip.contentToString()} $inet")

        return inet

    }

}