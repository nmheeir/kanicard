package com.nmheir.kanicard.ui.viewmodels

import android.content.Context
import androidx.core.content.contentValuesOf
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.nmheir.kanicard.constants.RefreshTokenKey
import com.nmheir.kanicard.utils.dataStore
import com.nmheir.kanicard.utils.get
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: SupabaseClient
) : ViewModel() {

    val authState = MutableStateFlow<AuthorizationState>(AuthorizationState.Loading)

    init {
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
                authState.value = AuthorizationState.Error(e.message ?: "Unknown error")
            }
        }
    }


}

sealed interface AuthorizationState {
    data object Authorized : AuthorizationState
    data object Unauthorized : AuthorizationState
    data object Loading : AuthorizationState
    data class Error(val message: String) : AuthorizationState
}