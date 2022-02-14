package com.ariefzuhri.mra.data.source.remote

import com.ariefzuhri.mra.data.source.remote.network.ApiService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RemoteDataSource private constructor(private val apiService: ApiService) {

    companion object {
        @Volatile
        private var instance: RemoteDataSource? = null

        fun getInstance(apiService: ApiService): RemoteDataSource {
            return instance ?: synchronized(this) {
                instance ?: RemoteDataSource(apiService)
            }
        }
    }

    fun checkIfEmailExistFromApi(email: String): Observable<Boolean> {
        return apiService.getRegisteredUsers()
            // Multi-threading to avoid the UI thread blocking
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            // Convert stream of List<String> to stream of String
            .flatMap { response -> Observable.fromIterable(response.emails) }
            // Check if the email was emitted by the previous stream
            .contains(email).toObservable()
    }
}