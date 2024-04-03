package good.damn.clientsocket.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import good.damn.clientsocket.views.ClientView

class MainActivity
    : AppCompatActivity() {

    private lateinit var mClientView: ClientView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mClientView = ClientView(
            this
        )
        setContentView(mClientView)
    }

}