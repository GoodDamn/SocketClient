package good.damn.clientsocket.activities

import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import good.damn.clientsocket.ContentLauncher
import good.damn.clientsocket.builder.shareProtocol.ShareRequestBuilder
import good.damn.clientsocket.listeners.network.connection.ConnectionListener
import good.damn.clientsocket.listeners.network.service.ResponseServiceListener
import good.damn.clientsocket.listeners.view.ClientViewListener
import good.damn.clientsocket.messengers.Messenger
import good.damn.clientsocket.network.ShareProtocolConnection
import good.damn.clientsocket.services.response.ResponseService
import good.damn.clientsocket.shareProtocol.ShareModelFile
import good.damn.clientsocket.shareProtocol.ShareModelListString
import good.damn.clientsocket.shareProtocol.ShareRequestBodyArgs
import good.damn.clientsocket.shareProtocol.ShareRequestMethod
import good.damn.clientsocket.utils.FileUtils
import good.damn.clientsocket.views.ClientView
import java.net.Socket

class IPPortActivity
    : AppCompatActivity(),
    ClientViewListener,
    ActivityResultCallback<Uri?>,
    ConnectionListener,
    ResponseServiceListener {

    private var mEditTextRequest: EditText? = null

    private var mTextQuery = "temp"

    private var msgr = Messenger()
    private val mResponseService = ResponseService()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        mResponseService.delegate = this

        val clientView = ClientView(this)
        clientView.delegate = this
        clientView.createView()

        setContentView(
            clientView
        )
    }

    override fun onCreateClientView(
        editHost: EditText,
        editMsg: EditText,
        btnConnect: Button,
        clientView: ClientView
    ) {
        mEditTextRequest = editMsg
        val contentLauncher = ContentLauncher(
            this,
            this
        )

        val btnSelectFile = Button(
            this
        )

        val textViewMsg = TextView(this)
        textViewMsg.text = "----"
        textViewMsg.textSize = 18f
        textViewMsg.movementMethod = ScrollingMovementMethod()
        textViewMsg.isVerticalScrollBarEnabled = true
        textViewMsg.isHorizontalScrollBarEnabled = false
        msgr.setTextView(textViewMsg)

        btnSelectFile.text = "Select file for response"

        btnSelectFile.setOnClickListener {
            contentLauncher.launch("*/*")
        }

        btnConnect.setOnClickListener {
            ShareProtocolConnection(
                editHost.text.toString()
            ).start(this)
        }

        btnSelectFile.layoutParams = ViewGroup
            .LayoutParams(-1,-2)

        clientView.addView(
            btnSelectFile,
            clientView.childCount - 2 // before messenger
        )

        clientView.addView(
            textViewMsg,
            -1,
            -1
        )
    }

    override fun onActivityResult(
        uri: Uri?
    ) {
        val activity = this
        val data = FileUtils.read(
            uri,
            activity
        )

        if (data == null) {
            Toast.makeText(
                activity,
                "Something went wrong with file",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val p = uri!!.path!!
        val t = "primary:"
        val filePath = p.substring(
            p.indexOf(t)+t.length
        )

        val nameIndex = filePath.lastIndexOf(
            "/"
        )

        val fileName = if (nameIndex == -1)
                filePath
            else filePath.substring(
            nameIndex+1
            )

        Toast.makeText(
            activity,
            "FILE IS PREPARED $fileName",
            Toast.LENGTH_SHORT)
            .show()
    }

    @WorkerThread
    override fun onConnected(
        socket: Socket
    ) {
        msgr.addMessage(
            "IPv4 Client: ${socket.remoteSocketAddress}"
        )
    }

    @WorkerThread
    override fun onRequest(): ByteArray {

        if (mEditTextRequest == null) {
            return ByteArray(0)
        }

        val params = mEditTextRequest!!.text.split(
            "\\s+".toRegex()
        )

        mTextQuery = mEditTextRequest!!.text.toString()

        val shareRequest = ShareRequestBuilder()
            .setMethod(ShareRequestMethod(
                params[0]
            ))
            .setBody(ShareRequestBodyArgs(
                params,
                1
            ))
            .build() ?: return ByteArray(0)

        return shareRequest.toByteArray()
    }

    @WorkerThread
    override fun onResponse(
        response: ByteArray
    ) {
        msgr.addMessage(
            "RESPONSE_BYTES: ${response[0]}"
        )

        if (response.isEmpty()) {
            return
        }

        mResponseService.decodeResponse(
            response
        )
    }

    @WorkerThread
    override fun onModelResponse(
        model: Any
    ) {
        if (model is ShareModelListString) {
            msgr.addMessage(
                "RESPONSE_FILES_LIST:"
            )

            for (fileName in model.list) {
                msgr.addMessage(fileName)
            }
            return
        }


        if (model is ShareModelFile) {
            msgr.addMessage(
                "RESPONSE_FILE: ${model.fileSize} bytes"
            )

            FileUtils.writeToDoc(
                mTextQuery,
                model.file,
                model.filePosition
            )
            return
        }
    }

}