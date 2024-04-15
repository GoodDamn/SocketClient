package good.damn.clientsocket.activities.clients

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import good.damn.clientsocket.Application
import good.damn.clientsocket.listeners.network.connection.DhcpConnectionListener
import good.damn.clientsocket.network.DhcpConnection
import good.damn.clientsocket.utils.ByteUtils
import java.net.InetAddress

class DhcpActivity
    : AppCompatActivity(),
    DhcpConnectionListener {

    companion object {
        private const val TAG = "DhcpActivity"
    }

    private var mEditTextMsg: EditText? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )

        val context = this
        val btnConnect = Button(
            context
        )

        mEditTextMsg = EditText(
            context
        )

        val layout = LinearLayout(
            context
        )

        layout.orientation = LinearLayout
            .VERTICAL

        btnConnect.text = "Connect to ${getBroadcastAddress()}"

        btnConnect.setOnClickListener(
            this::onClickBtnConnect
        )

        layout.addView(
            btnConnect,
            -1,
            -2
        )

        layout.addView(
            mEditTextMsg,
            -1,
            -2
        )

        setContentView(
            layout
        )
    }

    private fun getBroadcastAddress(): String {
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

        return inet.toString()

    }

    override fun onRequest(): ByteArray {
        val data = mEditTextMsg?.text.toString().toByteArray(
            Application.CHARSET_ASCII
        )
        return byteArrayOf(data.size.toByte()) + data
    }

}

private fun DhcpActivity.onClickBtnConnect(
    view: View
) {
    val connection = DhcpConnection()
    connection.start(
        this
    )
}