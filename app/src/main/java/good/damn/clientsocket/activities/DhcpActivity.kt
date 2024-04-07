package good.damn.clientsocket.activities

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import good.damn.clientsocket.listeners.view.ClientViewListener
import good.damn.clientsocket.network.DhcpConnection
import good.damn.clientsocket.views.ClientView
import java.util.Objects

class DhcpActivity
    : AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )

        val connection = DhcpConnection()
        val a = Any()

        connection.start(
            a
        )

    }


}