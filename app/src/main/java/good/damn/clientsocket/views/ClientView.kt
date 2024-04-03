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
import good.damn.clientsocket.Application
import good.damn.clientsocket.network.interfaces.Connectable
import good.damn.clientsocket.ContentLauncher
import good.damn.clientsocket.utils.FileUtils
import good.damn.clientsocket.messengers.Messenger
import good.damn.clientsocket.network.DnsConnection
import good.damn.clientsocket.services.network.HotspotServiceCompat
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
    private var mEditTextHost: EditText
    private var mEditTextMsg: EditText

    private var mResponseType: Int = 1 // file
    private var mResponse = byteArrayOf(48)
    private var mResponseText = byteArrayOf(48)

    private val mHotspotService: HotspotServiceCompat

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

        mEditTextHost = EditText(context)
        mEditTextHost.hint = "Enter host domain"

        mEditTextMsg = EditText(context)
        mEditTextMsg.hint = "Message"

        val btnConnect = Button(context)
        btnConnect.text = "Connect to host"

        val btnConnectDns = Button(
            context
        )
        btnConnectDns.text = "Connect to DNS server"

        val btnSelectFile = Button(context)
        btnSelectFile.text = "Select file for response"

        val textViewMsg = TextView(context)
        textViewMsg.text = "----"
        textViewMsg.textSize = 18f
        textViewMsg.movementMethod = ScrollingMovementMethod()
        textViewMsg.isVerticalScrollBarEnabled = true
        textViewMsg.isHorizontalScrollBarEnabled = false

        msgr.setTextView(textViewMsg)

        btnConnectDns.setOnClickListener {
            val dns = DnsConnection(
                mEditTextHost.text.toString(),
                53)
            dns.connect(
                mEditTextMsg.text.toString()
            ) { msg, _ ->
                msgr.addMessage(msg)
            }
        }

        btnConnect.setOnClickListener {
            connectToHost(
                mEditTextHost.text.toString(),
                8080,
                Application.BUFFER_MB
            )
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
        addView(btnConnectDns, -1,-2)
        addView(textViewMsg, -1, -2)

        mHotspotService = HotspotServiceCompat(
            context
        )
        mHotspotService.start()
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
}