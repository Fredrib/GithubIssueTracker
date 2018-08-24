package com.pdvend.githubrepoviewer.database;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pdvend.githubrepoviewer.model.Issue;

import java.util.List;

@Dao
public interface IssueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Issue> issues);

    // Look for issues and ignore pull requests, by matching the repository name and order them by number
    @Query("SELECT * FROM issues WHERE (repo LIKE :repoName) AND  isPulls = 0 ORDER BY number DESC")
    DataSource.Factory<Integer, Issue> issuesByRepoName(String repoName);

    // Look for  pull requests, by matching the repository name and order them by number
    @Query("SELECT * FROM issues WHERE (repo LIKE :repoName) AND isPulls = 1 ORDER BY number DESC")
    DataSource.Factory<Integer, Issue> pullRequestByRepoName(String repoName);
}
