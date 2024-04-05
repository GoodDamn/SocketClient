package good.damn.clientsocket

import android.os.Handler
import android.os.Looper
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class Application
: android.app.Application() {

    companion object {
        val BUFFER_MB = ByteArray(1024*1024)
        val BUFFER_300 = ByteArray(300)
        val CHARSET = Charset.forName("UTF-8")
        val CHARSET_ASCII = Charset.forName("US-ASCII")
        private val HANDLER_MAIN = Handler(
            Looper.getMainLooper()
        )

        fun ui(
            execute: Runnable
        ) {
            HANDLER_MAIN.post(
                execute
            )
        }
    }
}