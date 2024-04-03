package good.damn.clientsocket.activities

import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.appcompat.app.AppCompatActivity
import good.damn.clientsocket.ContentLauncher
import good.damn.clientsocket.listeners.view.ClientViewListener
import good.damn.clientsocket.utils.FileUtils
import good.damn.clientsocket.views.ClientView

class IPPortActivity
    : AppCompatActivity(),
    ClientViewListener,
    ActivityResultCallback<Uri?> {


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

        clientView.addView(
            btnSelectFile,
            -1,
            -2
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

}