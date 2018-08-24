package com.pdvend.githubrepoviewer.view;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;

import com.pdvend.githubrepoviewer.data.GithubRepository;
import com.pdvend.githubrepoviewer.model.Issue;
import com.pdvend.githubrepoviewer.model.IssueSearchResult;

/**
 * ViewModel for all the fragments and activities which observe issues and pull request, using the
 * [GithubRepository] to get the data.
 */
public class IssuesViewModel extends ViewModel {

    private final MutableLiveData<String> queryLiveData = new MutableLiveData<>();
    private final LiveData<IssueSearchResult> issueResult;
    private final MutableLiveData<Issue> selected = new MutableLiveData<>();

    public final LiveData<PagedList<Issue>> issues;
    public final LiveData<PagedList<Issue>> pullRequests;
    public final LiveData<String> networkErrors;
    public final LiveData<String> queryErrors;
    public final LiveData<Boolean> isLoadingFromNetwork;

    public IssuesViewModel(final GithubRepository repository) {
        issueResult = Transformations.map(queryLiveData, repository::search);
        issues = Transformations.switchMap(issueResult, IssueSearchResult::getIssueRequests);
        pullRequests = Transformations.switchMap(issueResult, IssueSearchResult::getPullRequests);
        networkErrors = Transformations.switchMap(issueResult, IssueSearchResult::getNetworkErrors);
        queryErrors = Transformations.switchMap(issueResult, IssueSearchResult::getQueryErrors);
        isLoadingFromNetwork = Transformations.switchMap(issueResult, IssueSearchResult::isLoading);
    }

    public void searchIssues(String repoName){
        queryLiveData.postValue(repoName);
    }

    /**
     * Selects a issue or pull request.
     * @param issue Issue or Pull request
     */
    public void select(Issue issue){
        selected.setValue(issue);
    }

    /**
     * Get the LiveData of the selected issue or pull request.
     * @return Selected LiveData issue
     */
    public LiveData<Issue> getSelected() {
        return selected;
    }

    /**
     * Get the LiveData of the last query used to search.
     * @return Last query used
     */
    public MutableLiveData<String> getQueryLiveData() {
        return queryLiveData;
    }
}
