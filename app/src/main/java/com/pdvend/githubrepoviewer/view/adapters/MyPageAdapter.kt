package com.pdvend.githubrepoviewer.view.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.pdvend.githubrepoviewer.R
import com.pdvend.githubrepoviewer.view.fragments.IssueListFragment
import com.pdvend.githubrepoviewer.view.fragments.PullRequestListFragment

/**
 * ViewPager Adapter which holds the list fragments.
 */
class MyPagerAdapter(
        context: Context,
        fm: FragmentManager,
        private val columnCount: Int)
    : FragmentPagerAdapter(fm) {

    private val tabIssuesTitle: String = context.resources.getString(R.string.tab_issues)
    private val tabPullRequestsTitle: String = context.resources.getString(R.string.tab_pull_requests)

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                IssueListFragment.newInstance(columnCount)
            }
            else -> {
                return PullRequestListFragment.newInstance(columnCount)
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> tabIssuesTitle
            else -> {
                return tabPullRequestsTitle
            }
        }
    }
}