package san.com.developermode

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.TextView
import com.zrhx.base.utils.ScreenUtils
import com.zrhx.base.utils.ToastUtils
import com.zrhx.base.widget.recyclerview.BaseClickableAdapter
import com.zrhx.base.widget.recyclerview.LViewHolder


var sAccessibilityService: ActivityAccessibilityService? = null

var showWindow = false

class ActivityAccessibilityService : AccessibilityService() {
    private var windowAdded = false

    private val windowList = mutableListOf<CharSequence>()

    private lateinit var mWindow: RecyclerView

    private lateinit var adapter: BaseClickableAdapter<CharSequence>

    private lateinit var mWindowManager: WindowManager

    private lateinit var layoutParams: WindowManager.LayoutParams

    override fun onServiceConnected() {
        super.onServiceConnected()
        ToastUtils.showShort("onServiceConnected")
        sAccessibilityService = this
        mWindow = RecyclerView(this)
        mWindow.layoutManager = LinearLayoutManager(this)
        adapter = object : BaseClickableAdapter<CharSequence>(windowList, R.layout.item_text) {
            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): LViewHolder {
                val holder = super.onCreateViewHolder(parent, viewType)
                holder.getView<TextView>(R.id.item_text).run {
                    setBackgroundResource(R.color.config_gray)
                    setPadding(0, 0, 0, 0)
                }
                return holder
            }

            override fun onBindHolder(p0: LViewHolder, p1: CharSequence) {
                p0.setText(R.id.item_text, p1)
            }
        }
        mWindow.adapter = adapter
        mWindowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val w = WindowManager.LayoutParams.WRAP_CONTENT
        val h = ScreenUtils.getScreenHeight() / 3

        val flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        val type =//解决Android 7.1.1起不能再用Toast的问题
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                    WindowManager.LayoutParams.TYPE_PHONE
                } else {
                    WindowManager.LayoutParams.TYPE_TOAST
                }
        layoutParams = WindowManager.LayoutParams(w, h, type, flags, PixelFormat.TRANSLUCENT)
        layoutParams.gravity = Gravity.TOP or Gravity.START
    }

    override fun onInterrupt() {

    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // 此方法是在主线程中回调过来的，所以消息是阻塞执行的
        // 获取包名
        val pkgName = event.packageName.toString()
        val eventType = event.eventType
        when (eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            AccessibilityEvent.TYPE_WINDOWS_CHANGED -> {
                windowList.add(event.className)
                updateWindow()
            }
        }
    }

    fun updateWindow() {
        if (showWindow) {
            if (!windowAdded) {
                windowAdded = true
                mWindowManager.addView(mWindow, layoutParams)
            }
            adapter.notifyItemInserted(windowList.size - 1)
            mWindow.smoothScrollToPosition(windowList.size - 1)
        } else {
            if (windowAdded) {
                windowAdded = false
                mWindowManager.removeView(mWindow)
            }
        }
    }
}