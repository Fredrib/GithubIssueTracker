package com.pdvend.githubrepoviewer.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Immutable model class for a Github repository issue that holds some information about the issue.
 * Objects of this type are received from the Github API, therefore all the fields are annotated
 * with the serialized name.
 * This class also defines the Room issues table, where the issue [id] is the primary key.
 */
@Entity(tableName = "issues")
data class Issue(
        @PrimaryKey @field:SerializedName("id") val id: Long,
        @field:SerializedName("repository_url") val repo: String,
        @field:SerializedName("number") val number: Int,
        @field:SerializedName("title") val title: String,
        @field:SerializedName("body") val description: String,
        @field:SerializedName("is_pull_request") val isPulls: Boolean
)