package com.pdvend.githubrepoviewer.database;

import android.annotation.SuppressLint;
import android.arch.paging.DataSource;

import com.pdvend.githubrepoviewer.model.Issue;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class GithubCache {

    private final IssueDao issueDao;

    public GithubCache(IssueDao issueDao) {
        this.issueDao = issueDao;

    }

    @SuppressLint("CheckResult")
    public void insert(List<Issue> issues, Runnable finished){
        Single.just(issues)
        .subscribeOn(Schedulers.io())
        .subscribe(issues1 -> {

            issueDao.insert(issues1);
            finished.run();
        });
    }

    public DataSource.Factory<Integer, Issue> issuesByRepoName(String repoName){
        return issueDao.issuesByRepoName(repoName);
    }

    public DataSource.Factory<Integer, Issue> pullRequestByRepoName(String repoName){
        return issueDao.pullRequestByRepoName(repoName);
    }
}
