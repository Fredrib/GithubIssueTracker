package com.pdvend.githubrepoviewer.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PagedList
import com.pdvend.githubrepoviewer.api.GithubService
import com.pdvend.githubrepoviewer.api.searchIssues
import com.pdvend.githubrepoviewer.database.GithubCache
import com.pdvend.githubrepoviewer.model.Issue

/**
 * BoundaryCallback implementation to automatically fetch new items on network when the local cache
 * has run out of items.
 */
class IssueBoundaryCallback(
        private val query: String,
        private val service: GithubService,
        private val cache: GithubCache
) : PagedList.BoundaryCallback<Issue>(){

    // keep the last requested page.
    // When the request is successful, increment the page number.
    private var lastRequestedPage = 1


    // LiveData of Request status.
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    // LiveData of network errors.
    private val _networkErrors = MutableLiveData<String>()
    val networkErrors: LiveData<String>
        get() = _networkErrors

    // LiveData of query errors.
    private val _queryErrors = MutableLiveData<String>()
    val queryErrors: LiveData<String>
        get() = _queryErrors

    // avoid triggering multiple requests at the same time
    // Cannot rely on observing the _isLoafing live data to update this, as this class is not life cycle
    // aware, meaning it would have to be managed manually
    private var isRequestInProgress = false

    init {
        _isLoading.postValue(isRequestInProgress)
    }

    override fun onZeroItemsLoaded() {
        requestAndSaveData(query)
    }

    override fun onItemAtEndLoaded(itemAtEnd: Issue) {
        requestAndSaveData(query)
    }

    private fun requestAndSaveData(query: String) {
        // Ignore the request if another request in progress or query is empty
        if (isRequestInProgress || query.isBlank() ) return

        isRequestInProgress = true
        _isLoading.postValue(isRequestInProgress)

        searchIssues(service,
                query,
                lastRequestedPage,
                NETWORK_PAGE_SIZE,
                {
                    // Successful Response
                    repos -> cache.insert(repos) {
                        lastRequestedPage++
                        isRequestInProgress = false
                        _isLoading.postValue(isRequestInProgress)
                    }
                },
                {
                    // There was a network error
                    networkError -> _networkErrors.postValue(networkError)
                            isRequestInProgress = false
                            _isLoading.postValue(isRequestInProgress)
                },
                {
                    // The query was invalid
                    queryError -> _queryErrors.postValue(queryError)
                            isRequestInProgress = false
                            _isLoading.postValue(isRequestInProgress)
                }
        )
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 50
    }

}