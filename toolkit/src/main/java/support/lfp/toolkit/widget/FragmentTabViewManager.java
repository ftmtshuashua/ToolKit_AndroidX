package support.lfp.toolkit.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.customview.view.AbsSavedState;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;


/**
 * <pre>
 * Tip:
 *      仿ViewGroup的Fragment加载逻辑实现的 Fragment TAB 管理
 *      实现Fragment懒加载,当用户点击某TAB的时候才加载对应的Fragment
 *
 * Function:
 *      setAdapter()                   :设置适配器
 *
 * Created by LiFuPing on 2020/10/13.
 * </pre>
 */
public class FragmentTabViewManager extends FrameLayout {

    public FragmentTabViewManager(Context context) {
        super(context);
    }

    public FragmentTabViewManager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FragmentTabViewManager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    //适配器
    private FragmentTabAdapter mFragmentTabAdapter;
    private PagerObserver mObserver;
//    private int mExpectedAdapterCount;

    private int mRestoredCurItem = -1; //当前Item

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();
        int width = right - left;
        int height = bottom - top;
        for (int i = 0; i < count; i++) {

            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                ItemInfo ii;
                if ((ii = infoForChild(child)) != null) {
                    ii.offset = ii.position * width;
                    int childLeft = getPaddingLeft() + ii.offset;
                    int childTop = getPaddingTop();
                    int childRight = childLeft + child.getMeasuredWidth();
                    int childBottom = childTop + child.getMeasuredHeight();

                    child.layout(childLeft, childTop, childRight, childBottom);
                }
            }
        }
    }

    private void autoScrollToCurrentPage() {
        final ItemInfo currentItemInfo = getCurrentItemInfo();
        if (currentItemInfo != null) {
            final int width = getWidth();
            if (width == 0) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        scrollTo(currentItemInfo.position * width, 0);
                    }
                });
            } else {
                scrollTo(currentItemInfo.position * width, 0);
            }
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.position = mRestoredCurItem;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mRestoredCurItem = ss.position;
    }

    //设置当前Item
    public void setCurrentItem(int position) {
        if (mFragmentTabAdapter != null) {
            mRestoredCurItem = position;

//            int count = mFragmentTabAdapter.getCount();

            int curIndex = -1;
            ItemInfo curItem = null;
            for (curIndex = 0; curIndex < mItems.size(); curIndex++) {
                final ItemInfo ii = mItems.get(curIndex);
                if (ii.position >= mRestoredCurItem) {
                    if (ii.position == mRestoredCurItem) curItem = ii;
                    break;
                }
            }

//            if (curItem == null && count > 0) {
            if (curItem == null) {
                curItem = createItemInfo(mRestoredCurItem, curIndex);
            }

            if (curItem != null) {
                mFragmentTabAdapter.setPrimaryItem(this, position, curItem.object);
                mFragmentTabAdapter.finishUpdate(this);
            }


            autoScrollToCurrentPage();
        }
    }

    public void setAdapter(FragmentTabAdapter adapter) {
        if (mFragmentTabAdapter != null) {
            mFragmentTabAdapter.setFragmentTabObserver(null);
            mFragmentTabAdapter.startUpdate(this);
            for (int i = 0; i < mItems.size(); i++) {
                final ItemInfo ii = mItems.get(i);
                mFragmentTabAdapter.destroyItem(this, ii.position, ii.object);
            }
            mFragmentTabAdapter.finishUpdate(this);
            mItems.clear();
            mRestoredCurItem = -1;
            scrollTo(0, 0);
        }
        mFragmentTabAdapter = adapter;

        if (mFragmentTabAdapter != null) {
            if (mObserver == null) mObserver = new PagerObserver();
        }
        mFragmentTabAdapter.setFragmentTabObserver(mObserver);

//        mExpectedAdapterCount = mFragmentTabAdapter.getCount();

        if (mRestoredCurItem > 0) {
            setCurrentItem(mRestoredCurItem);
        }

    }

    private void dataSetChanged() {

    }


    private class PagerObserver extends DataSetObserver {
        PagerObserver() {
        }

        @Override
        public void onChanged() {
            dataSetChanged();
        }

        @Override
        public void onInvalidated() {
            dataSetChanged();
        }
    }


    public static class SavedState extends AbsSavedState {
        int position;
        Parcelable adapterState;
        ClassLoader loader;

        public SavedState(@NonNull Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(position);
            out.writeParcelable(adapterState, flags);
        }

        @Override
        public String toString() {
            return "FragmentPager.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " position=" + position + "}";
        }

        public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in, null);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        SavedState(Parcel in, ClassLoader loader) {
            super(in, loader);
            if (loader == null) {
                loader = getClass().getClassLoader();
            }
            position = in.readInt();
            adapterState = in.readParcelable(loader);
            this.loader = loader;
        }
    }


    private final ArrayList<ItemInfo> mItems = new ArrayList<ItemInfo>();


    ItemInfo createItemInfo(int position, int index) {
        ItemInfo ii = new ItemInfo();
        ii.position = position;
        ii.object = mFragmentTabAdapter.initFragment(this, position);
        if (index < 0 || index >= mItems.size()) {
            mItems.add(ii);
        } else {
            mItems.add(index, ii);
        }
        return ii;
    }

    ItemInfo infoForChild(View child) {
        for (int i = 0; i < mItems.size(); i++) {
            ItemInfo ii = mItems.get(i);
            if (mFragmentTabAdapter.isViewFromObject(child, ii.object)) {
                return ii;
            }
        }
        return null;
    }

    ItemInfo infoForPosition(int position) {
        for (int i = 0; i < mItems.size(); i++) {
            ItemInfo ii = mItems.get(i);
            if (ii.position == position) {
                return ii;
            }
        }
        return null;
    }

    ItemInfo getCurrentItemInfo() {
        int mRestoredCurItem = this.mRestoredCurItem;
        for (int i = 0; i < mItems.size(); i++) {
            ItemInfo ii = mItems.get(i);
            if (ii.position == mRestoredCurItem) {
                return ii;
            }
        }
        return null;
    }


    static class ItemInfo {
        Fragment object;
        int position;
        int offset;
//        boolean scrolling;
        //        float widthFactor;
//        float offset;
    }

}
