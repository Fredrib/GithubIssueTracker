package com.pdvend.githubrepoviewer.data;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.pdvend.githubrepoviewer.api.GithubService;
import com.pdvend.githubrepoviewer.database.GithubCache;
import com.pdvend.githubrepoviewer.model.Issue;
import com.pdvend.githubrepoviewer.model.IssueSearchResult;

public class GithubRepository {

    private static final int DATABASE_PAGE_SIZE = 20;

    private final GithubService service;
    private final GithubCache cache;

    public GithubRepository(GithubService service, GithubCache cache) {
        this.service = service;
        this.cache = cache;
    }

    /**
     * Search issues and pull requests in a batch.
     * @param repoName Repository full name (e.g. owner/repository)
     * @return The search result object [IssueSearchResult]
     */
    @SuppressWarnings("unchecked")
    public IssueSearchResult search(String repoName){

        DataSource.Factory issuesFactory = cache.issuesByRepoName(repoName);
        DataSource.Factory pullRequestsFactory = cache.pullRequestByRepoName(repoName);

        IssueBoundaryCallback boundaryCallback = new IssueBoundaryCallback(repoName, service, cache);
        LiveData<String> networkErrors = boundaryCallback.getNetworkErrors();
        LiveData<String> queryErrors = boundaryCallback.getQueryErrors();
        LiveData<Boolean> isLoading = boundaryCallback.isLoading();

        LiveData<PagedList<Issue>> issues = new LivePagedListBuilder(issuesFactory, DATABASE_PAGE_SIZE)
                .setBoundaryCallback(boundaryCallback)
                .build();

        LiveData<PagedList<Issue>> pullRequests = new LivePagedListBuilder(pullRequestsFactory, DATABASE_PAGE_SIZE)
                .setBoundaryCallback(boundaryCallback)
                .build();

        return new IssueSearchResult(issues, pullRequests, networkErrors, queryErrors, isLoading);
    }
}
