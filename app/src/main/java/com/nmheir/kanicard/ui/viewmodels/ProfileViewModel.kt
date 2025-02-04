package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.data.entities.ProfileEntity
import com.nmheir.kanicard.domain.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userUseCase: UserUseCase
) : ViewModel() {

    val profileEntity = MutableStateFlow<ProfileEntity?>(null)

    init {
        viewModelScope.launch {
            fetchProfile()
        }
    }

    private suspend fun fetchProfile() {
        try {
            profileEntity.value = userUseCase.fetchProfile()
        } catch (e: Exception) {
            Timber.d(e)
        }
    }

}