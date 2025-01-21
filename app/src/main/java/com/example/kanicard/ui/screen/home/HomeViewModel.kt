package com.example.kanicard.ui.screen.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.example.kanicard.data.entities.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

) : ViewModel() {

    val user = MutableStateFlow<User?>(null)

    fun onAction() {

    }

}

@Immutable
sealed class HomeAction {

}