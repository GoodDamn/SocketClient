package good.damn.clientsocket.activities.clients

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import good.damn.clientsocket.Application
import good.damn.clientsocket.listeners.network.connection.DnsConnectionListener
import good.damn.clientsocket.listeners.view.ClientViewListener
import good.damn.clientsocket.messengers.Messenger
import good.damn.clientsocket.network.DnsConnection
import good.damn.clientsocket.views.ClientView

class DnsActivity
    : AppCompatActivity(),
    ClientViewListener,
    DnsConnectionListener {

    private var mEditTextDomain: EditText? = null

    private var mClientView: ClientView? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )

        mClientView = ClientView(
            this
        )

        mClientView?.delegate = this
        mClientView?.createView()

        setContentView(
            mClientView
        )
    }


    override fun onCreateClientView(
        editHost: EditText,
        editMsg: EditText,
        btnConnect: Button,
        clientView: ClientView
    ) {
        mEditTextDomain = editMsg

        btnConnect.text = "Connect to DNS"
        editHost.hint = "DNS IP"
        editMsg.hint = "Request domain (google.com, ...)"

        btnConnect.setOnClickListener {
            DnsConnection(
                editHost.text.toString(),
                Application.BUFFER_300
            ).start(this)
        }

    }

    override fun onGetIP(
        domain: String,
        ip: String
    ) {}

    override fun onRawResponse(
        response: ByteArray
    ) {
        mClientView?.addMessage(
            "RAW_RESPONSE: ${response.contentToString()}"
        )
    }

    override fun onRequestDomain(): String {
        if (mEditTextDomain == null) {
            return "vk.com"
        }

        return mEditTextDomain!!.text
            .toString()
    }

    override fun onDebugResponse(
        response: String
    ) {
        mClientView?.addMessage(
            "DEBUG_RESPONSE: $response"
        )
    }

}