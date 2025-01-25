package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val client: SupabaseClient
) : ViewModel() {

    val userInfo = MutableStateFlow(client.auth.currentUserOrNull())

    init {

    }



}