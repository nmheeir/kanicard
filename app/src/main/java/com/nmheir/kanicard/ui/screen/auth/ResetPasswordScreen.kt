package com.nmheir.kanicard.ui.screen.auth

import android.app.Activity
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.ui.viewmodels.CodeVerificationState
import com.nmheir.kanicard.ui.viewmodels.ResetPasswordAction
import com.nmheir.kanicard.ui.viewmodels.ResetPasswordViewModel

@Composable
fun ResetPasswordScreen(
    navController: NavHostController,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    // TODO: Need to Fix: Cannot open from link

    val context = LocalContext.current
    val intent = (context as? Activity)?.intent
    val oobCode = intent?.data?.getQueryParameter("oobCode")

    val verificationState by viewModel.codeVerificationState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (!oobCode.isNullOrEmpty()) {
            viewModel.onAction(ResetPasswordAction.CodeVerification(oobCode))
        }
    }

    when (verificationState) {
        is CodeVerificationState.Idle -> {
            CircularProgressIndicator()
        }

        is CodeVerificationState.Success -> {
            Text(text = "Success")
        }

        is CodeVerificationState.Failure -> {
            Text(text = "failure")
        }
    }
}