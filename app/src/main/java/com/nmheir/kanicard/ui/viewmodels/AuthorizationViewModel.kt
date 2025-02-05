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
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: SupabaseClient
) : ViewModel() {

    val authState = MutableStateFlow<AuthorizationState?>(null)

    init {
        authState.value = AuthorizationState.Loading
        viewModelScope.launch {
            checkAuthorization()
        }
    }

    private suspend fun checkAuthorization() {
        if (context.dataStore[RefreshTokenKey].isNullOrEmpty()) {
            //User already sign out before
            authState.value = AuthorizationState.Unauthorized
        } else {
            try {
                //Restore old session
                client.auth.refreshSession(context.dataStore[RefreshTokenKey]!!)
                authState.value = AuthorizationState.Authorized
            } catch (e: Exception) {
                Timber.d(e)
                context.dataStore.edit {
                    it.remove(RefreshTokenKey)
                }
                authState.value = AuthorizationState.Unauthorized
            }
        }
    }


}

sealed interface AuthorizationState {
    data object Authorized : AuthorizationState
    data object Unauthorized : AuthorizationState
    data object Loading : AuthorizationState
}