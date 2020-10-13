package support.lfp.toolkit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import support.lfp.toolkit.LogUtils
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * <pre>
 * Tip:
 *      自动导入布局资源文件
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/5 09:00
 * </pre>
 */


@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class BindView(val value: Int)


/**
 * View 绑定工具
 */
object BindViewUtils {

    fun <T : Any> getBindView(tagert: T, inflater: LayoutInflater, container: ViewGroup?): View? {
        val cls: Class<*> = tagert::class.java
        val annotation: Annotation? = cls.getAnnotation(BindView::class.java)
        if (annotation != null) {
            val layout_resouce: Int = (annotation as BindView).value
            try {
                return inflater.inflate(layout_resouce, container, false)
            } catch (e: Exception) {
                LogUtils.e(e)
            }
        }
        return null
    }
}
