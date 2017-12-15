package san.com.developermode

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.accessibility.AccessibilityManager
import com.zrhx.base.utils.ToastUtils
import san.com.developermode.utils.handCaptureData
import san.com.developermode.utils.needReques
import san.com.developermode.utils.requestCapturePermission


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (needReques()) {
            requestCapturePermission(this)
        }
    }

    private fun jumpToSettingPage() {
        isStartAccessibilityService()
        sAccessibilityService ?: run {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (!needReques()) {
            return
        }
        if (!handCaptureData(requestCode, resultCode, data)) {
            ToastUtils.showShort("失败")
        } else {
            startService(Intent(this, HelperService::class.java))
            jumpToSettingPage()
        }
    }

    private fun isStartAccessibilityService() {
        val accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val accessibilityServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
        for (info in accessibilityServices) {
            Log.e("dd", info.id)
        }
    }
}
