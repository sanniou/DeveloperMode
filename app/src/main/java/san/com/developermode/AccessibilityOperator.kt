//package san.com.developermode
//
//import android.accessibilityservice.AccessibilityService
//import android.content.Context
//import android.os.Build
//import android.view.accessibility.AccessibilityEvent
//import android.view.accessibility.AccessibilityNodeInfo
//import com.zrhx.base.utils.LogUtils
//
//
//class AccessibilityOperator(private var mContext: Context, private var mAccessibilityEvent: AccessibilityEvent, private var mAccessibilityService) {
//
//
//    // 建议使用getRootInActiveWindow，这样不依赖当前的事件类型
//    // 下面这个必须依赖当前的AccessibilityEvent
//    //            nodeInfo = curEvent.getSource();
//    private fun rootNodeInfo(): AccessibilityNodeInfo? {
//        val curEvent = mAccessibilityEvent
//        var nodeInfo: AccessibilityNodeInfo? = null
//        if (mAccessibilityService != null) {
//            nodeInfo = mAccessibilityService!!.rootInActiveWindow
//            LogUtils.e("nodeInfo: " + nodeInfo!!)
//        }
//
//        return nodeInfo
//    }
//
//    fun init(context: Context) {
//        mContext = context
//    }
//
//    fun updateEvent(service: AccessibilityService?, event: AccessibilityEvent?) {
//        if (service != null && mAccessibilityService == null) {
//            mAccessibilityService = service
//        }
//        if (event != null) {
//            mAccessibilityEvent = event
//        }
//    }
//
//    /**
//     * 根据Text搜索所有符合条件的节点, 模糊搜索方式
//     */
//    fun findNodesByText(text: String): List<AccessibilityNodeInfo>? {
//        val nodeInfo = rootNodeInfo()
//        return nodeInfo?.findAccessibilityNodeInfosByText(text)
//    }
//
//    /**
//     * 根据View的ID搜索符合条件的节点,精确搜索方式;
//     * 这个只适用于自己写的界面，因为ID可能重复
//     * api要求18及以上
//     * @param viewId
//     */
//    fun findNodesById(viewId: String): List<AccessibilityNodeInfo>? {
//        val nodeInfo = rootNodeInfo()
//        if (nodeInfo != null) {
//            if (Build.VERSION.SDK_INT >= 18) {
//                return nodeInfo.findAccessibilityNodeInfosByViewId(viewId)
//            }
//        }
//        return null
//    }
//
//    fun clickByText(text: String): Boolean {
//        return performClick(findNodesByText(text))
//    }
//
//    /**
//     * 根据View的ID搜索符合条件的节点,精确搜索方式;
//     * 这个只适用于自己写的界面，因为ID可能重复
//     * api要求18及以上
//     * @param viewId
//     * @return 是否点击成功
//     */
//    fun clickById(viewId: String): Boolean {
//        return performClick(findNodesById(viewId))
//    }
//
//    private fun performClick(nodeInfos: List<AccessibilityNodeInfo>?): Boolean {
//        if (nodeInfos != null && !nodeInfos.isEmpty()) {
//            var node: AccessibilityNodeInfo
//            for (i in nodeInfos.indices) {
//                node = nodeInfos[i]
//                // 获得点击View的类型
//                LogUtils.e("View类型：" + node.className)
//                // 进行模拟点击
//                if (node.isEnabled) {
//                    return node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
//                }
//            }
//        }
//        return false
//    }
//
//    fun clickBackKey(): Boolean {
//        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
//    }
//
//    private fun performGlobalAction(action: Int): Boolean {
//        return mAccessibilityService!!.performGlobalAction(action)
//    }
//
//}