package com.nmheir.kanicard.ui.screen.auth

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.ui.activities.LocalAuthActivityWindowInset
import com.nmheir.kanicard.ui.component.AuthTextField
import com.nmheir.kanicard.ui.component.ErrorBox
import com.nmheir.kanicard.ui.component.Gap
import com.nmheir.kanicard.ui.viewmodels.AuthAction
import com.nmheir.kanicard.ui.viewmodels.AuthError
import com.nmheir.kanicard.ui.viewmodels.AuthEvent
import com.nmheir.kanicard.ui.viewmodels.AuthViewModel
import com.nmheir.kanicard.utils.ObserveAsEvents
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SignUpScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    ObserveAsEvents(viewModel.channel) {
        when (it) {
            is AuthEvent.Failure -> {
                Toast.makeText(context, context.getString(it.messageId), Toast.LENGTH_SHORT).show()
            }

            is AuthEvent.Success -> {
                navController.navigateUp()
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val bringIntoView = remember { BringIntoViewRequester() }

    val error by viewModel.authError.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val (email, onEmailChange) = remember {
        mutableStateOf(TextFieldValue())
    }
    val (password, onPasswordChange) = remember {
        mutableStateOf(TextFieldValue())
    }
    val (confirmPassword, onConfirmPasswordChange) = remember {
        mutableStateOf(TextFieldValue())
    }
    var showPassword by remember {
        mutableStateOf(false)
    }
    var showConfirmPassword by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            bringIntoView.bringIntoView()
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(LocalAuthActivityWindowInset.current.asPaddingValues())
    ) {
        //Header Section
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(112.dp)
                .clip(MaterialTheme.shapes.medium)
        )

        Text(
            text = stringResource(R.string.sign_up_title),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = stringResource(R.string.sign_up_description),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        //Input section
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            AuthTextField(
                label = stringResource(R.string.email_address),
                value = email,
                onValueChange = onEmailChange,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_email),
                        contentDescription = null
                    )
                },
                modifier = Modifier.bringIntoViewRequester(bringIntoView)
            )

            AuthTextField(
                label = stringResource(R.string.password),
                value = password,
                onValueChange = onPasswordChange,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_lock),
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            showPassword = !showPassword
                        }
                    ) {
                        Icon(
                            painter = painterResource(
                                if (showPassword) R.drawable.ic_visibility else R.drawable.ic_visibility_off
                            ),
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.bringIntoViewRequester(bringIntoView)
            )

            AuthTextField(
                label = stringResource(R.string.confirm_password),
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_lock),
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            showConfirmPassword = !showConfirmPassword
                        }
                    ) {
                        Icon(
                            painter = painterResource(
                                if (showConfirmPassword) R.drawable.ic_visibility else R.drawable.ic_visibility_off
                            ),
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.bringIntoViewRequester(bringIntoView)
            )

            LaunchedEffect(error) {
                Timber.d(error.toString())
            }

            AnimatedVisibility(
                visible = error != AuthError.Nothing
            ) {
                ErrorBox(
                    message = when (error) {
                        AuthError.Nothing -> {
                            "Nothing"
                        }

                        AuthError.PasswordNotMatch -> {
                            stringResource(R.string.err_pw_not_match)
                        }

                        AuthError.InvalidEmail -> {
                            stringResource(R.string.err_invalid_email)
                        }

                        AuthError.InvalidPassword -> {
                            stringResource(R.string.err_invalid_password)
                        }
                    },
                    description = when (error) {
                        AuthError.InvalidEmail -> {
                            stringResource(R.string.err_invalid_email_des)
                        }

                        AuthError.InvalidPassword -> {
                            stringResource(R.string.err_invalid_password_des)
                        }

                        AuthError.Nothing -> {
                            null
                        }

                        AuthError.PasswordNotMatch -> {
                            null
                        }
                    }
                )
            }

            Button(
                enabled = email.text.isNotEmpty() && password.text.isNotEmpty() && confirmPassword.text.isNotEmpty()
                        && !isLoading,
                onClick = {
                    viewModel.onAction(
                        AuthAction.SignUp(
                            email.text.trim(),
                            password.text.trim(),
                            confirmPassword.text.trim()
                        )
                    )
                },
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(48.dp)
            ) {
                if (!isLoading) {
                    Text(
                        text = stringResource(R.string.sign_up),
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