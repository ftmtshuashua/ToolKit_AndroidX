package support.lfp.toolkit.widget;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

/**
 * <pre>
 * Tip:
 *      FragmentTabViewManager 的数据适配器
 *
 * Function:
 *
 * Created by LiFuPing on 2020/10/13.
 * </pre>
 */
public abstract class FragmentTabAdapter {

    private FragmentManager mManager;
    private DataSetObserver mDataSetObserver;
    private Fragment mCurrentPrimaryItem;

    public FragmentTabAdapter(FragmentManager manager) {
        this.mManager = manager;
    }


//    //设置数量
//    public abstract int getCount();

    public long getItemId(int position) {
        return position;
    }

    public void setFragmentTabObserver(DataSetObserver observer) {
        mDataSetObserver = observer;
    }

    private FragmentTransaction mCurrTransaction;

    private FragmentTransaction getTransaction() {
        if (mCurrTransaction == null) {
            mCurrTransaction = mManager.beginTransaction();
        }
        return mCurrTransaction;
    }

    //设置当前显示的Fragment
    public final void setPrimaryItem(ViewGroup container, int position, Fragment fragment) {
        if (fragment != null && fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                getTransaction().setMaxLifecycle(mCurrentPrimaryItem, Lifecycle.State.STARTED);
            }
            fragment.setMenuVisibility(true);
            getTransaction().setMaxLifecycle(fragment, Lifecycle.State.RESUMED);
            mCurrentPrimaryItem = fragment;
        }
    }

    //初始化Fragment
    public final Fragment initFragment(ViewGroup container, int position) {
        String fragment_tag = makeFragmentName(container.getId(), getItemId(position));
        Fragment fragment = mManager.findFragmentByTag(fragment_tag);

        if (fragment != null) {
            getTransaction().attach(fragment);
        } else {
            fragment = newInstance(position);
            getTransaction().add(container.getId(), fragment, fragment_tag);
        }

        if (fragment != mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
            getTransaction().setMaxLifecycle(fragment, Lifecycle.State.STARTED);
        }


//        View view = mCurrentFragment.getView();
//        if (view != null) {
//            scrollTo(view.getLeft(), 0);
//        }


        return fragment;
    }

    public void startUpdate(@NonNull ViewGroup container) {

    }

    //刷新
    public void finishUpdate(@NonNull ViewGroup container) {
        if (mCurrTransaction != null) {
            mCurrTransaction.commitNowAllowingStateLoss();
            mCurrTransaction = null;
        }
    }


    public abstract Fragment newInstance(int position);

    //通知数据变化
    public void notifyDataSetChanged() {
        synchronized (this) {
            if (mDataSetObserver != null) {
                mDataSetObserver.onChanged();
            }
        }
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:tab:" + viewId + ":" + id;
    }

    public boolean isViewFromObject(View child, Fragment object) {
        return child == object.getView();
    }

    public void destroyItem(FragmentTabViewManager fragmentTabViewManager, int position, Fragment fragment) {
        getTransaction().detach(fragment);
        if (fragment == mCurrentPrimaryItem) {
            mCurrentPrimaryItem = null;
        }
    }
}
