package com.ariefzuhri.mra.ui.di

import com.ariefzuhri.mra.data.repository.AuthenticationRepository
import com.ariefzuhri.mra.data.source.remote.RemoteDataSource
import com.ariefzuhri.mra.data.source.remote.network.ApiConfig

object Injection {

    fun provideRepository(): AuthenticationRepository {
        val remoteDataSource = RemoteDataSource.getInstance(ApiConfig.provideApiService())
        return AuthenticationRepository.getInstance(remoteDataSource)
    }
}