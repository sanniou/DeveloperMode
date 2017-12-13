package san.com.developermode.utils

import android.content.Context
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.media.projection.MediaProjectionManager
import android.os.Build

fun requestCapturePermission(context: Context) {
    //5.0 之后才允许使用屏幕截图
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        return
    }
    val mediaProjectionManager = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    context.startActivity(mediaProjectionManager.createScreenCaptureIntent().apply { flags = FLAG_ACTIVITY_NEW_TASK })
}