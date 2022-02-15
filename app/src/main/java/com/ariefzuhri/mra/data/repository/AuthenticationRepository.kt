package com.ariefzuhri.mra.data.repository

import com.ariefzuhri.mra.data.source.remote.RemoteDataSource
import io.reactivex.rxjava3.core.Observable

class AuthenticationRepository private constructor(private val remoteDataSource: RemoteDataSource) {

    companion object {
        @Volatile
        private var instance: AuthenticationRepository? = null

        fun getInstance(remoteDataSource: RemoteDataSource): AuthenticationRepository {
            return instance ?: synchronized(this) {
                instance ?: AuthenticationRepository(remoteDataSource)
            }
        }
    }

    fun checkIfEmailExistFromApi(email: String): Observable<Boolean> {
        return remoteDataSource.checkIfEmailExistFromApi(email)
    }
}