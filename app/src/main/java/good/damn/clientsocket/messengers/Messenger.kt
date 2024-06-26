package good.damn.clientsocket.messengers

import android.os.Handler
import android.os.Looper
import android.widget.TextView
import good.damn.clientsocket.Application
import java.nio.charset.Charset

class Messenger {

    private var mTextView: TextView? = null
    private var mIndex = 0
    private var messages = ""

    fun addMessage(
        text: String,
        indexed: Boolean = true
    ) {
        messages += if (indexed)
            "${mIndex++}) $text\n"
        else "$text\n"

        mTextView?.let {
            Application.ui {
                it.text = messages
            }
        }
    }

    fun setTextView(
        textView: TextView?
    ) {
        mTextView = textView
    }

    fun getMessages(): String {
        return messages
    }

}