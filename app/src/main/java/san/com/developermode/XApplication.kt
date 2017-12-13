package san.com.developermode

import android.app.Application
import com.zrhx.base.BaseAppUtils
import com.zrhx.base.BaseConfig

class XApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        BaseAppUtils.init(BaseConfig(this, "", "", "", "", "", ""))

    }
}