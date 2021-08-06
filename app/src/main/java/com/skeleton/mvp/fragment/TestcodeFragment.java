package com.skeleton.mvp.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.skeleton.mvp.R;


public class TestcodeFragment extends DialogFragment implements View.OnClickListener {

    private Button codeSubmitButton;
    private EditText enterCode;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container);
        codeSubmitButton = view.findViewById(R.id.codeSubmit);
        enterCode = view.findViewById(R.id.EnterCode);
        codeSubmitButton.setOnClickListener(this);
        return view;


    }

    public static TestcodeFragment newInstance(int arg) {
        TestcodeFragment frag = new TestcodeFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;

    }


    @Override
    public void onClick(View view) {

        String value = enterCode.getText().toString();
        if (value.equals("000000")) {
            openNewFragment();
        } else {
            Toast.makeText(getActivity(), "Invalid Code!",
                    Toast.LENGTH_LONG).show();
        }

    }

    public void openNewFragment() {
//        FragmentManager manager = (TestcodeFragment.this.getChildFragmentManager());
//        FragmentTransaction ft = manager.beginTransaction();
        ChangeEnvironment newFragment = ChangeEnvironment.newInstance(0);
        newFragment.show(getFragmentManager(), "ChangeEnvironment");
        dismiss();
    }

}