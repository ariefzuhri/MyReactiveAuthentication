package com.ariefzuhri.mra.ui.main

import androidx.lifecycle.ViewModel
import com.ariefzuhri.mra.data.repository.AuthenticationRepository
import io.reactivex.Observable

class MainViewModel(private val repository: AuthenticationRepository) : ViewModel() {

    fun checkIfEmailExistFromApi(email: String): Observable<Boolean> {
        return repository.checkIfEmailExistFromApi(email)
    }
}