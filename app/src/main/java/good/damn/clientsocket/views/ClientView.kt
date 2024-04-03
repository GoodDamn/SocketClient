package good.damn.clientsocket.views

import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import good.damn.clientsocket.Application
import good.damn.clientsocket.listeners.network.connection.Connectable
import good.damn.clientsocket.ContentLauncher
import good.damn.clientsocket.listeners.network.service.HotspotServiceListener
import good.damn.clientsocket.listeners.view.ClientViewListener
import good.damn.clientsocket.utils.FileUtils
import good.damn.clientsocket.messengers.Messenger
import good.damn.clientsocket.network.DnsConnection
import good.damn.clientsocket.services.network.HotspotServiceCompat
import java.net.Socket

class ClientView(
    activity: AppCompatActivity
) : LinearLayout(activity),
    Connectable, HotspotServiceListener {

    private val msgr = Messenger()
    private var mEditTextHost: EditText
    private var mEditTextMsg: EditText

    private val mHotspotService: HotspotServiceCompat

    var delegate: ClientViewListener? = null

    init {
        mEditTextHost = EditText(context)
        mEditTextMsg = EditText(context)

        mHotspotService = HotspotServiceCompat(
            context
        )
    }

    fun createView() {

        mEditTextHost.hint = "Enter host IP"
        mEditTextMsg.hint = "Message"

        val btnConnect = Button(context)
        btnConnect.text = "Connect"

        val textViewMsg = TextView(context)
        textViewMsg.text = "----"
        textViewMsg.textSize = 18f
        textViewMsg.movementMethod = ScrollingMovementMethod()
        textViewMsg.isVerticalScrollBarEnabled = true
        textViewMsg.isHorizontalScrollBarEnabled = false

        msgr.setTextView(
            textViewMsg
        )

        gravity = Gravity.CENTER
        orientation = VERTICAL

        addView(
            mEditTextHost,
            -1,
            -2
        )

        addView(
            mEditTextMsg,
            -1,
            -2
        )

        addView(
            btnConnect,
            -1,
            -2
        )

        addView(
            textViewMsg,
            -1,
            -2
        )

        delegate?.onCreateClientView(
            mEditTextHost,
            mEditTextMsg,
            btnConnect
        )

        mHotspotService.delegate = this
        mHotspotService.start()
    }

    @WorkerThread
    override fun onConnected(socket: Socket) {
        msgr.addMessage("Connected to ${socket.remoteSocketAddress}")
    }

    @WorkerThread
    override fun onSendTextBytes(): ByteArray {
        return ByteArray(0)
    }

    @WorkerThread
    override fun onSendBytes(): ByteArray {
        return ByteArray(0)
    }

    @WorkerThread
    override fun onSendTypeResponse(): Int {
        return 0 // 1 - file; 2 - text
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

    override fun onGetHotspotIP(
        ip: ByteArray
    ) {
        if (ip.size == 0) {
            return
        }

        mEditTextHost.setText(
            "${ip[0]}.${ip[1]}.${ip[2]}.${ip[3]}"
        )
    }

    final override fun addView(
        child: View?,
        width: Int,
        height: Int
    ) {
        super.addView(child, width, height)
    }
}