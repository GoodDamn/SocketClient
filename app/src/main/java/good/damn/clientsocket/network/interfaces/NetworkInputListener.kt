package good.damn.clientsocket.network.interfaces

import androidx.annotation.WorkerThread
import java.nio.charset.Charset

interface NetworkInputListener {

    fun input(
        data: ByteArray
    ): Boolean {

        if (data.size < 2) {
            return false
        }

        val charset = Charset
            .forName("UTF-8")

        when (data[0].toInt()) {
            1 -> { // F (File)
                val nameSize = data[1].toUByte()
                val fileName = String(data,
                    2,
                    nameSize.toInt(),
                    charset)

                onGetFile(
                    data,
                    nameSize.toInt()+2,
                    fileName)
            }
            2 -> { // T (Text)
                val msgSize = data[1].toUByte()
                val msg = String(data,
                    2,
                    msgSize.toInt(),
                    charset)
                onGetText(msg)
            }

            71 -> { // G (GET) http
                onHttpGet()
            }
        }

        return true
    }

    @WorkerThread
    fun onGetFile(data: ByteArray,offset:Int,fileName: String)

    @WorkerThread
    fun onGetText(msg: String)

    @WorkerThread
    fun onHttpGet()

}