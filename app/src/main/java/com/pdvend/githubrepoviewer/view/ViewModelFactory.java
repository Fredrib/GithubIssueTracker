package com.pdvend.githubrepoviewer.view;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.pdvend.githubrepoviewer.data.GithubRepository;

/**
 * Factory for ViewModels.
 */
public class ViewModelFactory implements ViewModelProvider.Factory{

    private GithubRepository repository;

    public ViewModelFactory(GithubRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(IssuesViewModel.class)) {
            return (T) new IssuesViewModel(repository);
        }

        throw new IllegalArgumentException("Must be an IssueViewModel Class");
    }
}
