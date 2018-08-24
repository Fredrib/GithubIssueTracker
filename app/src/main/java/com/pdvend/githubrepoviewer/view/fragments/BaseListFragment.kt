package com.pdvend.githubrepoviewer.view.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.pdvend.githubrepoviewer.Injection
import com.pdvend.githubrepoviewer.R
import com.pdvend.githubrepoviewer.view.IssuesViewModel
import com.pdvend.githubrepoviewer.view.adapters.IssuesAdapter
import io.reactivex.disposables.Disposable

/**
 * A fragment representing a list of Items.
 */
open class BaseListFragment : Fragment() {

    protected lateinit var viewModel: IssuesViewModel
    protected val adapter = IssuesAdapter()

    private var subscribe: Disposable? = null

    private var columnCount = 1

    private lateinit var noSearchText: String
    private lateinit var noResultsText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        noSearchText = context.getString(R.string.no_search)
        noResultsText = context.getString(R.string.no_results)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_issue_list, container, false)

        // get the view model
        activity?.let {
            viewModel = ViewModelProviders.of(it, Injection.provideViewModelFactory(it))
                    .get(IssuesViewModel::class.java)
        }

        val list: RecyclerView = view.findViewById(R.id.list)
        val emptyList: TextView = view.findViewById(R.id.emptyList)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)

        // Set the adapter
        list.let {
            with(it) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

                val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                addItemDecoration(decoration)

                initAdapterAndUI(it, emptyList, progressBar)
            }
        }

        return view
    }

    open fun initAdapterAndUI(list: RecyclerView, emptyList: TextView, progressBar: ProgressBar) {
        list.adapter = adapter

        activity?.let { it ->
            viewModel.queryLiveData.observe(it, Observer<String> {
                updateEmptyText(emptyList, it.isNullOrBlank())
            })

            viewModel.isLoadingFromNetwork.observe(it, Observer<Boolean>{
                // Just show the progress bar if the list is empty, toa void showing the progressBar
                // when fetching more pages
                if (adapter.itemCount == 0) {
                    updateProgressBar(progressBar, it!!)
                } else{
                    updateProgressBar(progressBar, false)
                }
            })
        }

        // Subscribe to observe item click
        subscribe = adapter.clickEvent
                .subscribe {
                    viewModel.select(it)
                }
    }

    protected fun showEmptyList(list: RecyclerView, emptyList: TextView, show: Boolean) {
        if (show) {
            emptyList.visibility = View.VISIBLE
            list.visibility = View.GONE
        } else {
            emptyList.visibility = View.GONE
            list.visibility = View.VISIBLE
        }
    }

    /**
     * Update the empty text depending if is the list is empty because the request return with no
     * result or if a search was not made yet.
     */
    private fun updateEmptyText(emptyList: TextView, isEmptyQuery: Boolean){
        if (isEmptyQuery){
            emptyList.text = noSearchText
        } else {
            emptyList.text = noResultsText
        }
    }

    private fun updateProgressBar(progressBar: ProgressBar, isLoading: Boolean){
        if (isLoading){
            progressBar.visibility = View.VISIBLE
        } else{
            progressBar.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        subscribe?.dispose()
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"
    }

}
