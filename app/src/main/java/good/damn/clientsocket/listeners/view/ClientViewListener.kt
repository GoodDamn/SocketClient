package good.damn.clientsocket.listeners.view

import android.widget.Button
import android.widget.EditText

interface ClientViewListener {

    fun onCreateClientView(
        editHost: EditText,
        editMsg: EditText,
        btnConnect: Button
    )
}