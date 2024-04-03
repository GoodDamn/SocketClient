package good.damn.clientsocket

class Application
: android.app.Application() {

    companion object {
        var BUFFER_MB = ByteArray(1024*1024)
    }
}