package com.skeleton.mvp.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class BottomBarAdapter(fragmentManager: FragmentManager) : SmartFragmentStatePagerAdapter(fragmentManager) {
    private val fragments = ArrayList<Fragment>()
    override fun getCount(): Int {
        return fragments.size
    }

    fun getFragments(): ArrayList<Fragment> {
        return fragments
    }

    fun addFragments(fragment: Fragment) {
        fragments.add(fragment)
    }

    override fun getItem(position: Int): Fragment {
        return fragments.get(position)
    }

    fun removeFragment(position: Int) {
        fragments.removeAt(position)
    }
}