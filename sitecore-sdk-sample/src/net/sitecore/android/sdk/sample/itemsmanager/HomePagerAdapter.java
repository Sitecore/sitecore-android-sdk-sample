package net.sitecore.android.sdk.sample.itemsmanager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HomePagerAdapter extends FragmentPagerAdapter {

    private ItemsListFragment mItemsFragment;
    private QueryFragment mQueryFragment;

    public HomePagerAdapter(FragmentManager fm, QueryFragment queryFragment, ItemsListFragment itemsFragment) {
        super(fm);

        mQueryFragment = queryFragment;
        mItemsFragment = itemsFragment;
    }

    @Override
    public Fragment getItem(int i) {
        if (i == 0) {
            return mQueryFragment;
        } else {
            return mItemsFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
