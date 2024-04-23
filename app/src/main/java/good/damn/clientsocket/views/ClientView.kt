package good.damn.clientsocket.views

import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import good.damn.clientsocket.listeners.network.service.HotspotServiceListener
import good.damn.clientsocket.listeners.view.ClientViewListener
import good.damn.clientsocket.messengers.Messenger
import good.damn.clientsocket.services.network.HotspotServiceCompat

class ClientView(
    activity: AppCompatActivity
) : LinearLayout(activity),
    HotspotServiceListener {

    private var mEditTextHost: EditText
    private var mEditTextMsg: EditText

    private val mHotspotService: HotspotServiceCompat

    private val msgr = Messenger()

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

        val textViewMsg = TextView(context)
        textViewMsg.text = "----"
        textViewMsg.textSize = 10f
        textViewMsg.movementMethod = ScrollingMovementMethod()
        textViewMsg.isVerticalScrollBarEnabled = true
        textViewMsg.isHorizontalScrollBarEnabled = false

        mEditTextHost.hint = "Enter host IP"
        mEditTextMsg.hint = "Message"

        mEditTextMsg.maxLines = 1

        btnConnect.text = "Connect"

        orientation = VERTICAL

        msgr.setTextView(
            textViewMsg
        )

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
            -1
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

        val ip1 = ip[0].toInt() and 0xff
        val ip2 = ip[1].toInt() and 0xff
        val ip3 = ip[2].toInt() and 0xff
        val ip4 = ip[3].toInt() and 0xff

        mEditTextHost.setText(
            "$ip1.$ip2.$ip3.$ip4"
        )
    }


    fun addMessage(
        msg: String,
        indexed: Boolean = true
    ) {
        msgr.addMessage(
            msg,
            indexed
        )
    }

}