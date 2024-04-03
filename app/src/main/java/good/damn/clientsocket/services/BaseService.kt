package good.damn.clientsocket.services

import android.content.Context

abstract class BaseService<DELEGATE>(
    val context: Context
) {
    var delegate: DELEGATE? = null
    abstract fun start()
}