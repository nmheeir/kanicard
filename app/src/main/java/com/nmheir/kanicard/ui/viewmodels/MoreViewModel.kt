package com.nmheir.kanicard.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.constants.EmailKey
import com.nmheir.kanicard.data.entities.ProfileEntity
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.utils.dataStore
import com.nmheir.kanicard.utils.get
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: KaniDatabase,
) : ViewModel() {

    val isLoading = MutableStateFlow(false)

    val profile = MutableStateFlow<ProfileEntity?>(null)

    private val _channel = Channel<MainState>()
    val channel = _channel.receiveAsFlow()
    val error = MutableStateFlow("")

    init {
        isLoading.value = true
        viewModelScope.launch {
            if (context.dataStore[EmailKey] != null) {
                fetchProfile()
            }
            isLoading.value = false
        }
    }

    private fun fetchProfile() {
        // TODO: Fetch profile 
        viewModelScope.launch {
            Timber.d("Fetch profile")
        }
    }

    fun signOut() {
        isLoading.value = true
        viewModelScope.launch {

        }
    }
}

sealed interface MainState {
    data object Success : MainState
    data class Error(val message: String) : MainState
}