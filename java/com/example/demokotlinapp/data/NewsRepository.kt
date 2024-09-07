package com.example.demokotlinapp.data

import javax.inject.Inject

interface NewsRepository {
    suspend fun getNewsArticles(): Result<List<Article>>
}

class NewsRepositoryImpl @Inject constructor(
    private val newsRemoteDataSource: NewsRemoteDataSource
) : NewsRepository {

    override suspend fun getNewsArticles(): Result<List<Article>> {
        return newsRemoteDataSource.getNewsArticles()
    }
}