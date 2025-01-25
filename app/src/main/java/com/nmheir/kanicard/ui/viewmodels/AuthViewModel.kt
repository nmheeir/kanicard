package com.nmheir.kanicard.ui.viewmodels

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.BuildConfig
import com.nmheir.kanicard.R
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.utils.Validate
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val database: KaniDatabase,
    private val client: SupabaseClient
) : ViewModel() {
//    private val auth = FirebaseAuth.getInstance()

    private val auth = client.auth

    private val _channel = Channel<AuthEvent>()
    val channel = _channel.receiveAsFlow()

    val sessionStatus = auth.sessionStatus

    val authError = MutableStateFlow<AuthError>(AuthError.Nothing)
    val isLoading = MutableStateFlow(false)

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.SignIn -> {
                viewModelScope.launch {
                    isLoading.value = true
                    signIn(action.email, action.password, action.rememberAccount)
                    isLoading.value = false
                }
            }

            is AuthAction.SignUp -> {
                signUp(action.email, action.password, action.confirmPassword)
            }
        }
    }

    private fun signUp(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            isLoading.value = true
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

                else -> signUpUser(email, password)
            }
            isLoading.value = false
        }
    }

    private suspend fun signUpUser(userEmail: String, userPassword: String) {
        try {
            auth.signUpWith(Email) {
                email = userEmail
                password = userPassword
            }

        } catch (e: Exception) {
            Timber.d(e)
            Timber.d(e.message)
            when (e.cause) {
                is AuthRestException -> {
                    Timber.d(e.message)
                }
            }
        }
    }

    private suspend fun emitError(error: AuthError, @StringRes message: Int) {
        authError.value = error
        _channel.send(AuthEvent.Failure(message))
    }

    private suspend fun signIn(userEmail: String, userPassword: String, rememberAccount: Boolean) {
        try {
            auth.signInWith(Email) {
                email = userEmail
                password = userPassword
            }
            _channel.send(AuthEvent.Success)
            Timber.d(auth.sessionStatus.toString())
        } catch (e: Exception) {
            Timber.d(e.message)
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
    data class SignIn(
        val email: String,
        val password: String,
        val rememberAccount: Boolean
    ) : AuthAction

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