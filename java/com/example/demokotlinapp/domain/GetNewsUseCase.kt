package com.example.demokotlinapp.domain

import com.example.demokotlinapp.data.Article
import com.example.demokotlinapp.data.NewsRepository
import javax.inject.Inject

class GetNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(): Result<List<Article>> {
        return newsRepository.getNewsArticles()
    }
}