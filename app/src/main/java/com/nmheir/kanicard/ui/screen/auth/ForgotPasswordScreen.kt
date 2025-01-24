package com.nmheir.kanicard.ui.screen.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.ui.component.AuthTextField
import com.nmheir.kanicard.ui.component.DefaultDialog
import com.nmheir.kanicard.ui.component.Gap
import com.nmheir.kanicard.ui.viewmodels.ResetPasswordAction
import com.nmheir.kanicard.ui.viewmodels.ResetPasswordEvent
import com.nmheir.kanicard.ui.viewmodels.ResetPasswordViewModel
import com.nmheir.kanicard.utils.ObserveAsEvents

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavHostController,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var showSendEmailSuccessDialog by remember { mutableStateOf(false) }
    if (showSendEmailSuccessDialog) {
        DefaultDialog(
            onDismiss = {},
            buttons = {
                TextButton(
                    onClick = { navController.navigateUp() }
                ) {
                    Text(
                        text = stringResource(R.string.ok),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            content = {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    modifier = Modifier.size(112.dp)
                )

                Text(
                    text = stringResource(R.string.please_check_email),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        )
    }

    ObserveAsEvents(viewModel.channel) {
        when (it) {
            is ResetPasswordEvent.Failure -> {
                Toast.makeText(context, context.getString(it.messageId), Toast.LENGTH_SHORT).show()
            }

            is ResetPasswordEvent.Success -> {
                showSendEmailSuccessDialog = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.forgot_password))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        modifier = Modifier
    ) { paddingValues ->

        val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
        val (email, onEmailChange) = remember { mutableStateOf(TextFieldValue("")) }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = paddingValues.calculateTopPadding())
        ) {
            Image(
                painter = painterResource(R.drawable.img_send_email),
                contentDescription = null
            )

            Text(
                text = stringResource(R.string.forgot_password_description),
                style = MaterialTheme.typography.titleMedium
            )


            AuthTextField(
                label = stringResource(R.string.your_email),
                value = email,
                onValueChange = onEmailChange,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Button(
                enabled = email.text.isNotEmpty()
                        && !isLoading,
                onClick = {
                    viewModel.onAction(ResetPasswordAction.EmailVerification(email.text))
                },
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (!isLoading) {
                    Text(
                        text = stringResource(R.string.send_email),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Gap(8.dp)
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_forward),
                        contentDescription = null
                    )
                } else {
                    CircularProgressIndicator()
                }
            }
        }

    }
}