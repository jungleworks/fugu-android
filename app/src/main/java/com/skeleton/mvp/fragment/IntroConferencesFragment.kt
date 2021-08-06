package com.skeleton.mvp.fragment

/********************************
Created by Amandeep Chauhan     *
Date :- 26/04/2020              *
 ********************************/

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.skeleton.mvp.R

class IntroConferencesFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_intro_conferences, container, false)
    }
}