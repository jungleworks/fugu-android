package com.skeleton.mvp.fragment;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.skeleton.mvp.R;
import com.skeleton.mvp.model.UserReaction;

import java.util.ArrayList;

/**
 * Created by rajatdhamija
 * 09/04/18.
 */

public class ReactionFragment extends DialogFragment {
    UserReaction reactions;
    ArrayList<String> users = new ArrayList<>();
    RecyclerView recyclerView;

    public static ReactionFragment newInstance(int arg, UserReaction reactions) {
        ReactionFragment frag = new ReactionFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        frag.setReactions(reactions);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_reactions, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        recyclerView = view.findViewById(R.id.rvReactions);

        String name = "";
        for (int i = 0; i < reactions.getReaction().size(); i++) {
            for (int j = 0; j < reactions.getReaction().get(i).getFullNames().size(); j++) {
                if (j == 0) {
                    name = reactions.getReaction().get(i).getFullNames().get(j);
                } else if (j == 1) {
                    if (reactions.getReaction().get(i).getFullNames().size() == 2) {
                        name = name + " and " + reactions.getReaction().get(i).getFullNames().get(j);
                    } else {
                        name = name + " , " + reactions.getReaction().get(i).getFullNames().get(j);
                    }
                } else {
                    name = name + " and " + (reactions.getReaction().get(i).getFullNames().size() - 2) + " more";
                }
            }
            users.add(name);
            name = "";
        }
        return view;
    }

    public void setReactions(UserReaction reactions) {
        this.reactions = reactions;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, 1000);
    }

    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }
}