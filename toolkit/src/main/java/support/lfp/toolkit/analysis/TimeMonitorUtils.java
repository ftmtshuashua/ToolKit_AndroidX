package support.lfp.toolkit.analysis;

import android.text.TextUtils;

import java.text.MessageFormat;

/**
 * 耗时监控工具
 *
 * 启动检查：adb shell am start -W com.weather.interest/v2.weather.app.ui.splash.SplashActivity2
 * 启动检查：adb shell am start -W com.hopenebula.tools.clean/com.hopenebula.tools.clean.activity.SplashActivity
 *
 */
public class TimeMonitorUtils {
    private String mTag;

    private long mTime;

    private TimeMonitorUtils(String tag) {
        mTag = tag;
        dot();
    }


    public static final TimeMonitorUtils get(String tag) {
        return new TimeMonitorUtils(tag);
    }

    public void run(Runnable runnable) {
        long start = System.currentTimeMillis();
        if (runnable != null) runnable.run();
        log(MessageFormat.format("TimeMonitorUtils:{0} -> {1,number,0}ms", mTag, System.currentTimeMillis() - start));
    }

    public void dot() {
        mTime = System.currentTimeMillis();
    }

    public void look() {
        log(MessageFormat.format("TimeMonitorUtils:{0} -> {1,number,0}ms", mTag, System.currentTimeMillis() - mTime));
    }

    public void look(String tag) {
        if (TextUtils.isEmpty(tag)) {
            log(MessageFormat.format("TimeMonitorUtils:{0} -> {1,number,0}ms", mTag, System.currentTimeMillis() - mTime));
        } else {
            log(MessageFormat.format("TimeMonitorUtils:{0}-{2} -> {1,number,0}ms", mTag, System.currentTimeMillis() - mTime, tag));
        }
    }

    private void log(String log) {
        System.out.println(log);
    }


}
