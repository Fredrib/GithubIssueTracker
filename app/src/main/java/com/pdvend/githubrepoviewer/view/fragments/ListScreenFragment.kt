package com.pdvend.githubrepoviewer.view.fragments

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pdvend.githubrepoviewer.R
import com.pdvend.githubrepoviewer.view.adapters.MyPagerAdapter

/**
 * A container fragment for the issues and pull request list fragments. This fragment was necessary
 * to create to allow both lists and detail fragments to be shown side by side on larger screens.
 */
class ListScreenFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list_screen, container, false)

        val tabsLayout: TabLayout = view.findViewById(R.id.tabs_main)
        val viewpager: ViewPager = view.findViewById(R.id.viewpager_main)

        // Set ViewPager
        val fragmentAdapter = activity?.applicationContext?.let {
            fragmentManager?.let {
                it1 -> MyPagerAdapter(it, it1, 1)
            }
        }
        viewpager.adapter = fragmentAdapter
        tabsLayout.setupWithViewPager(viewpager)

        return view
    }

}
