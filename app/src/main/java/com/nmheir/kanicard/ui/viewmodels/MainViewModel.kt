package com.nmheir.kanicard.ui.viewmodels

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.constants.RefreshTokenKey
import com.nmheir.kanicard.utils.dataStore
import com.nmheir.kanicard.utils.get
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.SignOutScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val client: SupabaseClient,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _channel = Channel<MainState>()
    val channel = _channel.receiveAsFlow()

    val userInfo = MutableStateFlow(client.auth.currentUserOrNull())
    val isLoading = MutableStateFlow(false)
    val error = MutableStateFlow("")

    init {
        viewModelScope.launch {
//            updateRefreshToken()
            val currentSession = client.auth.currentSessionOrNull()
            Timber.d("Current session: " + currentSession.toString())
            updateRefreshToken()
        }
    }

    private suspend fun updateRefreshToken() {
        if (context.dataStore[RefreshTokenKey] == null) {
            val currentSession = client.auth.currentSessionOrNull()
            if (currentSession != null) {
                context.dataStore.edit {
                    it[RefreshTokenKey] = currentSession.refreshToken
                }
            }
            Timber.d("Refresh Token: " + currentSession?.refreshToken)

        } else {

            Timber.d(
                "Refresh token from current session: " + client.auth.currentSessionOrNull()
                    .toString()
            )
            Timber.d("Refresh token from data store: " + context.dataStore[RefreshTokenKey])
        }
    }


    fun signOut() {
        isLoading.value = true
        viewModelScope.launch {
            try {
                client.auth.signOut(SignOutScope.LOCAL)
                context.dataStore.edit {
                    it.remove(RefreshTokenKey)
                }
                _channel.send(MainState.Success)
                isLoading.value = false
            } catch (e: Exception) {
                error.value = e.message ?: "Something went wrong, please check log"
                Timber.d(e)
                _channel.send(MainState.Error(e.message!!))
                isLoading.value = false
            }
        }
    }
}

sealed interface MainState {
    data object Success : MainState
    data class Error(val message: String) : MainState
}