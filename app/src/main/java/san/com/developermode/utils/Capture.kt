package san.com.developermode.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.util.DisplayMetrics
import android.view.WindowManager
import com.zrhx.base.utils.ScreenUtils
import com.zrhx.base.utils.Utils
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

val R_CODE: Int = 788

fun needReques() = intent == null || result == 0

fun requestCapturePermission(activity: Activity) {
    val mediaProjectionManager = activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    activity.startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), R_CODE)
}

fun handCaptureData(requestCode: Int, resultCode: Int, data: Intent?): Boolean =
        (requestCode == R_CODE && resultCode == Activity.RESULT_OK && data != null).apply {
            result = resultCode
            intent = data
        }


var intent: Intent? = null

var result: Int = 0

class Capture {

    private var dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy_MM_dd_hh_mm_ss")

    private var strDate: String

    private var pathImage: String

    private var nameImage: String

    private var mMediaProjectionManager: MediaProjectionManager

    private var windowWidth: Int = 0

    private var windowHeight: Int = 0

    private var mScreenDensity: Int = 0

    private var count = AtomicInteger(0)

    init {
        strDate = dateFormat.format(Date())
        pathImage = Utils.getApp().externalCacheDir.absolutePath + "/Pictures/"
        File(pathImage).mkdir()
        nameImage = pathImage + strDate + ".png"
        mMediaProjectionManager = Utils.getApp()
                .getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val windowManager1 = Utils.getApp()
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowWidth = ScreenUtils.getScreenWidth()
        windowHeight = ScreenUtils.getScreenHeight()
        val metrics = DisplayMetrics()
        windowManager1.defaultDisplay.getMetrics(metrics)
        mScreenDensity = metrics.densityDpi
    }


    private lateinit var mVirtualDisplay: VirtualDisplay
    private lateinit var mMediaProjection: MediaProjection

    fun setUpMediaProjection() {
        if (count.incrementAndGet() == 1) {
            val imageReader = ImageReader.newInstance(windowWidth, windowHeight, PixelFormat.RGBA_8888, 2)
            imageReader.setOnImageAvailableListener({ reader ->
                if (count.decrementAndGet() < 0) {
                    count.set(0)
                } else {
                    startCapture(reader)
                }
            }, null)
            mMediaProjection = mMediaProjectionManager.getMediaProjection(result, intent)
            mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror", windowWidth, windowHeight, mScreenDensity,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    imageReader.surface, null, null)
        }
    }

    private fun startCapture(reader: ImageReader) {
        strDate = dateFormat.format(java.util.Date())
        nameImage = pathImage + strDate + ".png"
        val image = reader.acquireLatestImage()
        val width = image.width
        val height = image.height
        val planes = image.planes
        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * width
        var bitmap: Bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(buffer)
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height)
        image.close()
        val fileImage = File(nameImage)
        if (!fileImage.exists()) {
            fileImage.createNewFile()
        }
        val out = FileOutputStream(fileImage)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
        out.close()
        if (count.get() == 0) {
            mVirtualDisplay.release()
            mMediaProjection.stop()
            reader.setOnImageAvailableListener(null, null)
            reader.close()
        }
    }
}

