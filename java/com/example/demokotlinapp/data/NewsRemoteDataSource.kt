package com.example.demokotlinapp.data

import javax.inject.Inject

interface NewsRemoteDataSource {
    suspend fun getNewsArticles(): Result<List<Article>>
}

class NewsRemoteDataSourceImpl @Inject constructor(
    private val newsApi: NewsApi
) : NewsRemoteDataSource {

    override suspend fun getNewsArticles(): Result<List<Article>> {
        return try {
            val response = newsApi.getTopHeadlines()
            if (response.isSuccessful) {
                Result.success(response.body()?.articles ?: emptyList())
            } else {
                Result.failure(Exception("Error fetching news"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
