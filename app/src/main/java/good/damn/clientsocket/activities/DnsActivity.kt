package good.damn.clientsocket.activities

import android.os.Bundle
import android.os.PersistableBundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import good.damn.clientsocket.listeners.view.ClientViewListener
import good.damn.clientsocket.messengers.Messenger
import good.damn.clientsocket.network.DnsConnection
import good.damn.clientsocket.views.ClientView

class DnsActivity
    : AppCompatActivity(),
    ClientViewListener {

    private val msgr = Messenger()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )

        val clientView = ClientView(
            this
        )

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
        btnConnect.text = "Connect to DNS"
        editHost.hint = "DNS IP"
        editMsg.hint = "Request domain (google.com, ...)"

        val textViewMsg = TextView(this)
        textViewMsg.text = "----"
        textViewMsg.textSize = 18f
        textViewMsg.movementMethod = ScrollingMovementMethod()
        textViewMsg.isVerticalScrollBarEnabled = true
        textViewMsg.isHorizontalScrollBarEnabled = false

        clientView.addView(
            textViewMsg,
            -1,
            500
        )

        msgr.setTextView(
            textViewMsg
        )

        btnConnect.setOnClickListener {
            DnsConnection(
                editHost.text.toString()
            ).connect(
                editMsg.text.toString()
            ) { response, ip ->
                msgr.addMessage(
                    response
                )
            }
        }

    }

}