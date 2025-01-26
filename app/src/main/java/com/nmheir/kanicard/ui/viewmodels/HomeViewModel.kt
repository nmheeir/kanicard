package com.nmheir.kanicard.ui.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nmheir.kanicard.data.entities.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

) : ViewModel() {

    val isLoading = MutableStateFlow(false)
    val isRefreshing = MutableStateFlow(false)

    val user = MutableStateFlow<UserEntity?>(null)

    fun onAction() {

    }

    fun refresh() {
        Timber.d("refreshing")
        isRefreshing.value = true
        viewModelScope.launch {
            delay(300)
            isRefreshing.value = false
        }
    }

}

@Immutable
sealed class HomeAction {

}

sealed interface HomeScreenState {

}