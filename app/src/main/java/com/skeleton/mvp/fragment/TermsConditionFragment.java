package com.skeleton.mvp.fragment;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.skeleton.mvp.R;

public class TermsConditionFragment extends DialogFragment {
    private WebView webView;
    private String url;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.terms_and_condition, container);
        webView = view.findViewById(R.id.wvTermsAndConditions);
        webView.loadUrl(url);
        return view;


    }

    public static TermsConditionFragment newInstance(int arg, String url) {
        TermsConditionFragment frag = new TermsConditionFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        frag.setUrl(url);
        return frag;
    }

    private void setUrl(String url) {
        this.url = url;
    }


    @Override
    public void onStart() {
        super.onStart();
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}
