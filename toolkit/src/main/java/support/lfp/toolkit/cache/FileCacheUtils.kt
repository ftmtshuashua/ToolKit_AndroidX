package support.lfp.toolkit.cache

import android.app.Application
import support.lfp.toolkit.AppUtils
import support.lfp.toolkit.LogUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


/**
 * 文件缓存工具
 */
object FileCacheUtils {

    /**
     * 获得缓存文件
     */
    fun getCacheFile(filename: String?): File {
        return File(AppUtils.getApp<Application>().cacheDir, filename)
    }

    fun saveByte(bytes: ByteArray?, file: File): String? {
        if (file.exists()) {
            file.delete()
        }
        try {
            file.createNewFile()
            val fos = FileOutputStream(file)
            fos.write(bytes)
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            LogUtils.e(e)
            return null
        }
        return file.absolutePath
    }

    fun saveString(str: String, file: File): String? {
        return saveByte(str.toByteArray(), file)
    }

    fun readString(file: File): String {
        if (!file.exists()) return ""
        return FileInputStream(file).buffered().reader().readText()
    }
}