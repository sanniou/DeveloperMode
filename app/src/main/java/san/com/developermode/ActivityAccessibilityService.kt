package san.com.developermode

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.zrhx.base.utils.LogUtils


class ActivityAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
    }

    override fun onInterrupt() {

    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // 此方法是在主线程中回调过来的，所以消息是阻塞执行的
        // 获取包名
        val pkgName = event.packageName.toString()
        val eventType = event.eventType
        // AccessibilityOperator封装了辅助功能的界面查找与模拟点击事件等操作
        AccessibilityOperator().updateEvent(this, event)
        LogUtils.e("eventType: $eventType pkgName: $pkgName")
        when (eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
            }
        }
    }
}