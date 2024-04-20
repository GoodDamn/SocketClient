package good.damn.clientsocket.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import good.damn.clientsocket.activities.clients.*

class MainActivity
    : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = this

        val layout = LinearLayout(
            context
        )

        layout.orientation = LinearLayout
            .VERTICAL

        addClientButton(
            "DNS",
            DnsActivity::class.java,
            layout
        )

        addClientButton(
            "TCP",
            ShareProtocolActivity::class.java,
            layout
        )

        addClientButton(
            "DHCP",
            DhcpActivity::class.java,
            layout
        )

        addClientButton(
            "SSH",
            SSHActivity::class.java,
            layout
        )

        setContentView(
            layout
        )
    }

    private fun addClientButton(
        text: String,
        activityClick: Class<*>,
        layout: LinearLayout
    ) {
        val context = layout.context
        val btnServer = Button(
            context
        )

        btnServer.text = text

        btnServer.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    activityClick
                )
            )
        }

        layout.addView(
            btnServer,
            -1,
            -2
        )
    }
}