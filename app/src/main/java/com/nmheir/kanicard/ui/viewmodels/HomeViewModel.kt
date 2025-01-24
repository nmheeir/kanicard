package com.nmheir.kanicard.ui.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.nmheir.kanicard.data.entities.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
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

    }

}

@Immutable
sealed class HomeAction {

}