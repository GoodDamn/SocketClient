package good.damn.clientsocket.services

import android.content.Context

abstract class BaseService(
    val context: Context
) {
    abstract fun start()
}