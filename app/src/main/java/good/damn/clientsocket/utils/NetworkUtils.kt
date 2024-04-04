package good.damn.clientsocket.utils

import android.util.Log
import good.damn.clientsocket.Application
import java.io.ByteArrayOutputStream
import java.io.InputStream

class NetworkUtils {
    companion object {
        private const val TAG = "NetworkUtils"

        fun readBytes(
            inp: InputStream
        ): ByteArray {
            return readBytes(inp, Application.BUFFER_MB)
        }

        fun readBytes(
            inp: InputStream,
            buffer: ByteArray
        ): ByteArray {

            val outArr = ByteArrayOutputStream()

            var n: Int

            while (true) {
                Log.d(TAG, "readBytes: AVAILABLE ${inp.available()} ${outArr.size()}")
                n = inp.read(buffer)
                Log.d(TAG, "readBytes: READ $n ${outArr.size()}")
                if (n == -1) {
                    break
                }
                outArr.write(buffer,0,n)
            }

            Log.d(TAG, "readBytes: READY")
            val data = outArr.toByteArray()
            Log.d(TAG, "readBytes: ${data.size}")
            outArr.close()
            Log.d(TAG, "readBytes: returning")
            return data
        }

    }
}