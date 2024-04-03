package good.damn.clientsocket.activities

import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import good.damn.clientsocket.ContentLauncher
import good.damn.clientsocket.listeners.network.connection.ConnectionListener
import good.damn.clientsocket.listeners.view.ClientViewListener
import good.damn.clientsocket.messengers.Messenger
import good.damn.clientsocket.network.OwnConnection
import good.damn.clientsocket.utils.FileUtils
import good.damn.clientsocket.views.ClientView
import java.net.Socket

class IPPortActivity
    : AppCompatActivity(),
    ClientViewListener,
    ActivityResultCallback<Uri?>,
    ConnectionListener {

    private var msgr: Messenger? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

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
        val contentLauncher = ContentLauncher(
            this,
            this
        )

        val btnSelectFile = Button(
            this
        )

        btnSelectFile.text = "Select file for response"

        btnSelectFile.setOnClickListener {
            contentLauncher.launch("*/*")
        }

        btnConnect.setOnClickListener {
            OwnConnection(
                editHost.text.toString()
            ).start(this)
        }

        btnSelectFile.layoutParams = ViewGroup
            .LayoutParams(-1,-2)

        clientView.addView(
            btnSelectFile,
            clientView.childCount - 2 // before messenger
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
        msgr?.addMessage(
            "IPv4 Client: ${socket.remoteSocketAddress}"
        )
    }

    @WorkerThread
    override fun onRequest(): ByteArray {
        return byteArrayOf(0,15,15)
    }

    @WorkerThread
    override fun onResponse(
        response: ByteArray
    ) {
        msgr?.addMessage(
            "RESPONSE_BYTES: ${response.contentToString()}"
        )
    }

}