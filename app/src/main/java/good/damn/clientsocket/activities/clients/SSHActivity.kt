package good.damn.clientsocket.activities.clients

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultCallback
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import good.damn.clientsocket.Application
import good.damn.clientsocket.ContentLauncher
import good.damn.clientsocket.listeners.network.connection.SSHConnectionListener
import good.damn.clientsocket.listeners.view.ClientViewListener
import good.damn.clientsocket.network.SSHConnection
import good.damn.clientsocket.utils.ByteUtils
import good.damn.clientsocket.utils.FileUtils
import good.damn.clientsocket.views.ClientView

class SSHActivity
: AppCompatActivity(),
    ClientViewListener,
    SSHConnectionListener,
    ActivityResultCallback<Uri?> {


    companion object {
        private const val RESPONSE_ID_MESSAGE = 1
        private const val RESPONSE_ID_MESSAGE16 = 2
    }

    private var mClientView: ClientView? = null

    private var mEditTextAuth: EditText? = null
    private var mEditTextCommand: EditText? = null

    private var mRsaKey = ByteArray(0)
    private var mBtnKey: Button? = null

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

        mBtnKey = Button(
            this
        )
        mBtnKey?.text = "Load RSA key"

        val contentBrowser = ContentLauncher(
            this,
            this
        )

        mBtnKey?.setOnClickListener {
            contentBrowser.launch("*/*")
        }

        val buffer = ByteArray(4096)
        btnConnect.setOnClickListener {
            SSHConnection(
                editHost.text.toString(),
                8080,
                buffer
            ).start(this)
        }

        mBtnKey?.layoutParams = ViewGroup
            .LayoutParams(-1,-2)

        mEditTextAuth?.layoutParams = ViewGroup
            .LayoutParams(-1,-2)

        mClientView?.addView(
            mEditTextAuth,
            mClientView!!.childCount - 1
        )

        mClientView?.addView(
            mBtnKey,
            mClientView!!.childCount - 1
        )
    }

    @WorkerThread
    override fun onStartConnection() {
        mClientView?.addMessage(
            "Connection started"
        )
    }

    @WorkerThread
    override fun onDebugConnection(
        msg: String
    ) {
        mClientView?.addMessage(
            msg
        )
    }

    @WorkerThread
    override fun onEndConnection() {
        mClientView?.addMessage(
            "Connection ended"
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
        return mRsaKey
    }

    override fun onResponse(
        response: ByteArray
    ) {
        mClientView?.addMessage(
            "RESPONSE:"
        )

        val responseID = ByteUtils
            .integer(response)

        var lenMsg = 0
        var offsetMsg = 0

        when (responseID) {
            RESPONSE_ID_MESSAGE -> {
                lenMsg = response[4]
                    .toInt()
                offsetMsg = 5
            }
            RESPONSE_ID_MESSAGE16 -> {
                lenMsg = ByteUtils.short(
                    response,
                    4
                )
                offsetMsg = 6
            }
        }

        val msg = String(
            response,
            offsetMsg,
            lenMsg,
            Application.CHARSET_ASCII
        )

        mClientView?.addMessage(
            msg
        )
    }

    override fun onActivityResult(
        result: Uri?
    ) {
        val k = FileUtils
            .read(result, this)

        if (k == null) {
            mBtnKey?.text = "Load RSA key"
            mRsaKey = ByteArray(0)
            return
        }

        mBtnKey?.text = "RSA Key attached"
        mRsaKey = k
    }

}