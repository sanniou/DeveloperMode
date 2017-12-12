package san.com.developermode

import android.animation.ValueAnimator
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.zrhx.base.multitype.binder.BaseViewBinder
import com.zrhx.base.utils.ScreenUtils
import com.zrhx.base.utils.ToastUtils
import com.zrhx.base.widget.recyclerview.LViewHolder
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter


class HelperService : Service() {

    private val NOTIFY_ID = 79
    private lateinit var mWindowManager: WindowManager

    private lateinit var mView: RecyclerView

    private var startX = 0

    private var startY = 0

    private var swipeX = 0

    private var swipeY = 0

    private lateinit var items: Items

    private lateinit var adapter: MultiTypeAdapter

    private var touchSlop: Int = 0

    private var screenWidth: Int = 0

    private lateinit var notification: Notification

    private lateinit var playerReceiver: BroadcastReceiver

    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()
        touchSlop = ViewConfiguration.get(this).scaledTouchSlop
        screenWidth = ScreenUtils.getScreenWidth()
        swipeY = ScreenUtils.getScreenHeight() / 2
        bindForeground()
        show()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        mWindowManager.removeView(mView)
        unregisterReceiver(playerReceiver)
    }

    private val play = "play"

    private fun bindForeground() {
        playerReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                ToastUtils.showShort("${intent.action}dddd")
                stopSelf()
            }

        }
        val mFilter = IntentFilter(play)
        registerReceiver(playerReceiver, mFilter)
        val requestCode = 100
        // 指定操作意图--设置对应的行为ACTION
        val intentPlay = Intent(play)
        // 取一个PendingIntent
        val pIntentPlay = PendingIntent.getBroadcast(applicationContext, requestCode, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT)
        notification = Notification.Builder(applicationContext)
                .addAction(R.drawable.ic_launcher_foreground, "关闭", pIntentPlay) // #0
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText("Helper Notify")
                .setContentTitle("title")
                .setNumber(88)
                .setOngoing(true)
                .setWhen(System.currentTimeMillis())
                .build()
        startForeground(NOTIFY_ID, notification)
    }

    private lateinit var layoutParams: WindowManager.LayoutParams

    private fun show() {
        mWindowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val w = WindowManager.LayoutParams.WRAP_CONTENT
        val h = WindowManager.LayoutParams.WRAP_CONTENT

        val flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //解决Android 7.1.1起不能再用Toast的问题（先解决crash）
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                WindowManager.LayoutParams.TYPE_PHONE
            } else {
                WindowManager.LayoutParams.TYPE_TOAST
            }
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }


        layoutParams = WindowManager.LayoutParams(w, h, type, flags, PixelFormat.TRANSLUCENT)
        layoutParams.gravity = Gravity.TOP or Gravity.START
        layoutParams.y = swipeY
        layoutParams.x = swipeX
        mView = View.inflate(this, R.layout.pop_view, null) as RecyclerView

        // display content
        items = Items()
        items.add("Helper")
        items.add("Helper")
        items.add("Helper")
        items.add("Helper")
        adapter = MultiTypeAdapter(items)
        adapter.register(Item::class.java, object : BaseViewBinder<Item>(R.layout.item_helper_main) {
            override fun onInitViewHolder(p0: LViewHolder) {
                p0.itemView.setOnTouchListener { _, event ->
                    p0.itemView.parent.requestDisallowInterceptTouchEvent(true)
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            startX = event.rawX.toInt()
                            startY = event.rawY.toInt()
                            p0.itemView.setBackgroundResource(R.color.config_orange)
                        }
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            p0.itemView.setBackgroundResource(R.color.config_blue)
                            if (swipeX > 0 || swipeY > 0) {
                                release()
                            }
                        }
                        MotionEvent.ACTION_MOVE -> {
                            val thisX = event.rawX.toInt()
                            val thisY = event.rawY.toInt()
                            swipeX += thisX - startX
                            swipeY += thisY - startY
                            startX = thisX
                            startY = thisY
                            updateView()
                        }
                    }
                    true
                }
            }

            override fun onBindLViewHolder(p0: LViewHolder, p1: Item) {
                p0.setText(R.id.helper_main_text, p1.name)
            }
        })
        mView.layoutManager = LinearLayoutManager(this)
        mView.adapter = adapter
        mWindowManager.addView(mView, layoutParams)
    }

    private fun updateView() {
        layoutParams.x = swipeX
        layoutParams.y = swipeY
        // 使参数生效
        mWindowManager.updateViewLayout(mView, layoutParams)
    }

    private fun release() {
        ValueAnimator.ofInt(swipeX, if (swipeX > screenWidth / 2) screenWidth else 0)
                .run {
                    addUpdateListener { animation ->
                        swipeX = animation.animatedValue as Int
                        updateView()
                    }
                    start()
                }
    }

}