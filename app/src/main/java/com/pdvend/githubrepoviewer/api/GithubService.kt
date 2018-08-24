package com.pdvend.githubrepoviewer.api

import com.google.gson.GsonBuilder
import com.pdvend.githubrepoviewer.model.Issue
import com.pdvend.githubrepoviewer.model.IssueDeserializer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val TAG = "GithubService"
private const val REPO_QUALIFIER = "repo:"

/**
 * Search issues and pull requests based on a full repository name.
 * Trigger a request to the Github searchRepo API with the following params:
 * @param query searchRepo keyword
 * @param page request page index
 * @param itemsPerPage number of repositories to be returned by the Github API per page
 *
 * The result of the request is handled by the implementation of the functions passed as params
 * @param onSuccess function that defines how to handle the list of repos received
 * @param onNetworkError function that defines how to handle network request failure
 * @param onSearchError function that defines how to handle query request failure (Invalid repository)
 */
fun searchIssues(
        service: GithubService,
        repo: String,
        page: Int,
        itemsPerPage: Int,
        onSuccess: (repos: List<Issue>) -> Unit,
        onNetworkError: (error: String) -> Unit,
        onSearchError: (error: String) -> Unit) {

    val apiQuery = REPO_QUALIFIER + repo

    service.searchRepos(apiQuery, page, itemsPerPage).enqueue(
            object : Callback<IssueSearchResponse> {
                override fun onFailure(call: Call<IssueSearchResponse>?, t: Throwable) {
                    onNetworkError(t.message ?: "unknown error")
                }

                override fun onResponse(
                        call: Call<IssueSearchResponse>?,
                        response: Response<IssueSearchResponse>
                ) {

                    when {
                        // Success
                        response.isSuccessful -> {
                            val repos = response.body()?.items ?: emptyList()
                            onSuccess(repos)
                        }
                        // Github error code when requesting from a invalid or protected repository
                        response.code() == 422 -> onSearchError(response.errorBody()?.string() ?: "Repository not found error")

                        // General network error
                        else -> onNetworkError(response.errorBody()?.string() ?: "Unknown error")
                    }
                }
            }
    )
}

/**
 * Github API communication setup via Retrofit.
 */
interface GithubService {
    /**
     * Get repos ordered by stars.
     */
    @GET("search/issues?sort=created")
    fun searchRepos(@Query("q") query: String,
                    @Query("page") page: Int,
                    @Query("per_page") itemsPerPage: Int): Call<IssueSearchResponse>


    companion object {
        private const val BASE_URL = "https://api.github.com/"

        fun create(): GithubService {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BASIC

            // Special Gson to conform the Github response to the app's Issue Model
            val gson = GsonBuilder()
                    .registerTypeAdapter(Issue::class.java, IssueDeserializer())
                    .create()

            val client = OkHttpClient.Builder()
                    .addInterceptor(logger)
                    .build()
            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(GithubService::class.java)
        }
    }
}