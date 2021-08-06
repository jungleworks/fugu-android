package com.skeleton.mvp.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skeleton.mvp.R;
import com.skeleton.mvp.adapter.EmojiAdapter;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.model.Emoji;

import java.util.ArrayList;

/**
 * Created by rajatdhamija on 09/04/18.
 */

public class FragmentRecent extends Fragment {
    private ArrayList<String> emojiMap = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        RecyclerView rvEmoji = view.findViewById(R.id.rvEmoji);
        EmojiAdapter emojiAdapter = new EmojiAdapter(loadJSONFromAsset(), getActivity());
        rvEmoji.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        rvEmoji.setAdapter(emojiAdapter);
        ViewCompat.setNestedScrollingEnabled(rvEmoji, false);
        return view;
    }

    public ArrayList<Emoji> loadJSONFromAsset() {
        ArrayList<Emoji> emojiList = new ArrayList<>();
        emojiMap = CommonData.getEmojiMap();
        for (int i = emojiMap.size() - 1; i > -0; i--) {
            emojiList.add(new Emoji(emojiMap.get(i), emojiMap.get(i)));
        }
        return emojiList;
    }
}
