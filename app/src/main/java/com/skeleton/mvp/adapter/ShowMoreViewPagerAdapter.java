package com.skeleton.mvp.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.skeleton.mvp.fragment.ShowMoreDocumentFragment;
import com.skeleton.mvp.fragment.ShowMoreImageFragment;
import com.skeleton.mvp.fragment.ShowMoreVideoFragment;

public class ShowMoreViewPagerAdapter extends FragmentPagerAdapter {


    public ShowMoreViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new ShowMoreImageFragment();
        }
        else if (position == 1)
        {
            fragment = new ShowMoreVideoFragment();
        }
        else if (position == 2)
        {
            fragment = new ShowMoreDocumentFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
        {
            title = "Images";
        }
        else if (position == 1)
        {
            title = "Videos";
        }
        else if (position == 2)
        {
            title = "Documents";
        }
        return title;
    }
}
