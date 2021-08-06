package com.skeleton.mvp.fragment;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.skeleton.mvp.R;
import com.skeleton.mvp.model.UserReaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajatdhamija
 * 12/04/18.
 */

public class EmojiReactionsDialog extends BottomSheetFragment {
    UserReaction reactions;
    ArrayList<String> users = new ArrayList<>();
    RecyclerView recyclerView;

    public static EmojiReactionsDialog newInstance(int arg, UserReaction reactions) {
        EmojiReactionsDialog frag = new EmojiReactionsDialog();
        Bundle args = new Bundle();
        frag.setArguments(args);
        frag.setReactions(reactions);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_reaction_emoji, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ViewPager viewPager = view.findViewById(R.id.emojipager);
        setupViewPager(viewPager);
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        return view;
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

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        AllReactionsFragment fragment = AllReactionsFragment.newInstance(1, reactions);
        int count = 0;
        for (int i = 0; i < reactions.getReaction().size(); i++) {
            count = count + reactions.getReaction().get(i).getUsers().size();
        }
        adapter.addFrag(fragment, "ALl(" + count + ")");

        for (int i = 0; i < reactions.getReaction().size(); i++) {
            EmojiReactionsFragment fragment2 = EmojiReactionsFragment.newInstance(1, reactions.getReaction().get(i));
            adapter.addFrag(fragment2, reactions.getReaction().get(i).getReaction() + " " + reactions.getReaction().get(i).getUsers().size());
        }
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

    public void setReactions(UserReaction reactions) {
        this.reactions = reactions;
    }

    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }
}
