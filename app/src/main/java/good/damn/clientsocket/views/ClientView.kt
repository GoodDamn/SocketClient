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
    HotspotServiceListener {

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
        val btnConnect = Button(context)

        mEditTextHost.hint = "Enter host IP"
        mEditTextMsg.hint = "Message"

        btnConnect.text = "Connect"

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

        delegate?.onCreateClientView(
            mEditTextHost,
            mEditTextMsg,
            btnConnect,
            this
        )

        mHotspotService.delegate = this
        mHotspotService.start()
    }

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
}