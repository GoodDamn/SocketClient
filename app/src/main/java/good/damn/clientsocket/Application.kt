package good.damn.clientsocket

import android.os.Handler
import android.os.Looper
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class Application
: android.app.Application() {

    companion object {
        val BUFFER_MB = ByteArray(1024*1024)
        val CHARSET = Charset.forName("UTF-8")
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