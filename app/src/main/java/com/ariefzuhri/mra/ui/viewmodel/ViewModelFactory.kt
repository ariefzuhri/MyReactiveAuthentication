package com.ariefzuhri.mra.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ariefzuhri.mra.data.repository.AuthenticationRepository
import com.ariefzuhri.mra.ui.di.Injection
import com.ariefzuhri.mra.ui.main.MainViewModel

class ViewModelFactory private constructor(
    private val authenticationRepository: AuthenticationRepository,
) : ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(): ViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideRepository()
                )
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(authenticationRepository) as T
            }
            else -> {
                throw Throwable("Unknown ViewModel class: " + modelClass.name)
            }
        }
    }
}