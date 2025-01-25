package com.nmheir.kanicard.ui.screen.auth

import android.app.Activity
import android.widget.CheckBox
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.ui.activities.LocalAuthActivityWindowInset
import com.nmheir.kanicard.ui.activities.MainActivity
import com.nmheir.kanicard.ui.component.AuthTextField
import com.nmheir.kanicard.ui.component.Gap
import com.nmheir.kanicard.ui.viewmodels.AuthAction
import com.nmheir.kanicard.ui.viewmodels.AuthEvent
import com.nmheir.kanicard.ui.viewmodels.AuthViewModel
import com.nmheir.kanicard.utils.ObserveAsEvents
import com.nmheir.kanicard.utils.startNewActivity
import io.github.jan.supabase.auth.status.SessionSource
import io.github.jan.supabase.auth.status.SessionStatus

@Composable
fun SignInScreen(
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
                (context as? Activity)?.startNewActivity(MainActivity::class.java)
            }
        }
    }

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(LocalAuthActivityWindowInset.current.asPaddingValues())
            .verticalScroll(rememberScrollState())
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
            text = stringResource(R.string.sign_in_title),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = stringResource(R.string.sign_in_description),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Gap(24.dp)

        //Input Section
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val (email, onEmailChange) = remember {
                mutableStateOf(TextFieldValue())
            }
            val (password, onPasswordChange) = remember {
                mutableStateOf(TextFieldValue())
            }

            val (rememberAccount, onRememberAccountChange) = remember {
                mutableStateOf(false)
            }

            var showPassword by remember {
                mutableStateOf(false)
            }

            AuthTextField(
                label = stringResource(R.string.email_address),
                value = email,
                onValueChange = onEmailChange,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_email),
                        contentDescription = null
                    )
                }
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
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Checkbox(
                    checked = rememberAccount,
                    onCheckedChange = onRememberAccountChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primaryContainer,
                        checkmarkColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                )
                Text(
                    text = "Remember account",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Button(
                enabled = email.text.isNotEmpty() && password.text.isNotEmpty()
                        && !isLoading,
                onClick = {
                    viewModel.onAction(
                        AuthAction.SignIn(
                            email.text,
                            password.text,
                            rememberAccount
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
            ) {
                if (!isLoading) {
                    Text(
                        text = stringResource(R.string.sign_in),
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

        //Sign in option Section
        HorizontalDivider(thickness = 2.dp)

        Gap(24.dp)
        Row {
            Text(
                text = stringResource(R.string.already_have_account),
                style = MaterialTheme.typography.bodyLarge
            )
            Gap(4.dp)
            Text(
                text = stringResource(R.string.sign_up),
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    navController.navigate("sign_up")
                }
            )
        }
        Text(
            text = stringResource(R.string.forgot_password),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable {
                navController.navigate("forgot_password")
            }
        )
    }
}