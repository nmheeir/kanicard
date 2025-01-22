package com.nmheir.kanicard.ui.screen.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.constants.SmallArrange
import com.nmheir.kanicard.ui.component.InputField
import com.nmheir.kanicard.utils.ObserveAsEvents
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel(),
    isExpandScreen: Boolean,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current

    ObserveAsEvents(viewModel.channel) {
        when (it) {
            is AuthEvent.Success -> {
                onLoginSuccess()
            }

            is AuthEvent.Failure -> {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            }
        }
    }


    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val width = maxWidth
        val logo = @Composable {
            Image(
                painter = painterResource(R.drawable.ic_launcher_background),
                contentScale = ContentScale.Crop,
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(112.dp)
            )
        }

        if (!isExpandScreen) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                logo()
                AuthSection(
                    viewModel = viewModel,
                    modifier = Modifier
                        .weight(1f)
                )

            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                logo()
                AuthSection(
                    viewModel = viewModel,
                    modifier = Modifier
                        .weight(1f)
                )
            }
        }

    }


}

@Composable
private fun AuthSection(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val loginUiState by viewModel.loginUiState.collectAsStateWithLifecycle()
    val registerUiState by viewModel.registerUiState.collectAsStateWithLifecycle()

    val pageState = rememberPagerState(initialPage = 0) {
        2
    }
    var authOptionSelected by rememberSaveable {
        mutableStateOf(AuthOptions.Login)
    }

    Surface(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TabRow(
                selectedTabIndex = authOptionSelected.ordinal,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[authOptionSelected.ordinal])
                    )
                },
                divider = {},
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Tab(
                    selected = authOptionSelected == AuthOptions.Login,
                    onClick = {
                        authOptionSelected = AuthOptions.Login
                        coroutineScope.launch {
                            pageState.animateScrollToPage(pageState.currentPage - 1)
                        }
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.secondary,

                    ) {
                    Text(
                        text = stringResource(R.string.login),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
                Tab(
                    selected = authOptionSelected == AuthOptions.Register,
                    onClick = {
                        authOptionSelected = AuthOptions.Register
                        coroutineScope.launch {
                            pageState.animateScrollToPage(pageState.currentPage + 1)
                        }
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.secondary
                ) {
                    Text(
                        text = stringResource(R.string.register),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
            HorizontalPager(
                beyondViewportPageCount = 1,
                verticalAlignment = Alignment.Top,
                state = pageState,
                userScrollEnabled = false,
                modifier = Modifier
                    .wrapContentSize()
            ) { currentPage ->
                when (currentPage) {
                    0 -> LoginSection(viewModel::onAction, loginUiState)
                    1 -> RegisterSection(action = viewModel::onAction, registerUiState)
                }
            }
        }
    }
}

@Composable
private fun AuthText(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit
) {

}

@Composable
private fun LoginSection(
    onAction: (AuthAction) -> Unit,
    loginUiState: LoginUiState
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SmallArrange),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        val isPasswordVisible = rememberSaveable { mutableStateOf(false) }
        InputField(
            modifier = Modifier
                .fillMaxWidth(),
            query = loginUiState.email,
            onQueryChange = {
                onAction(AuthAction.OnLoginEmailChange(it))
            },
            label = {
                Text(
                    text = "Email",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            placeHolder = {
                Text(
                    text = "eg: abc@email.com",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_email),
                    contentDescription = null
                )
            }
        )

        // Nhập mật khẩu
        InputField(
            modifier = Modifier
                .fillMaxWidth(),
            query = loginUiState.password,
            onQueryChange = {
                onAction(AuthAction.OnLoginPasswordChange(it))
            },
            label = {
                Text(
                    text = "Password"
                )
            },
            placeHolder = {
                Text(
                    text = "eg: test"
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_lock),
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        isPasswordVisible.value = !isPasswordVisible.value
                    }
                ) {
                    if (isPasswordVisible.value) {
                        Icon(
                            painter = painterResource(R.drawable.ic_eye_off),
                            contentDescription = "Hide password"
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.ic_eye_on),
                            contentDescription = "Show password"
                        )
                    }
                }
            },
            visualTransformation = if (isPasswordVisible.value) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            }
        )

        Text(
            text = stringResource(R.string.forgot_password),
            style = MaterialTheme.typography.bodyLarge.also {
                TextDecoration.Underline
            },
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.End,
            modifier = Modifier
                .align(Alignment.End)
        )

        AuthButton(
            text = stringResource(R.string.login),
            onClick = {
                onAction(AuthAction.Login(loginUiState.email, loginUiState.password))
            },
            enabled = {
                loginUiState.email.trim().isNotBlank() && loginUiState.password.trim().isNotBlank()
            }
        )
    }
}


