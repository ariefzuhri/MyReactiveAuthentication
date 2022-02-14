package com.ariefzuhri.mra.data.source.remote.network

import com.ariefzuhri.mra.data.source.remote.response.RegisteredUsersResponse
import io.reactivex.Observable
import retrofit2.http.GET

private const val REGISTERED_USERS_ENDPOINT =
    "f56baf72bd8de6bb23bb013d5bda0f53/raw/d7d5565be93c3e009415a07e453eb333086e944a/registered-users.json"

interface ApiService {

    @GET(REGISTERED_USERS_ENDPOINT)
    fun getRegisteredUsers(): Observable<RegisteredUsersResponse>
}