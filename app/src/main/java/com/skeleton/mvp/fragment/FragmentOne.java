package com.skeleton.mvp.fragment;

import android.content.Context;
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
import com.skeleton.mvp.model.Emoji;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by rajatdhamija on 09/04/18.
 */

public class FragmentOne extends Fragment {

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
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("emojis.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        try {
            JSONObject obj = new JSONObject(json);
            JSONObject emo = new JSONObject(json);
            JSONArray m_jArry = obj.getJSONArray("emojis");
            JSONArray emojiArray = m_jArry.getJSONObject(0).getJSONArray("emojis");


            for (int i = 0; i < emojiArray.length(); i++) {
                JSONObject jo_inside = emojiArray.getJSONObject(i);
                Emoji location = new Emoji(jo_inside.getString("unicode"), jo_inside.getString("name"));
                emojiList.add(location);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return emojiList;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
}
