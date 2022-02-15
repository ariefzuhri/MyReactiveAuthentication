package com.ariefzuhri.mra.ui.main

/**
 * This project demonstrates the concept of reactive programming written by Muhammad Arif Rohman Hakim on Medium:
 * https://medium.com/@rohmanhakim/mengulik-reactive-programming-di-android-bagian-1-916b111c5597
 * */

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ariefzuhri.mra.R
import com.ariefzuhri.mra.databinding.ActivityMainBinding
import com.ariefzuhri.mra.ui.viewmodel.ViewModelFactory
import com.ariefzuhri.mra.util.TAG
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * List of requirements:
 * - In the default state, the form has 3 fields for email, password, and password confirmation, as well as a submit button.
 * - In the default state, the submit button is disabled.
 *
 * - Users are required to enter an unregistered email. If already registered, display an alert notification.
 * - Email checking is only performed when the user types more than 3 characters.
 *
 * - Users are required to enter password with a minimum length of 6 characters. If not, display an alert notification.
 *
 * - Users are required to enter password confirmation. If it is not the same as the password, display an alert notification.
 *
 * - Users are required to enter all the fields. If there are still blank fields, the submit button will be disabled.
 * - If all the conditions are met, enable the submit button.
 * */

private const val API_CALL_TIMEOUT = 500L

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* Init view model */
        val factory = ViewModelFactory.getInstance()
        val viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        /* Init email stream */
        val emailStream: Observable<Boolean> = binding.edtEmail.textChanges()
            // Convert from CharSequence to String
            .map { email -> email.toString() }
            // All "character" strings will be escaped if their length is more than 3
            .filter { email -> email.length > 3 }
            // Only call the API when the user stops typing for 100ms
            .debounce(API_CALL_TIMEOUT, TimeUnit.MILLISECONDS)
            // Trigger stream in checkIfEmailExistFromAPI()
            .flatMap { email -> viewModel.checkIfEmailExistFromApi(email) }

        val emailObserver: Observer<Boolean> = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {
                disposables.add(d)
            }

            override fun onNext(isEmailExist: Boolean) {
                showEmailExistAlert(isEmailExist)
            }

            override fun onError(e: Throwable) {
                Log.w(TAG, e)
            }

            override fun onComplete() {

            }
        }

        emailStream.subscribe(emailObserver)

        /* Init password stream */
        val passwordStream: Observable<Boolean> = binding.edtPassword.textChanges()
            .map { password ->
                password.isNotEmpty() && password.toString().trim().length < 6
            }

        val passwordObserver: Observer<Boolean> = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {
                disposables.add(d)
            }

            override fun onNext(isPasswordLessThanLimit: Boolean) {
                showPasswordMinimalAlert(isPasswordLessThanLimit)
            }

            override fun onError(e: Throwable) {
                Log.w(TAG, e)
            }

            override fun onComplete() {
            }
        }

        passwordStream.subscribe(passwordObserver)

        /* Init password confirmation stream */
        val passwordConfirmationStream: Observable<Boolean> = Observable.merge(
            binding.edtPassword.textChanges()
                .map { password ->
                    password.toString().trim() != binding.edtPasswordConfirmation.text.toString()
                },
            binding.edtPasswordConfirmation.textChanges()
                .map { passwordConfirmation ->
                    passwordConfirmation.toString().trim() != binding.edtPassword.text.toString()
                }
        )

        val passwordConfirmationObserver: Observer<Boolean> = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {
                disposables.add(d)
            }

            override fun onNext(isPasswordConfirmationDontMatch: Boolean) {
                showPasswordConfirmationAlert(isPasswordConfirmationDontMatch)
            }

            override fun onError(e: Throwable) {
                Log.w(TAG, e)
            }

            override fun onComplete() {

            }
        }

        passwordConfirmationStream.subscribe(passwordConfirmationObserver)

        /* Init empty field stream */
        val emptyFieldStream: Observable<Boolean> = Observable.combineLatest(
            binding.edtEmail.textChanges()
                .map { email -> email.isEmpty() },
            binding.edtPassword.textChanges()
                .map { password -> password.isEmpty() },
            binding.edtPasswordConfirmation.textChanges()
                .map { passwordConfirmation -> passwordConfirmation.isEmpty() }
        ) { isEmailEmpty, isPasswordEmpty, isPasswordConfirmationEmpty ->
            isEmailEmpty || isPasswordEmpty || isPasswordConfirmationEmpty
        }

        /* Init invalid fields stream */
        val invalidFieldsStream: Observable<Boolean> = Observable.combineLatest(
            emailStream,
            passwordStream, passwordConfirmationStream,
            emptyFieldStream,
        ) { isEmailInvalid, isPasswordInvalid, isPasswordConfirmationInvalid, isEmptyFieldExist ->
            !isEmailInvalid &&
                    !isPasswordInvalid &&
                    !isPasswordConfirmationInvalid &&
                    !isEmptyFieldExist
        }

        val invalidFieldsObserver: Observer<Boolean> = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {
                disposables.add(d)
            }

            override fun onNext(isInvalidFieldExist: Boolean) {
                binding.btnSubmit.isEnabled = isInvalidFieldExist
            }

            override fun onError(e: Throwable) {
                Log.w(TAG, e)
            }

            override fun onComplete() {

            }
        }

        invalidFieldsStream.subscribe(invalidFieldsObserver)
    }

    private fun showEmailExistAlert(isEmailExist: Boolean) {
        binding.tilEmail.error =
            if (isEmailExist) getString(R.string.edt_error_email_main)
            else null
    }

    private fun showPasswordMinimalAlert(isPasswordLessThanLimit: Boolean) {
        binding.tilPassword.error =
            if (isPasswordLessThanLimit) getString(R.string.edt_error_password_main)
            else null
    }

    private fun showPasswordConfirmationAlert(isPasswordConfirmationMatch: Boolean) {
        binding.tilPasswordConfirmation.error =
            if (isPasswordConfirmationMatch) getString(R.string.edt_password_confirmation_error_main)
            else null
    }

    override fun onPause() {
        super.onPause()
        disposables.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }
}