@Composable
private fun RegisterSection(
    action: (AuthAction) -> Unit,
    registerUiState: RegisterUiState
) {
    val isPasswordVisible = rememberSaveable { mutableStateOf(false) }
    val isConfirmPasswordVisible = rememberSaveable { mutableStateOf(false) }

    val nameInputField: @Composable (modifier: Modifier) -> Unit = {
        InputField(
            modifier = Modifier
                .fillMaxWidth(),
            query = registerUiState.name,
            onQueryChange = {
                action(AuthAction.OnRegisterUsernameChange(it))
            },
            label = {
                Text(
                    text = "Name"
                )
            },
            placeHolder = {
                Text(
                    text = "eg: Test User"
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_person),
                    contentDescription = null
                )
            }
        )
    }

    val emailInputField: @Composable (modifier: Modifier) -> Unit = {
        InputField(
            modifier = Modifier
                .fillMaxWidth(),
            query = registerUiState.email,
            onQueryChange = {
                action(AuthAction.OnRegisterEmailChange(it))
            },
            label = {
                Text(text = "Email")
            },
            placeHolder = {
                Text(
                    text = "eg: abc@gmail.com"
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_email),
                    contentDescription = null
                )
            }
        )
    }

    val passwordInputField: @Composable (modifier: Modifier) -> Unit = {
        InputField(
            modifier = Modifier
                .fillMaxWidth(),
            query = registerUiState.password,
            onQueryChange = {
                action(AuthAction.OnRegisterPasswordChange(it))
            },
            label = {
                Text(
                    text = "Password"
                )
            },
            placeHolder = {
                Text(
                    text = "eg: test"
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_lock),
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        isPasswordVisible.value = !isPasswordVisible.value
                    }
                ) {
                    if (isPasswordVisible.value) {
                        Icon(
                            painter = painterResource(R.drawable.ic_eye_off),
                            contentDescription = "Hide password"
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.ic_eye_on),
                            contentDescription = "Show password"
                        )
                    }
                }
            },
            visualTransformation = if (isPasswordVisible.value) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            }
        )
    }

    val confirmPasswordInputField: @Composable (modifier: Modifier) -> Unit = {
        InputField(
            modifier = Modifier
                .fillMaxWidth(),
            query = registerUiState.confirmPassword,
            onQueryChange = {
                action(AuthAction.OnRegisterConfirmPasswordChange(it))
            },
            label = {
                Text(
                    text = "Confirm Password"
                )
            },
            placeHolder = {
                Text(
                    text = "eg: test"
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_lock),
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        isConfirmPasswordVisible.value = !isConfirmPasswordVisible.value
                    }
                ) {
                    if (isConfirmPasswordVisible.value) {
                        Icon(
                            painter = painterResource(R.drawable.ic_eye_off),
                            contentDescription = "Hide password"
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.ic_eye_on),
                            contentDescription = "Show password"
                        )
                    }
                }
            },
            visualTransformation = if (isConfirmPasswordVisible.value) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            }
        )
    }

    val registerButton = @Composable {
        AuthButton(
            text = stringResource(R.string.register),
            onClick = {
                action(AuthAction.Register)
            },
            enabled = {
                registerUiState.email.trim().isNotBlank()
                        && registerUiState.password.trim().isNotBlank()
                        && registerUiState.confirmPassword.trim().isNotBlank()
                        && registerUiState.name.trim().isNotBlank()
            }
        )
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        if (maxWidth < 600.dp) {
            Column {
                nameInputField(Modifier.weight(1f))
                emailInputField(Modifier.weight(1f))
                passwordInputField(Modifier.weight(1f))
                confirmPasswordInputField(Modifier.weight(1f))
                registerButton()
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        nameInputField(Modifier.weight(1f))
                        emailInputField(Modifier.weight(1f))
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        passwordInputField(Modifier.weight(1f))
                        confirmPasswordInputField(Modifier.weight(1f))
                    }
                }
                registerButton()
            }
        }
    }

}

@Composable
private fun AuthButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: () -> Boolean
) {
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        enabled = enabled(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

enum class AuthOptions {
    Login, Register
}