package com.pdvend.githubrepoviewer

import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.pdvend.githubrepoviewer.api.GithubService
import com.pdvend.githubrepoviewer.data.GithubRepository
import com.pdvend.githubrepoviewer.database.GithubCache
import com.pdvend.githubrepoviewer.database.LocalDatabase
import com.pdvend.githubrepoviewer.view.ViewModelFactory

/**
 * Class that handles object creation.
 * Like this, objects can be passed as parameters in the constructors and then replaced for
 * testing, where needed.
 */
object Injection {

    /**
     * Creates an instance of [GithubLocalCache] based on the database DAO.
     */
    private fun provideCache(context: Context): GithubCache {
        val database = LocalDatabase.getInstance(context)
        return GithubCache(database.issueDao())
    }

    /**
     * Creates an instance of [GithubRepository] based on the [GithubService] and a
     * [GithubLocalCache]
     */
    private fun provideGithubRepository(context: Context): GithubRepository {
        return GithubRepository(GithubService.create(), provideCache(context))
    }

    /**
     * Provides the [ViewModelProvider.Factory] that is then used to get a reference to
     * [ViewModel] objects.
     */
    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(provideGithubRepository(context))
    }

}