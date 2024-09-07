package com.example.demokotlinapp.di

import com.example.demokotlinapp.data.NewsApi
import com.example.demokotlinapp.data.NewsRemoteDataSource
import com.example.demokotlinapp.data.NewsRemoteDataSourceImpl
import com.example.demokotlinapp.data.NewsRepository
import com.example.demokotlinapp.data.NewsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    fun provideNewsApi(): NewsApi {
        return Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/") // Replace with your actual base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApi::class.java)
    }

    @Provides
    fun provideNewsRemoteDataSource(newsApi: NewsApi): NewsRemoteDataSource {
        return NewsRemoteDataSourceImpl(newsApi) // Replace with your actual implementation
    }

    @Provides
    fun provideNewsRepository(newsRemoteDataSource: NewsRemoteDataSource): NewsRepository {
        return NewsRepositoryImpl(newsRemoteDataSource)
    }
}