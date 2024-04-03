package good.damn.clientsocket.listeners.view

import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import good.damn.clientsocket.views.ClientView

interface ClientViewListener {

    fun onCreateClientView(
        editHost: EditText,
        editMsg: EditText,
        btnConnect: Button,
        clientView: ClientView
    )
}