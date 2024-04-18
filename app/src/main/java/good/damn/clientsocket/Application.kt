package good.damn.clientsocket

import android.icu.text.ListFormatter.Width
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

        var WIDTH: Int = 0
        var HEIGHT: Int = 0

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


    override fun onCreate() {
        super.onCreate()

        val metrics = applicationContext
            .resources
            .displayMetrics

        WIDTH = metrics.widthPixels
        HEIGHT = metrics.heightPixels
    }
}