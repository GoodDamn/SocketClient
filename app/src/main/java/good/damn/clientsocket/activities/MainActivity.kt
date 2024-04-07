package good.damn.clientsocket.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import good.damn.clientsocket.views.ClientView

class MainActivity
    : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = this
        val btnDns = Button(
            context
        )

        val btnIPPort = Button(
            context
        )

        val btnDhcp = Button(
            context
        )

        val layout = LinearLayout(
            context
        )

        layout.orientation = LinearLayout
            .VERTICAL

        btnDns.text = "DNS"
        btnIPPort.text = "IP:Port"
        btnDhcp.text = "DHCP"

        btnDns.setOnClickListener(
            this::onClickBtnDns
        )

        btnIPPort.setOnClickListener(
            this::onClickBtnIPPort
        )

        btnDhcp.setOnClickListener(
            this::onClickBtnDhcp
        )

        layout.addView(
            btnDns,
            -1,
            -2
        )

        layout.addView(
            btnIPPort,
            -1,
            -2
        )

        layout.addView(
            btnDhcp,
            -1,
            -2
        )

        setContentView(
            layout
        )
    }

    private fun onClickBtnDns(
        view: View
    ) {
        val intent = Intent(
            this,
            DnsActivity::class.java
        )

        startActivity(intent)
    }

    private fun onClickBtnIPPort(
        view: View
    ) {
        val intent = Intent(
            this,
            IPPortActivity::class.java
        )

        startActivity(intent)
    }

    private fun onClickBtnDhcp(
        view: View
    ) {
        val intent = Intent(
            this,
            DhcpActivity::class.java
        )

        startActivity(intent)
    }
}