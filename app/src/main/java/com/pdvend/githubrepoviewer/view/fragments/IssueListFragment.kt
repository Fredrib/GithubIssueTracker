package com.pdvend.githubrepoviewer.view.fragments

import android.arch.lifecycle.Observer
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.widget.ProgressBar
import android.widget.TextView
import com.pdvend.githubrepoviewer.model.Issue

/**
 * Simple list fragment which observe issues from local cache.
 */
class IssueListFragment : BaseListFragment() {

    override fun initAdapterAndUI(list: RecyclerView, emptyList: TextView, progressBar: ProgressBar) {
        super.initAdapterAndUI(list, emptyList, progressBar)
        activity?.let { it ->
            viewModel.issues.observe(it, Observer<PagedList<Issue>> {
                showEmptyList(list, emptyList,it?.size == 0)
                adapter.submitList(it)
            })
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(columnCount: Int) =
                IssueListFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}