package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountSessionViewModel @Inject constructor(

) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser

//    val accounts = MutableStateFlow<AccountSession>()

    init {
        auth.currentUser?.displayName
    }

}