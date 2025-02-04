package com.nmheir.kanicard.ui.viewmodels

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.constants.RefreshTokenKey
import com.nmheir.kanicard.data.entities.ProfileEntity
import com.nmheir.kanicard.domain.usecase.UserUseCase
import com.nmheir.kanicard.utils.dataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.SignOutScope
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: SupabaseClient,
    private val userUseCase: UserUseCase
) : ViewModel() {

    val isLoading = MutableStateFlow(false)

    val profile = MutableStateFlow<ProfileEntity?>(null)

    private val _channel = Channel<MainState>()
    val channel = _channel.receiveAsFlow()
    val error = MutableStateFlow("")

    init {
        isLoading.value = true
        viewModelScope.launch {
            fetchProfile()
            isLoading.value = false
        }
    }

    private suspend fun fetchProfile() {
        try {
            profile.value = userUseCase.fetchProfile()
            Timber.e(profile.toString())
        } catch (e: Exception) {
            _channel.send(MainState.Error("Something went wrong, please check log"))
            Timber.d(e)
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