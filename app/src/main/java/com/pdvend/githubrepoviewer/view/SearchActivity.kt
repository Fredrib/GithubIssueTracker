package com.pdvend.githubrepoviewer.view

import android.app.SearchManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import com.pdvend.githubrepoviewer.Injection
import com.pdvend.githubrepoviewer.R
import com.pdvend.githubrepoviewer.model.Issue
import com.pdvend.githubrepoviewer.view.fragments.DetailFragment

/**
 * Single activity used to make repositories issues and pull requests search.
 */
class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel: IssuesViewModel
    private var isShowingDetail = false

    private lateinit var currentAlertDialog: AlertDialog

    private var isBigScreen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // get the view model
        viewModel = ViewModelProviders.of(this, Injection.provideViewModelFactory(this))
                .get(IssuesViewModel::class.java)

        // Check if the device classifies as a big screen (necessary to avoid same behaviors of
        // small screens
        isBigScreen = isBigScreen()

        // start to observe events
        setEventObservers()
    }

    private fun setEventObservers(){

        // On big screens the DetailFragment shares the screen with the list so no need to transition
        if (!isBigScreen) {
            // Observe Selections
            viewModel.selected.observe(this, Observer<Issue> { issue ->
                showBackButton()

                // Bring the Detail Fragment above the ListScreenFragment
                supportFragmentManager.inTransaction {
                    isShowingDetail = true
                    setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                    add(R.id.detail_item, DetailFragment())
                }
            })
        }

        // Observe general network Errors
        viewModel.networkErrors.observe(this, Observer<String> { it ->
            onNetworkError(it)
        })

        // Observe specific of invalid repository name search
        viewModel.queryErrors.observe(this, Observer<String> { it ->
            onQueryError(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }

        // Add the query hint and fix inconsistencies with searchView width for different screen sizes
        searchView.queryHint = getString(R.string.search_hint)
        searchView.maxWidth = Integer.MAX_VALUE

        // listen to query events
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchIssues(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()){
                    // Clear the results and leave the detail fragment if shown
                    viewModel.searchIssues("")
                    viewModel.select(null)
                    if (isShowingDetail){
                        onBackPressed()
                    }
                }
                return false
            }
        })


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Check if the smallest screen dimension is bigger than 600dp, which is the threshold to use
     * the multipane layout.
     */
    private fun isBigScreen() : Boolean {
        val size = resources.configuration.screenLayout

        return ((size and Configuration.SCREENLAYOUT_SIZE_LARGE) == Configuration.SCREENLAYOUT_SIZE_LARGE)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        hideBackButton()

        isShowingDetail = false
    }

    private fun showBackButton(){
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun hideBackButton(){
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    /**
     * Handle query errors.
     */
    private fun onQueryError(error: String?) {
        // Just show dialog if other dialogs are not showing
        if (isAnyDialogShowing()) {
            currentAlertDialog = getRepoNotFoundDialog(this)
            currentAlertDialog.show()
        }
    }

    /**
     * Handle network errors
     */
    private fun onNetworkError(error: String?) {
        // Just show dialog if other dialogs are not showing
        if (isAnyDialogShowing()) {
            currentAlertDialog = getRequestErrorDialog(this)
            currentAlertDialog.show()
        }
    }

    private fun isAnyDialogShowing() : Boolean {
        return !this::currentAlertDialog.isInitialized || !currentAlertDialog.isShowing
    }

    private fun getRepoNotFoundDialog(context: Context): AlertDialog {
        val builder = AlertDialog.Builder(context)

        return builder
                .setPositiveButton(R.string.error_dismiss) { _, _ -> }
                .setMessage(R.string.error_repo_not_found)
                .create()
    }

    private fun getRequestErrorDialog(context: Context): AlertDialog {
        val builder = AlertDialog.Builder(context)

        return builder
                .setPositiveButton(R.string.error_dismiss) {  _, _ -> }
                .setMessage(R.string.error_request_error)
                .create()
    }

    /**
     * Method to wrap the fragment transaction procedure.
     */
    inline fun FragmentManager.inTransaction(func : FragmentTransaction.() -> Unit){
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }

}
