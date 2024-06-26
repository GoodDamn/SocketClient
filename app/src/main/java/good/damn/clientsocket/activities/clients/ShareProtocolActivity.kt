package good.damn.clientsocket.activities.clients

import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import good.damn.clientsocket.Application
import good.damn.clientsocket.ContentLauncher
import good.damn.clientsocket.builder.shareProtocol.ShareRequestBuilder
import good.damn.clientsocket.listeners.network.connection.ConnectionListener
import good.damn.clientsocket.listeners.network.service.ResponseServiceListener
import good.damn.clientsocket.listeners.view.ClientViewListener
import good.damn.clientsocket.network.SSLShareConnection
import good.damn.clientsocket.network.ShareConnection
import good.damn.clientsocket.services.response.ResponseService
import good.damn.clientsocket.shareProtocol.*
import good.damn.clientsocket.utils.ByteUtils
import good.damn.clientsocket.utils.FileUtils
import good.damn.clientsocket.views.ClientView
import java.net.Socket

class ShareProtocolActivity
    : AppCompatActivity(),
    ClientViewListener,
    ConnectionListener,
    ResponseServiceListener,
    ActivityResultCallback<Uri?> {

    private var mEditTextRequest: EditText? = null

    private var mTextQuery = "temp"

    private val mResponseService = ResponseService()
    private var mClientView: ClientView? = null

    private val mFiles = HashMap<String, ByteArray>()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        mResponseService.delegate = this

        mClientView = ClientView(this)
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
        mEditTextRequest = editMsg

        val horizontalLayout = LinearLayout(
            this
        )

        val checkBoxSSL = CheckBox(
            this
        )

        val btnLoadFile = Button(
            this
        )

        val content = ContentLauncher(
            this,
            this
        )

        horizontalLayout.orientation = LinearLayout
            .HORIZONTAL

        btnLoadFile.text  = "Load file"
        checkBoxSSL.text = "SSL"

        btnLoadFile.setOnClickListener {
            content.launch("*/*")
        }

        btnConnect.setOnClickListener {
            if (checkBoxSSL.isChecked) {
                SSLShareConnection(
                    editHost.text.toString()
                ).start(this)
                return@setOnClickListener
            }

            ShareConnection(
                editHost.text.toString()
            ).start(this)
        }

        horizontalLayout.layoutParams = ViewGroup
            .LayoutParams(-1,-2)

        horizontalLayout.addView(
            checkBoxSSL,
            -2,-2
        )

        horizontalLayout.addView(
            btnLoadFile,
            -2,-2
        )

        clientView.addView(
            horizontalLayout,
            clientView.childCount - 1 // before messenger
        )
    }

    @WorkerThread
    override fun onConnected(
        socket: Socket
    ) {
        mClientView?.addMessage(
            "Connected to server!"
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

        if (params[0] == "sf") {

            val d = mFiles[params[1]] ?: return ByteArray(0)

            val fileName = params[1].toByteArray(
                Application.CHARSET_ASCII
            )

            val shareRequest = ShareRequestBuilder()
                .setMethod(ShareRequestMethod(
                    params[0]
                ))
                .setBody(ShareRequestBody(
                    byteArrayOf(
                        2,
                        fileName.size.toByte()
                    ) + fileName + ByteUtils.integer(
                        d.size
                    ) + d
                ))
                .build() ?: return ByteArray(0)

            return shareRequest.toByteArray()
        }

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
    override fun onDebugMessage(msg: String) {
        mClientView?.addMessage(
            msg
        )
    }

    @WorkerThread
    override fun onResponse(
        response: ByteArray
    ) {
        if (response.isEmpty()) {
            mClientView?.addMessage(
                "No response"
            )
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
        if (model is String) {
            mClientView?.addMessage(
                model
            )
            return
        }

        if (model is ShareModelListString) {
            mClientView?.addMessage(
                "RESPONSE_FILES_LIST:"
            )

            for (fileName in model.list) {
                mClientView?.addMessage(fileName)
            }
            return
        }


        if (model is ShareModelFile) {
            mClientView?.addMessage(
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

        mFiles[fileName] = data

        Toast.makeText(
            activity,
            "FILE IS PREPARED $fileName",
            Toast.LENGTH_SHORT
        ).show()
    }

}