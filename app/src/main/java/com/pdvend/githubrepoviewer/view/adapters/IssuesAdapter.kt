package com.pdvend.githubrepoviewer.view.adapters

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pdvend.githubrepoviewer.R
import com.pdvend.githubrepoviewer.model.Issue
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Adapter for the list of issues and pull requests.
 */
class IssuesAdapter : PagedListAdapter<Issue, RecyclerView.ViewHolder>(ISSUE_COMPARATOR) {

    // Create a subject which emits the recyclerView item selection and can be subscribed by outside
    // classes.
    private val clickSubject = PublishSubject.create<Issue>()

    // Exposes the Subject
    val clickEvent : Observable<Issue> = clickSubject

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.issue_view_item, parent, false)
        return IssueViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val issueItem = getItem(position)
        issueItem?.let { (holder as IssueViewHolder).bind(issueItem) }
    }

    inner class IssueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val title: TextView = itemView.findViewById(R.id.issueTitle)
        private val number: TextView = itemView.findViewById(R.id.issueNumber)

        private var issue: Issue? = null

        init {
            // Emit the click event
            itemView.setOnClickListener {
                issue?.let { itIssue -> clickSubject.onNext(itIssue) }
            }
        }

        fun bind(issue: Issue?){
            if (issue == null){
                val resources = itemView.resources
                title.text = resources.getString(R.string.loading)
                number.visibility = View.GONE
            } else {
                loadInfo(issue)
            }
        }

        private fun loadInfo(issue : Issue){
            this.issue = issue
            title.text = issue.title
            number.text = "#" + String.format("%d", issue.number)
            number.visibility = View.VISIBLE
        }
    }


    companion object {
        /**
         * Comparator object to assist the PagedListAdapter in differentiate list items
         */
        private val ISSUE_COMPARATOR = object : DiffUtil.ItemCallback<Issue>() {
            override fun areItemsTheSame(oldItem: Issue, newItem: Issue): Boolean =
                    oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Issue, newItem: Issue): Boolean =
                    oldItem == newItem
        }
    }
}