package support.lfp.toolkit.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import support.lfp.toolkit.R;
import support.lfp.toolkit.action.Action1;

/**
 * <pre>
 * Tip:
 *      带有联动功能的ScrollView
 *
 * Function:
 *      setGroup()                   :设置分组
 *
 * Created by LiFuPing on 2020/10/13.
 * </pre>
 */
public class LinkageNestedScrollView extends NestedScrollView {

    public LinkageNestedScrollView(@NonNull Context context) {
        super(context);
    }

    public LinkageNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LinkageNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    /**
     * 联动组件 根据该字段确定分组
     */
    private String mGroup = "LinkageNestedScrollViewDefaultGroup";


    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LinkageNestedScrollView);
            String group = array.getString(R.styleable.LinkageNestedScrollView_group);
            if (!TextUtils.isEmpty(group)) {
                setGroup(group);
            }
        }
    }

    /**
     * 设置分组，相同分组的LinkageNestedScrollView会带有联动效果
     * @param group
     */
    public void setGroup(String group) {
        this.mGroup = group;
    }

    private static final Map<String, LinkageControl> mLinkageControl = new HashMap<>();
    private OnScrollChangeListener mOnScrollChangeListener;

    private void registerOnScrollChangeListener(OnScrollChangeListener listener) {
        mOnScrollChangeListener = listener;
    }

    private void unregisterOnScrollChangeListener() {
        mOnScrollChangeListener = null;
    }

    @Override
    protected void onScrollChanged(int l, int current_y, int oldl, int old_y) {
        super.onScrollChanged(l, current_y, oldl, old_y);
//        LogUtils.e(MessageFormat.format("滚动到:{0,number,0}", current_y));

        if (mOnScrollChangeListener != null) {
            mOnScrollChangeListener.onScrollChange(this, l, current_y, oldl, old_y);
        }
    }

    private LinkageControl getLinkageControl() {
        LinkageControl linkageControl = mLinkageControl.get(mGroup);
        if (linkageControl == null) {
            linkageControl = new LinkageControl();
            mLinkageControl.put(mGroup, linkageControl);
        }
        return linkageControl;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        getLinkageControl().setTarget(this);
        return super.onTouchEvent(ev);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getLinkageControl().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getLinkageControl().unregister(this);
    }


    /**
     * 联动控制器
     */
    private static final class LinkageControl implements OnScrollChangeListener {
        Source mSource = new StrongReferenceSource();

        int mCurrentScroll_Y = 0;

        /**
         * 将联动注册联动View
         *
         * @param v
         */
        public void register(View v) {
            mSource.register(v);
            setScrollY(v, mCurrentScroll_Y);
        }

        //设置滚动值
        private void setScrollY(final View view, final int current_y) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    if (view != mSource.getTarget()) view.setScrollY(current_y);
                }
            });
        }


        public void setTarget(LinkageNestedScrollView linkageNestedScrollView) {
            View target = mSource.getTarget();
            if (target == linkageNestedScrollView) return;

            if (target != null) ((LinkageNestedScrollView) target).unregisterOnScrollChangeListener();
            mSource.setTarget(linkageNestedScrollView);
            linkageNestedScrollView.registerOnScrollChangeListener(this);
        }

        @Override
        public void onScrollChange(View v, int scrollX, final int scrollY, int oldScrollX, int oldScrollY) {
            mCurrentScroll_Y = scrollY;
            mSource.forEach(new Action1<View>() {
                @Override
                public void call(View view) {
                    setScrollY(view, scrollY);
                }
            });
        }

        public void unregister(View v) {
            mSource.unregister(v);
        }

        //-------------------------- 资源控制器 ----------------------

        private interface Source {

            boolean isContains(View v);

            void register(View v);

            void unregister(View v);

            View getTarget();

            void setTarget(LinkageNestedScrollView linkageNestedScrollView);

            void forEach(Action1<View> action);

            int size();

        }

        private static final class WeakReferenceSource implements Source {
            WeakReference<LinkageNestedScrollView> mTarget;
            List<WeakReference<View>> mRegisterList = new ArrayList<>();

            @Override
            public boolean isContains(View v) {
                for (WeakReference<View> reference : mRegisterList) {
                    if (reference.get() == v) return true;
                }
                return false;
            }

            @Override
            public void register(View v) {
                if (!isContains(v)) {
//                    LogUtils.e(MessageFormat.format("联动View注册 <- {0}", v));
                    WeakReference<View> reference = new WeakReference<>(v);
                    mRegisterList.add(reference);
                }
            }

            @Override
            public void unregister(View v) {
                for (int i = mRegisterList.size() - 1; i >= 0; i--) {
                    View view = mRegisterList.get(i).get();
                    if (view == v) {
                        WeakReference<View> remove = mRegisterList.remove(i);
                        remove.clear();
//                        LogUtils.i(MessageFormat.format("联动View移除 <- {0}", v));
                    }
                }
            }

            @Override
            public View getTarget() {
                return mTarget == null ? null : mTarget.get() == null ? null : mTarget.get();
            }

            @Override
            public void setTarget(LinkageNestedScrollView linkageNestedScrollView) {
                mTarget = new WeakReference<>(linkageNestedScrollView);
            }

            @Override
            public void forEach(Action1<View> action) {
                for (int i = mRegisterList.size() - 1; i >= 0; i--) {
                    WeakReference<View> reference = mRegisterList.get(i);
                    View view = reference.get();
                    if (view == null) {
                        mRegisterList.remove(i);
                    } else {
                        action.call(view);
                    }
                }
            }

            @Override
            public int size() {
                return mRegisterList.size();
            }

        }

        private static final class StrongReferenceSource implements Source {
            LinkageNestedScrollView mTarget;
            List<View> mRegisterList = new ArrayList<>();

            @Override
            public boolean isContains(View v) {
                return mRegisterList.contains(v);
            }

            @Override
            public void register(View v) {
                if (!isContains(v)) mRegisterList.add(v);
            }

            @Override
            public void unregister(View v) {
                mRegisterList.remove(v);
            }

            @Override
            public View getTarget() {
                return mTarget == null ? null : mTarget;
            }

            @Override
            public void setTarget(LinkageNestedScrollView linkageNestedScrollView) {
                mTarget = linkageNestedScrollView;
            }

            @Override
            public void forEach(Action1<View> action) {
                for (int i = mRegisterList.size() - 1; i >= 0; i--) {
                    View view = mRegisterList.get(i);
                    if (view == null) {
                        mRegisterList.remove(i);
                    } else {
                        action.call(view);
                    }
                }
            }

            @Override
            public int size() {
                return mRegisterList.size();
            }
        }


    }

    private interface OnScrollChangeListener {
        void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY);
    }
}
