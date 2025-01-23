package com.nmheir.kanicard.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nmheir.kanicard.ui.theme.KaniCardTheme
import com.nmheir.kanicard.ui.viewmodels.AuthorizationState
import com.nmheir.kanicard.ui.viewmodels.AuthorizationViewModel
import com.nmheir.kanicard.utils.startNewActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AuthorizationActivity : ComponentActivity() {

    private val viewModel by viewModels<AuthorizationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.authState.value is AuthorizationState.Loading
            }
        }

        setContent {
            KaniCardTheme {
                val state by viewModel.authState.collectAsStateWithLifecycle()

                Timber.d(state.toString())
                LaunchedEffect(state) {
                    when (state) {
                        AuthorizationState.Authorized -> {
                            startNewActivity(MainActivity::class.java)
                        }

                        AuthorizationState.Loading -> {

                        }

                        is AuthorizationState.Error -> {

                        }


                        AuthorizationState.Unauthorized -> {
                            startNewActivity(AuthActivity::class.java)
                        }
                    }
                }
            }
        }
    }
}