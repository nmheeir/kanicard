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
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val client: SupabaseClient,
    @ApplicationContext private val context: Context
) : ViewModel() {

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

            try {
                client.auth.refreshCurrentSession()

                updateRefreshTokenDataStore(context)

                Timber.d(
                    "Refresh token from current session: " + client.auth.currentSessionOrNull()?.refreshToken
                        .toString()
                )
                Timber.d("Refresh token from data store: " + context.dataStore[RefreshTokenKey])
            } catch (e: Exception) {
                Timber.d(e)
            }
        }
    }

    private suspend fun updateRefreshTokenDataStore(context: Context) {
        try {
            val refreshToken = client.auth.currentSessionOrNull()?.refreshToken

            Timber.d("AuthorizationViewModel updateRefreshToken: $refreshToken")
            context.dataStore.edit {
                it[RefreshTokenKey] = refreshToken!!
            }
        } catch (e: Exception) {
            Timber.d(e)
        }
    }

}

