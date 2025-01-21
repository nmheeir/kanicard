package com.example.kanicard.ui.screen.home

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import com.example.kanicard.data.entities.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
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

}

@Immutable
sealed class HomeAction {

}