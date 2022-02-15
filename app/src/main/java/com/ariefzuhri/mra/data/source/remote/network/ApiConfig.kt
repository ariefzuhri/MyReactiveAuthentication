package com.ariefzuhri.mra.data.source.remote.network

import com.ariefzuhri.mra.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private const val NETWORK_CALL_TIMEOUT = 120L

object ApiConfig {

    fun provideApiService(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(provideHttpClient())
            .build()
        return retrofit.create(ApiService::class.java)
    }

    private fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(provideLoggingInterceptor())
            .connectTimeout(NETWORK_CALL_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NETWORK_CALL_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    private fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        return HttpLoggingInterceptor().setLevel(level)
    }
}