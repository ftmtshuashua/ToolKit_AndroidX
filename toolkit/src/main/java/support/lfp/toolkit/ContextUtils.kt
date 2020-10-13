package  support.lfp.toolkit

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.GenericLifecycleObserver
import androidx.lifecycle.Lifecycle


/**
 * <pre>
 * Tip:
 *      上下文管理工具
 *
 * Function:
 *      setAdapter()                   :设置适配器
 *
 * Created by LiFuPing on 2020/10/13.
 * </pre>
 */
object ContextUtils {

    /**
     * 从上下文获得生命周期管理器
     */
    fun getLifecycle(context: Context): Lifecycle? {
        val activity = getActivity(context)
        if (activity != null) {
            if (activity is FragmentActivity) {
                return activity.lifecycle
            }
        }
        return null
    }

    fun getActivity(context: Context): Activity? {

        val limit = 15
        var result: Context? = context
        if (result is Activity) {
            return result
        }
        var tryCount = 0
        while (result is ContextWrapper) {
            if (result is Activity) {
                return result
            }
            if (tryCount > limit) {
                //break endless loop
                return null
            }
            result = result.baseContext
            tryCount++
        }
        return null
    }

    fun getComponentActivity(from: Context): ComponentActivity? {
        val activity: Activity? = getActivity(from)
        return if (activity != null && activity is ComponentActivity) {
            activity
        } else null
    }

    /**
     * 绑定上下文的生命周期
     */
    fun bindLifecycle(
        context: Context
        , ON_DESTROY: (() -> Unit)? = null
        , ON_ANY: (() -> Unit)? = null
        , ON_CREATE: (() -> Unit)? = null
        , ON_START: (() -> Unit)? = null
        , ON_STOP: (() -> Unit)? = null
        , ON_RESUME: (() -> Unit)? = null
        , ON_PAUSE: (() -> Unit)? = null
    ) {

        getLifecycle(context)?.let {
            it.addObserver((GenericLifecycleObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_DESTROY -> {
                        ON_DESTROY?.invoke()
                    }
                    Lifecycle.Event.ON_ANY -> {
                        ON_ANY?.invoke()
                    }
                    Lifecycle.Event.ON_CREATE -> {
                        ON_CREATE?.invoke()
                    }
                    Lifecycle.Event.ON_START -> {
                        ON_START?.invoke()
                    }
                    Lifecycle.Event.ON_STOP -> {
                        ON_STOP?.invoke()
                    }
                    Lifecycle.Event.ON_RESUME -> {
                        ON_RESUME?.invoke()
                    }
                    Lifecycle.Event.ON_PAUSE -> {
                        ON_PAUSE?.invoke()
                    }
                }
            }))
        }
    }


    /**
     * 判断Activity是否活着
     */
    fun isActivityAlive(act: Activity?): Boolean {
        if (act == null) return false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            !act.isFinishing && !act.isDestroyed
        } else {
            !act.isFinishing
        }
    }

    /**判断Activity是否存在*/
    fun isActivityExists(
        pkg: String,
        cls: String
    ): Boolean {
        val intent = Intent()
        intent.setClassName(pkg, cls)

        val app = AppUtils.getApp<Application>()

        return !(app.getPackageManager()
            .resolveActivity(intent, 0) == null || intent.resolveActivity(
            app.getPackageManager()
        ) == null || app.getPackageManager().queryIntentActivities(intent, 0).size === 0)
    }


    fun toActivity(context: Context?): Activity? {
        return context2Activity(context)
    }

    fun toApplication(context: Context?): Application {
        return context2Application(context)
    }

    //Context转Activity
    private fun context2Activity(context: Context?): Activity? {
        requireNotNull(context) { "You cannot start a load on a null Context" }
        if (context is FragmentActivity) {
            return context
        } else if (context is Activity) {
            return context
        } else if (context is ContextWrapper && context.baseContext.applicationContext != null) {
            return context2Activity(context.baseContext)
        }
        return null
    }

    //Context转Application
    private fun context2Application(context: Context?): Application {
        requireNotNull(context) { "You cannot start a load on a null Context" }
        return if (context is Application) {
            context
        } else context.applicationContext as Application
    }


}