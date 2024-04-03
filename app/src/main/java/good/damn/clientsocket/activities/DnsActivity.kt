package good.damn.clientsocket.activities

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import good.damn.clientsocket.views.ClientView

class DnsActivity
    : AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )

        setContentView(
            ClientView(this)
        )

    }

}