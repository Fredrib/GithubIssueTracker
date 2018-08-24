package com.pdvend.githubrepoviewer.view.fragments

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pdvend.githubrepoviewer.Injection
import com.pdvend.githubrepoviewer.R
import com.pdvend.githubrepoviewer.model.Issue
import com.pdvend.githubrepoviewer.view.IssuesViewModel
import ru.noties.markwon.Markwon

/**
 * A fragment to show details of the issue or pull request.
 */
class DetailFragment : Fragment() {

    private lateinit var viewModel: IssuesViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        // get the view model
        activity?.let {
            viewModel = ViewModelProviders.of(it, Injection.provideViewModelFactory(it))
                    .get(IssuesViewModel::class.java)
        }

        val cardView: CardView = view.findViewById(R.id.cardView)
        val titleTV: TextView  = view.findViewById(R.id.title)
        val numberTV: TextView  = view.findViewById(R.id.number)
        val descriptionTV: TextView  = view.findViewById(R.id.description)

        viewModel.selected.observe(this, Observer<Issue>{issue ->
            if (issue != null) {
                // load and show pane
                cardView.visibility = View.VISIBLE
                titleTV.text = issue.title
                numberTV.text = "#"+ String.format("%d", issue.number)

                // Parser the text Markdown
                issue.description.let { Markwon.setMarkdown(descriptionTV, it) }
            } else{
                // Clear and hide pane
                cardView.visibility = View.INVISIBLE
                titleTV.text = ""
                numberTV.text = ""
                descriptionTV.text = ""
            }

        })

        return view
    }

}
