package san.com.developermode

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zrhx.base.BaseAppUtils
import com.zrhx.base.BaseConfig

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseAppUtils.init(BaseConfig(application,"","","","","",""))
        startService(Intent(this, HelperService::class.java))
    }
}
