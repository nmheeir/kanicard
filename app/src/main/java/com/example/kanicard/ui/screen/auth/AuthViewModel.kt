package com.example.kanicard.ui.screen.auth

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(

) : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState>
        get() = _loginUiState

    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState: StateFlow<RegisterUiState>
        get() = _registerUiState

    private val _channel = Channel<AuthEvent>()
    val channel = _channel.receiveAsFlow()

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.Login -> {
                login(action.email, action.password)
            }

            is AuthAction.OnLoginEmailChange -> {
                _loginUiState.update {
                    it.copy(
                        email = action.email
                    )
                }
            }

            is AuthAction.OnLoginPasswordChange -> {
                _loginUiState.update {
                    it.copy(
                        password = action.password
                    )
                }
            }

            is AuthAction.OnRegisterConfirmPasswordChange -> {
                _registerUiState.update {
                    it.copy(
                        confirmPassword = action.confirmPassword
                    )
                }
            }

            is AuthAction.OnRegisterEmailChange -> {
                _registerUiState.update {
                    it.copy(
                        email = action.email
                    )
                }
            }

            is AuthAction.OnRegisterPasswordChange -> {
                _registerUiState.update {
                    it.copy(
                        password = action.password
                    )
                }
            }

            is AuthAction.OnRegisterUsernameChange -> {
                _registerUiState.update {
                    it.copy(
                        name = action.username
                    )
                }
            }

            AuthAction.Register -> {
                register()
            }
        }
    }

    private fun register() {
        viewModelScope.launch {
            // TODO: xử lý sau
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            if (email == "test" && password == "test") {
                _channel.send(AuthEvent.Success("Login Success"))
            } else {
                _channel.send(AuthEvent.Success("Login Failed"))
            }
        }
    }
}

@Immutable
sealed interface AuthEvent {
    data class Success(val message: String) : AuthEvent
    data class Failure(val message: String) : AuthEvent
}

@Immutable
sealed interface AuthAction {
    data class Login(val email: String, val password: String) : AuthAction
    data object Register : AuthAction

    data class OnLoginEmailChange(val email: String) : AuthAction
    data class OnLoginPasswordChange(val password: String) : AuthAction

    data class OnRegisterEmailChange(val email: String) : AuthAction
    data class OnRegisterUsernameChange(val username: String) : AuthAction
    data class OnRegisterPasswordChange(val password: String) : AuthAction
    data class OnRegisterConfirmPasswordChange(val confirmPassword: String) : AuthAction
}

@Immutable
data class LoginUiState(
    val email: String = "",
    val password: String = ""
)

@Immutable
data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)