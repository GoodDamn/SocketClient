package good.damn.clientsocket.activities.clients

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import good.damn.clientsocket.Application
import good.damn.clientsocket.listeners.network.connection.SSHConnectionListener
import good.damn.clientsocket.listeners.view.ClientViewListener
import good.damn.clientsocket.network.SSHConnection
import good.damn.clientsocket.views.ClientView

class SSHActivity
: AppCompatActivity(),
    ClientViewListener,
    SSHConnectionListener {

    private var mClientView: ClientView? = null

    private var mEditTextAuth: EditText? = null
    private var mEditTextCommand: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        mEditTextAuth = EditText(
            this
        )
        mEditTextCommand = editMsg

        mEditTextCommand?.hint = "command line"
        mEditTextAuth?.hint = "user@password"

        val buffer = ByteArray(300)
        btnConnect.setOnClickListener {
            SSHConnection(
                editHost.text.toString(),
                8080,
                buffer
            ).start(this)
        }

        mEditTextAuth?.layoutParams = ViewGroup
            .LayoutParams(-1,-2)

        mClientView?.addView(
            mEditTextAuth,
            mClientView!!.childCount - 1
        )

    }

    override fun onCredentials(): String {
        return mEditTextAuth?.text.toString()
    }

    override fun onCommandArgs(): Array<String> {
        return mEditTextCommand?.text.toString()
            .split("\\s+".toRegex())
            .toTypedArray()
    }

    override fun keyRSA(): ByteArray {
        return ByteArray(0)
    }

    override fun onResponse(
        response: ByteArray
    ) {
        mClientView?.addMessage(
            "RESPONSE:"
        )

        val lenMsg = response[0]
            .toInt()

        val msg = String(
            response,
            1,
            lenMsg,
            Application.CHARSET_ASCII
        )

        mClientView?.addMessage(
            msg
        )
    }

}