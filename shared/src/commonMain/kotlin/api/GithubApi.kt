package com.suadahaji.githubkmp.mobile.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.Url

class GithubApi  {
    private val client = HttpClient()

    // capture the GithubAPI endpoint we're hitting
    private val membersUrl = Url("https://api.github.com/orgs/raywenderlich/members")

    // this function will be used in a coroutine to ask Ktor client to make a GET call to the members
    // endpoint and that returns the result as a String for now
    suspend fun getMembers(): String {
        val result: String = client.get {
            url(this@GithubApi.membersUrl.toString())
        }
        return result
    }
}