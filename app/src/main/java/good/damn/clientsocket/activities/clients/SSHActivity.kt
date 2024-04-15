package good.damn.clientsocket.activities.clients

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import good.damn.clientsocket.listeners.view.ClientViewListener
import good.damn.clientsocket.views.ClientView

class SSHActivity
: AppCompatActivity(),
    ClientViewListener {

    private var mClientView: ClientView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mClientView = ClientView(
            this
        )

        mClientView?.delegate = this

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

    }

}