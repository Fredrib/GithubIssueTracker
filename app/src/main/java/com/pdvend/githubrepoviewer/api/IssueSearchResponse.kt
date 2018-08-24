package com.pdvend.githubrepoviewer.api

import com.google.gson.annotations.SerializedName
import com.pdvend.githubrepoviewer.model.Issue

/**
 * Data class to hold repository issues and pull request (pull requests are treated like issues by
 * Github) responses from searchRepo API calls.
 */
data class IssueSearchResponse(
        @SerializedName("total_count") val total: Int = 0,
        @SerializedName("items") val items: List<Issue> = emptyList(),
        val nextPage: Int? = null
)