package com.skeleton.mvp.fragment;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.skeleton.mvp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajatdhamija on 09/04/18.
 */

public class EmojiFragment extends BottomSheetDialogFragment {
    private int[] tabIcons = {
            0x1f642,
            0x1f4a1,
            0x1f3c8,
            0x1f33f,
            0x2708,
            0x2764,
            0x1f354,
            0x1f553
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_emoji, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ViewPager viewPager = view.findViewById(R.id.emojipager);
        setupViewPager(viewPager);
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(new FragmentRecent(), getEmojiByUnicode(tabIcons[7]));
        adapter.addFrag(new FragmentOne(), getEmojiByUnicode(tabIcons[0]));
        adapter.addFrag(new FragmentTwo(), getEmojiByUnicode(tabIcons[1]));
        adapter.addFrag(new FragmentThree(), getEmojiByUnicode(tabIcons[2]));
        adapter.addFrag(new FragmentFour(), getEmojiByUnicode(tabIcons[3]));
        adapter.addFrag(new FragmentFive(), getEmojiByUnicode(tabIcons[4]));
        adapter.addFrag(new FragmentSix(), getEmojiByUnicode(tabIcons[5]));
        adapter.addFrag(new FragmentSeven(), getEmojiByUnicode(tabIcons[6]));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final BottomSheetDialog bottomSheetDialog =
                (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        bottomSheetDialog.setOnShowListener(dialog -> {
            FrameLayout bottomSheet =
                    bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (null != bottomSheet) {
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setHideable(false);
                behavior.setPeekHeight(1000);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return bottomSheetDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar);
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = 1000;
            dialog.getWindow().setLayout(width, height);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            window.setAttributes(wlp);
        }
    }
}