package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nmheir.kanicard.data.entities.Profile
import com.nmheir.kanicard.domain.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userUseCase: UserUseCase
) : ViewModel() {

    val profile = MutableStateFlow<Profile?>(null)

    init {
        viewModelScope.launch {
            fetchProfile()
        }
    }

    private suspend fun fetchProfile() {
        try {
            profile.value = userUseCase.fetchProfile()
        } catch (e: Exception) {
            Timber.d(e)
        }
    }

}