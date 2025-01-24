package com.nmheir.kanicard.ui.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
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
class ResetPasswordViewModel @Inject constructor(

) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _channel = Channel<ResetPasswordEvent>()
    val channel = _channel.receiveAsFlow()

    val codeVerificationState = MutableStateFlow<CodeVerificationState>(CodeVerificationState.Idle)

    val isLoading = MutableStateFlow(false)

    fun onAction(action: ResetPasswordAction) {
        when (action) {
            is ResetPasswordAction.EmailVerification -> {
                if (Validate.validateEmail(action.email)) {
                    sendEmailVerification(action.email)
                } else {
                    viewModelScope.launch {
                        _channel.send(ResetPasswordEvent.Failure(R.string.err_invalid_email))
                    }
                }
            }

            is ResetPasswordAction.CodeVerification -> {
                codeVerification(action.code)
            }

            is ResetPasswordAction.NewPassword -> TODO()
        }
    }

    private fun sendEmailVerification(email: String) {
        isLoading.value = true
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                viewModelScope.launch {
                    if (task.isSuccessful) {
                        _channel.send(ResetPasswordEvent.Success(ResetPasswordFlow.EmailVerification))
                    } else {
                        when (val exception = task.exception) {
                            is FirebaseNetworkException -> {
                                _channel.send(ResetPasswordEvent.Failure(R.string.err_network_error))
                            }

                            else -> {
                                Timber.d(exception?.message)
                                _channel.send(ResetPasswordEvent.Failure(R.string.err_unknown))
                            }
                        }
                    }
                    isLoading.value = false
                }
            }
    }

    private fun codeVerification(code: String) {
        isLoading.value = true
        auth.verifyPasswordResetCode(code)
            .addOnCompleteListener { task ->
                viewModelScope.launch {
                    if (task.isSuccessful) {
                        codeVerificationState.value = CodeVerificationState.Success
                    } else {
                        when (val exception = task.exception) {
                            is FirebaseNetworkException -> {
                            }

                            else -> {
                                Timber.d(exception?.message)
                            }
                        }
                    }
                    isLoading.value = true
                }
            }
    }

    /*    private fun sendNewPassword(password: String) {
            isLoading.value = true
            auth.confirmPasswordReset(, password)
                .addOnCompleteListener { task ->
                    viewModelScope.launch {
                        if (task.isSuccessful) {
                            _channel.send(ResetPasswordEvent.Success(ResetPasswordFlow.NewPassword))
                        } else {
                            when (val exception = task.exception) {
                                is FirebaseNetworkException -> {

                                }

                                else -> {
                                    Timber.d(exception?.message)
                                    _channel.send(ResetPasswordEvent.Failure(R.string.err_unknown))
                                }
                            }
                        }
                    }
                }
        }*/
}

sealed interface ResetPasswordEvent {
    data class Success(val flow: ResetPasswordFlow) : ResetPasswordEvent
    data class Failure(@StringRes val messageId: Int) : ResetPasswordEvent
}

sealed interface ResetPasswordFlow {
    data object EmailVerification : ResetPasswordFlow
    data object CodeVerification : ResetPasswordFlow
    data object NewPassword : ResetPasswordFlow
}

sealed interface ResetPasswordAction {
    data class EmailVerification(val email: String) : ResetPasswordAction
    data class CodeVerification(val code: String) : ResetPasswordAction
    data class NewPassword(val password: String) : ResetPasswordAction
}

sealed interface CodeVerificationState {
    data object Idle : CodeVerificationState
    data object Success : CodeVerificationState
    data class Failure(@StringRes val messageId: Int) : CodeVerificationState
}