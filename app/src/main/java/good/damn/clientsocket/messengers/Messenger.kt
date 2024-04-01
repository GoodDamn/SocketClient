package good.damn.clientsocket.messengers

import android.os.Handler
import android.os.Looper
import android.widget.TextView
import java.nio.charset.Charset

class Messenger {

    private val mHandler = Handler(Looper.getMainLooper())
    private val mCharset = Charset.forName("UTF-8")

    private var mTextView: TextView? = null
    private var mIndex = 0
    private var messages = ""

    fun addMessage(
        text: String
    ) {
        messages += "${mIndex++}) $text\n"
        mTextView?.let {
            mHandler.post {
                it.text = messages
            }
        }
    }

    fun setTextView(
        textView: TextView?
    ) {
        mTextView = textView
    }

    fun getCharset(): Charset {
        return mCharset
    }

    fun getMessages(): String {
        return messages
    }

}