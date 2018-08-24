package com.pdvend.githubrepoviewer.model

import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList

/**
 * IssueSearchResult from a search, which contains LiveData<List<Issue>> holding query issueRequests,
 * and a LiveData<String> of network error state.
 */
data class IssueSearchResult(
        val issueRequests: LiveData<PagedList<Issue>>,
        val pullRequests : LiveData<PagedList<Issue>>,
        val networkErrors: LiveData<String>,
        val queryErrors: LiveData<String>,
        val isLoading: LiveData<Boolean>
)