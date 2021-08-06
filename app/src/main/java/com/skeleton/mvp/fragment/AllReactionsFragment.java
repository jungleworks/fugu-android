package com.skeleton.mvp.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skeleton.mvp.R;
import com.skeleton.mvp.adapter.EmojiReactionAdapter;
import com.skeleton.mvp.model.EmojiReactions;
import com.skeleton.mvp.model.UserReaction;

import java.util.ArrayList;

/**
 * Created by rajatdhamija on 12/04/18.
 */

public class AllReactionsFragment extends Fragment {
    UserReaction reactions;
    RecyclerView recyclerView;
    ArrayList<EmojiReactions> reactionsArrayList = new ArrayList<>();

    public static AllReactionsFragment newInstance(int arg, UserReaction reactions) {
        AllReactionsFragment frag = new AllReactionsFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        frag.setReactions(reactions);
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_reactions, container, false);
        recyclerView = view.findViewById(R.id.rvReactions);
        reactionsArrayList = getList();
        EmojiReactionAdapter emojiReactionAdapter = new EmojiReactionAdapter(reactionsArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(emojiReactionAdapter);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        return view;
    }

    private ArrayList<EmojiReactions> getList() {
        ArrayList<EmojiReactions> reactionsList = new ArrayList<>();
        for (int i = 0; i < reactions.getReaction().size(); i++) {
            for (int j = 0; j < reactions.getReaction().get(i).getUsers().size(); j++) {
                reactionsList.add(new EmojiReactions(reactions.getReaction().get(i).getReaction(), reactions.getReaction().get(i).getFullNames().get(j)));
            }
        }
        return reactionsList;
    }

    public void setReactions(UserReaction reactions) {
        this.reactions = reactions;
    }
}
