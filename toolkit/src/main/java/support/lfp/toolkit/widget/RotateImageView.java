package support.lfp.toolkit.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

/**
 * <pre>
 * Tip:
 *      RotateImageView 带有自动旋转动画
 *
 * Function:
 *      setEnableAnimation()                   :设置动画启用状态
 *      setDuration()                          :设置旋转一圈的时间
 *
 * Created by LiFuPing on 2020/10/13.
 * </pre>
 */
public class RotateImageView extends androidx.appcompat.widget.AppCompatImageView {
    public RotateImageView(Context context) {
        super(context);
    }

    public RotateImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    long duration = 1500;           //一次动画持续时间
    boolean mEnableAnimation = true;  //是否启用动画

    /**
     * 设置动画启用状态
     */
    public void setEnableAnimation(boolean enable) {
        mEnableAnimation = enable;
        refresh();
    }

    /**
     * 设置旋转一圈的时间
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mEnableAnimation) {
            float rate = (getDrawingTime() % duration) / (float) duration;
            canvas.save();
            canvas.rotate(-rate * 360, getWidth() / 2, getHeight() / 2);
            super.onDraw(canvas);
            canvas.restore();
            refresh();
        } else {
            super.onDraw(canvas);
        }
    }


    private final void refresh() {
        if (Build.VERSION.SDK_INT >= 16) {
            postInvalidateOnAnimation();
        } else {
            postInvalidate();
        }
    }


}
