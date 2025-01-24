package com.nmheir.kanicard.ui.viewmodels

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.nmheir.kanicard.R
import com.nmheir.kanicard.utils.Validate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(

) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _channel = Channel<AuthEvent>()
    val channel = _channel.receiveAsFlow()

    val authError = MutableStateFlow<AuthError>(AuthError.Nothing)
    val isLoading = MutableStateFlow(false)

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.SignIn -> {
                signIn(action.email, action.password)
            }

            is AuthAction.SignUp -> {
                signUp(action.email, action.password, action.confirmPassword)
            }
        }
    }

    private fun signUp(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            when {
                password != confirmPassword -> {
                    emitError(AuthError.PasswordNotMatch, R.string.err_pw_not_match)
                }

                !Validate.validateEmail(email) -> {
                    emitError(AuthError.InvalidEmail, R.string.err_invalid_email)
                }

                !Validate.validatePassword(password) -> {
                    emitError(AuthError.InvalidPassword, R.string.err_invalid_password)
                }

                else -> registerUser(email, password)
            }
        }
    }

    private fun registerUser(email: String, password: String) {
        isLoading.value = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                viewModelScope.launch {
                    if (task.isSuccessful) {
                        _channel.send(AuthEvent.Success)
                    } else {
                        when (val exception = task.exception) {
                            is FirebaseNetworkException -> {
                                _channel.send(AuthEvent.Failure(R.string.err_network_error))
                            }

                            is FirebaseTooManyRequestsException -> {
                                _channel.send(AuthEvent.Failure(R.string.err_too_many_request))
                            }

                            else -> {
                                Timber.d(exception?.message)
                            }
                        }
                    }
                    isLoading.value = false
                }
            }
    }

    private suspend fun emitError(error: AuthError, @StringRes message: Int) {
        authError.value = error
        _channel.send(AuthEvent.Failure(message))
    }

    private fun signIn(email: String, password: String) {
        isLoading.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                viewModelScope.launch {
                    if (task.isSuccessful) {
                        _channel.send(AuthEvent.Success)
                    } else {
                        when (val exception = task.exception) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                _channel.send(AuthEvent.Failure(R.string.err_sign_in_incorrect))
                            }

                            is FirebaseNetworkException -> {
                                _channel.send(AuthEvent.Failure(R.string.err_network_error))
                            }

                            is FirebaseTooManyRequestsException -> {
                                _channel.send(AuthEvent.Failure(R.string.err_too_many_request))
                            }

                            else -> {
                                Timber.d(exception?.message)
                            }
                        }
                    }
                    isLoading.value = false
                }
            }
    }
}

@Immutable
sealed interface AuthEvent {
    data object Success : AuthEvent
    data class Failure(@StringRes val messageId: Int) : AuthEvent
}

@Immutable
sealed interface AuthAction {
    data class SignIn(val email: String, val password: String) : AuthAction
    data class SignUp(
        val email: String,
        val password: String,
        val confirmPassword: String
    ) : AuthAction
}

sealed interface AuthError {
    data object PasswordNotMatch : AuthError
    data object Nothing : AuthError
    data object InvalidEmail : AuthError
    data object InvalidPassword : AuthError
}