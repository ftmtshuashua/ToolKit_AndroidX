package support.lfp.toolkit.canvas;

import android.view.View;

import androidx.core.math.MathUtils;
import androidx.core.view.ViewCompat;

/**
 * View动画进度管理
 */
public class ViewAnimationProgressManager {
    private long duration = 1000;
    private View mView;
    private AnimationProgressValue mValue;

    public ViewAnimationProgressManager(View view) {
        mView = view;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setFloat(float start, float end) {
        mValue = new FloatProgress(start, end);
    }

    public void setInt(int start, int end) {
        mValue = new IntProgress(start, end);
    }

    /**
     * 获得当前进度 (0f ~ 1f)
     */
    public float getProgress() {
        return (mView.getDrawingTime() % duration) / (float) duration;
    }

    public Object getValue() {
        if (mValue == null) mValue = new FloatProgress(0f, 1f);
        return mValue.get(getProgress());
    }

    public void invalidate() {
        ViewCompat.postInvalidateOnAnimation(mView);
    }


    private interface AnimationProgressValue<T> {
        T get(float progress);
    }

    private static final class FloatProgress implements AnimationProgressValue<Float> {
        float start;
        float end;

        public FloatProgress(float start, float end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public Float get(float progress) {
            return (end - start) * MathUtils.clamp(progress, 0f, 1f) + start;
        }

    }

    private static final class IntProgress implements AnimationProgressValue<Integer> {
        int start;
        int end;

        public IntProgress(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public Integer get(float progress) {
            float clamp = MathUtils.clamp(progress, 0f, 1f);

            float v = (end - start) * clamp + start;
            return (int) v;
        }

    }

}
