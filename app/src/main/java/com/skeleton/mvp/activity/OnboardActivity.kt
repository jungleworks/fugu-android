package com.skeleton.mvp.activity

/********************************
Created by Amandeep Chauhan     *
Date :- 26/04/2020              *
 ********************************/

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.skeleton.mvp.R
import com.skeleton.mvp.fragment.IntroAppsFragment
import com.skeleton.mvp.fragment.IntroChatsFragment
import com.skeleton.mvp.fragment.IntroConferencesFragment
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.ui.intro.IntroActivity

class OnboardActivity : BaseActivity() {

    private lateinit var introPager: ViewPager2
    private lateinit var btnGetStarted: android.widget.Button
    private lateinit var llChatsIndicators: LinearLayout
    private lateinit var llConferenceIndicators: LinearLayout
    private lateinit var llAppsIndicators: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard)
        btnGetStarted = findViewById(R.id.btnGetStartedOnboard)
        introPager = findViewById(R.id.vpOnboardIntro)
        llChatsIndicators = findViewById(R.id.llChatsIndicators)
        llConferenceIndicators = findViewById(R.id.llConferenceIndicators)
        llAppsIndicators = findViewById(R.id.llAppsIndicators)
        val pagerAdapter = IntroSlideAdapter(this)
        introPager.adapter = pagerAdapter
        introPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> setChatsActive()
                    1 -> setConferenceIActive()
                    else -> setAppsActive()
                }
            }
        })
        btnGetStarted.setOnClickListener {
            run {
                val start = Intent(this@OnboardActivity, IntroActivity::class.java)
                startActivity(start)
                finish()
            }
        }
    }


    fun setChatsActive() {
        llChatsIndicators.visibility = View.VISIBLE
        llConferenceIndicators.visibility = View.GONE
        llAppsIndicators.visibility = View.GONE
    }

    fun setConferenceIActive() {
        llChatsIndicators.visibility = View.GONE
        llConferenceIndicators.visibility = View.VISIBLE
        llAppsIndicators.visibility = View.GONE
    }

    fun setAppsActive() {
        llChatsIndicators.visibility = View.GONE
        llConferenceIndicators.visibility = View.GONE
        llAppsIndicators.visibility = View.VISIBLE
    }

    class IntroSlideAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

        private val fragments: ArrayList<Fragment> = ArrayList()

        init {
            fragments.add(IntroChatsFragment())
            fragments.add(IntroConferencesFragment())
            fragments.add(IntroAppsFragment())
        }

        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment = fragments[position]

    }
}
