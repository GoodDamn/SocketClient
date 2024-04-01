package good.damn.clientsocket.views

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Context.WIFI_SERVICE
import android.net.*
import android.net.ConnectivityManager.NetworkCallback
import android.net.wifi.WifiManager
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import good.damn.clientsocket.network.interfaces.Connectable
import good.damn.clientsocket.ContentLauncher
import good.damn.clientsocket.utils.FileUtils
import good.damn.clientsocket.messengers.Messenger
import good.damn.clientsocket.network.DnsConnection
import good.damn.clientsocket.utils.ByteUtils
import java.net.InetAddress
import java.net.Socket
import java.nio.ByteOrder

@ExperimentalUnsignedTypes
class ClientView(
    context: Context,
    activity: AppCompatActivity
) : LinearLayout(context),
    Connectable {

    private val TAG = "ClientView"

    private val msgr = Messenger()
    private var mEditTextDns: EditText
    private var mEditTextHost: EditText
    private var mEditTextMsg: EditText

    private var mResponseType: Int = 1 // file
    private var mResponse = byteArrayOf(48)
    private var mResponseText = byteArrayOf(48)

    init {
        val contentLauncher = ContentLauncher(activity) {
            uri ->

            val data = FileUtils.read(uri,context)

            if (data == null) {
                Toast.makeText(
                    context,
                    "Something went wrong with file",
                    Toast.LENGTH_SHORT
                ).show()
                return@ContentLauncher
            }

            val p = uri!!.path!!
            val t = "primary:"
            val filePath = p.substring(p.indexOf(t)+t.length)

            val nameIndex = filePath.lastIndexOf("/")

            val fileName = if (nameIndex == -1)
                            filePath
                           else filePath.substring(nameIndex+1)

            mResponseText = fileName.toByteArray(
                msgr.getCharset()
            )

            mResponse = data
            mResponseType = 1

            Toast.makeText(
                context,
                "FILE IS PREPARED $fileName",
                Toast.LENGTH_SHORT)
                .show()

        }

        mEditTextDns = EditText(context)
        mEditTextDns.hint = "DNS IP"

        mEditTextHost = EditText(context)
        mEditTextHost.hint = "Enter host domain"

        mEditTextMsg = EditText(context)
        mEditTextMsg.hint = "Message"

        val btnConnect = Button(context)
        btnConnect.text = "Connect to host"

        val btnSelectFile = Button(context)
        btnSelectFile.text = "Select file for response"

        val textViewMsg = TextView(context)
        textViewMsg.text = "----"
        textViewMsg.textSize = 18f
        textViewMsg.movementMethod = ScrollingMovementMethod()
        textViewMsg.isVerticalScrollBarEnabled = true
        textViewMsg.isHorizontalScrollBarEnabled = false

        msgr.setTextView(textViewMsg)

        val buffer = ByteArray(1024*1024)

        btnConnect.setOnClickListener {
            val dns = DnsConnection(
                mEditTextHost.text.toString(),
                53)
            dns.connect(mEditTextHost.text.toString()) {
                msg, port ->
                msgr.addMessage(msg)
                //connectToHost(ipHost, port, buffer)
            }
        }

        btnSelectFile.setOnClickListener {
            contentLauncher.launch("*/*")
        }

        mEditTextMsg.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s == null) {
                    return
                }
                mResponseText = s.toString().toByteArray(
                    msgr.getCharset()
                )

                mResponse = byteArrayOf()
                mResponseType = 2
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        gravity = Gravity.CENTER
        orientation = VERTICAL

        addView(mEditTextHost, -1, -2)
        addView(btnSelectFile, -1, -2)
        addView(mEditTextMsg, -1,-2)
        addView(btnConnect, -1, -2)
        addView(textViewMsg, -1, -2)

        getHotspotIP {
            mEditTextHost.setText(it)
        }
    }

    @WorkerThread
    override fun onConnected(socket: Socket) {
        msgr.addMessage("Connected to ${socket.remoteSocketAddress}")
    }

    @WorkerThread
    override fun onSendTextBytes(): ByteArray {
        return mResponseText
    }

    @WorkerThread
    override fun onSendBytes(): ByteArray {
        return mResponse
    }

    @WorkerThread
    override fun onSendTypeResponse(): Int {
        return mResponseType // 1 - file; 2 - text
    }

    @WorkerThread
    override fun onGetFile(
        data: ByteArray,
        offset: Int,
        fileName: String
    ) {
        val msg = FileUtils.writeToDoc(fileName, data, offset)

        if (msg != null) {
            msgr.addMessage("Non saved $fileName: $msg")
            return
        }

        msgr.addMessage("$fileName is saved to Documents")
    }

    @WorkerThread
    override fun onGetText(msg: String) {
        msgr.addMessage(msg)
    }

    @WorkerThread
    override fun onHttpGet() {}

    private fun getHotspotIP(
        onGetIP: (String) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val manager = context.getSystemService(CONNECTIVITY_SERVICE)
                as ConnectivityManager

            val request = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()

            val callback = object : NetworkCallback() {
                override fun onLinkPropertiesChanged(
                    network: Network,
                    link: LinkProperties
                ) {
                    val dhcp = link.dhcpServerAddress?.address ?: ByteArray(0)
                    onGetIP("${dhcp[0]}.${dhcp[1]}.${dhcp[2]}.${dhcp[3]}")
                    super.onLinkPropertiesChanged(network, link)
                }

            }

            manager.requestNetwork(request, callback)
            manager.registerNetworkCallback(request, callback)

            return
        }

        val manager = context.getSystemService(WIFI_SERVICE)
                as WifiManager

        val dhcp = manager.dhcpInfo

        val ipDhcp = dhcp.gateway

        if (ipDhcp == 0) {
            onGetIP("")
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

        onGetIP("$serverIP".substring(1))
    }
}