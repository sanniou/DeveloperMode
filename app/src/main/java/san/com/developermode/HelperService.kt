package san.com.developermode

import android.animation.ValueAnimator
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import com.zrhx.base.multitype.binder.BaseViewBinder
import com.zrhx.base.widget.recyclerview.LViewHolder
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

class HelperService : Service() {

    private lateinit var mWindowManager: WindowManager

    private lateinit var mView: RecyclerView
    private val mStartX = 0
    private val mStartY = 0

    private var mSwipeX = 0
    private var mSwipeY = 0

    private lateinit var mItems: Items

    private lateinit var mAdapter: MultiTypeAdapter

    override fun onBind(intent: Intent?) = null

    private var mTouchSlop: Int = 0

    override fun onCreate() {
        super.onCreate()
        show()
        mTouchSlop = ViewConfiguration.get(this).scaledTouchSlop
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        mWindowManager.removeView(mView)
    }


    fun show() {
        mWindowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val w = WindowManager.LayoutParams.WRAP_CONTENT
        val h = WindowManager.LayoutParams.WRAP_CONTENT

        val flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //解决Android 7.1.1起不能再用Toast的问题（先解决crash）
            if (Build.VERSION.SDK_INT > 24) {
                WindowManager.LayoutParams.TYPE_PHONE
            } else {
                WindowManager.LayoutParams.TYPE_TOAST
            }
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }


        val layoutParams = WindowManager.LayoutParams(w, h, type, flags, PixelFormat.TRANSLUCENT)
        layoutParams.gravity = Gravity.TOP or Gravity.START

        mView = View.inflate(this, R.layout.pop_view, null) as RecyclerView

        // display content
        mItems = Items()
        mItems.add("Helper")
        mAdapter = MultiTypeAdapter(mItems)
        mAdapter.register(String::class.java, object : BaseViewBinder<String>(R.layout.item_helper_main) {
            override fun onInitViewHolder(p0: LViewHolder) {
                p0.itemView.setOnTouchListener { _, event ->
                    p0.itemView.parent.requestDisallowInterceptTouchEvent(true)
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            p0.itemView.setBackgroundResource(R.color.config_orange)
                        }
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            p0.itemView.setBackgroundResource(R.color.config_blue)
                            release()
                        }
                        MotionEvent.ACTION_MOVE -> {
                            // 当前值以屏幕左上角为原点
                            mSwipeX = event.rawX.toInt() - mTouchSlop
                            mSwipeY = event.rawY.toInt() - mTouchSlop
                            updateView()
                        }
                        else -> {
                        }
                    }
                    true
                }
            }

            private fun updateView() {
                layoutParams.x = mSwipeX
                layoutParams.y = mSwipeY
                // 使参数生效
                mWindowManager.updateViewLayout(mView, layoutParams)
            }

            private fun release() {
                ValueAnimator.ofInt(mSwipeX, mStartX)
                        .run {
                            addUpdateListener { animation ->
                                mSwipeX = animation.animatedValue as Int
                                updateView()
                            }
                            start()
                        }
            }

            override fun onBindLViewHolder(p0: LViewHolder, p1: String) {
                p0.setText(R.id.helper_main_text, p1)
            }
        })
        mView.layoutManager = LinearLayoutManager(this)
        mView.adapter = mAdapter
        mWindowManager.addView(mView, layoutParams)
    }
}