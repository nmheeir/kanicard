package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.nmheir.kanicard.data.local.KaniDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val database: KaniDatabase
) : ViewModel() {

    val isRefreshing = MutableStateFlow(false)

    fun refresh() {

    }
}

sealed interface HomeAction {
    data class HomeCategorySelected(val category: HomeCategory) : HomeAction
}

enum class HomeCategory {
    ALL,
    MY_DECK,
    DOWNLOADED
}

data class HomeUiState(
    val selectedHomeCategory: HomeCategory = HomeCategory.MY_DECK
)