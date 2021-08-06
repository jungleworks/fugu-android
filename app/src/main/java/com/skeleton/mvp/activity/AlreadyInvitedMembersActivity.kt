package com.skeleton.mvp.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.skeleton.mvp.R
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse
import com.skeleton.mvp.fragment.AcceptedFragment
import com.skeleton.mvp.fragment.PendingFragment
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.util.SearchAnimationToolbar
import java.util.*


class AlreadyInvitedMembersActivity : BaseActivity(), View.OnClickListener, SearchAnimationToolbar.OnSearchQueryChangedListener {

    lateinit var recyclerView: RecyclerView
    lateinit var fcCommonResponse: FcCommonResponse
    private var toolbar: SearchAnimationToolbar? = null
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private var searchString = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_already_invited_members)
        initView()

    }

    fun initView() {
        toolbar = findViewById(R.id.toolbar)
        toolbar?.setSupportActionBar(this)
        toolbar?.setOnSearchQueryChangedListener(this)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        viewPager = findViewById(R.id.viewpager)
        tabLayout = findViewById(R.id.tabs)
        tabLayout.setupWithViewPager(viewPager)
        setupViewPager(viewPager)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    val page = supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.currentItem)
                    (page as PendingFragment).setFilteredList(searchString)
                } else {
                    val page = supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.currentItem)
                    (page as AcceptedFragment).setFilteredList(searchString)
                }
            }

        })
    }


    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        val allGroupsFragment = PendingFragment()
        val joinedGroupsFragment = AcceptedFragment()
        adapter.addFragment(allGroupsFragment, "Pending")
        adapter.addFragment(joinedGroupsFragment, "Accepted")
        viewPager.adapter = adapter
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }
    }

    override fun onSearchCollapsed() {
    }

    override fun onSearchQueryChanged(query: String?) {
        searchString = query!!
        val page = supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.currentItem)
        if (page != null) {
            if (page is PendingFragment) {
                page.setFilteredList(query)
            } else {
                (page as AcceptedFragment).setFilteredList(query)
            }
        }
    }

    override fun onSearchExpanded() {
    }

    override fun onSearchSubmitted(query: String?) {
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val itemId = item.itemId

        if (itemId == R.id.action_search) {
            toolbar?.onSearchIconClick()
            return true
        } else if (itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {

        val handledByToolbar = toolbar?.onBackPressed()

        if (!handledByToolbar!!) {
            super.onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        if (v!!.id == R.id.ivBack) {
            onBackPressed()
        }
    }

}


