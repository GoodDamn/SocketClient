package good.damn.clientsocket

import android.app.Activity
import android.net.Uri
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat

class ContentLauncher(
    activity: AppCompatActivity,
    callback: ActivityResultCallback<Uri?>)
    : ActivityResultLauncher<String>(){

    private var mContentBrowser: ActivityResultLauncher<String>

    init {
        mContentBrowser = activity.registerForActivityResult(
            ActivityResultContracts.GetContent(),
            callback)
    }

    override fun launch(
        input: String?,
        options: ActivityOptionsCompat?) {
        mContentBrowser.launch(input)
    }

    override fun unregister() {
        mContentBrowser.unregister()
    }

    override fun getContract(): ActivityResultContract<String, *> {
        return ActivityResultContracts.GetContent()
    }

}