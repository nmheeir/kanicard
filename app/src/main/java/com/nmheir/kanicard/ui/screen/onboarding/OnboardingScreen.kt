package com.nmheir.kanicard.ui.screen.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.constants.ShowOnboardingKey
import com.nmheir.kanicard.core.presentation.components.padding
import com.nmheir.kanicard.core.presentation.screens.InfoScreen
import com.nmheir.kanicard.utils.rememberPreference
import soup.compose.material.motion.animation.materialSharedAxisX
import soup.compose.material.motion.animation.rememberSlideDistance

@Composable
fun OnboardingScreen(
    navController: NavController
) {
    val (showOnboarding, showOnboardingChange) = rememberPreference(ShowOnboardingKey, true)

    val finishOnboarding = {
        navController.popBackStack()
        showOnboardingChange(false)
    }

    BackHandler(
        enabled = !showOnboarding,
        onBack = {}
    )

    OnboardingScreen(
        onComplete = {
            finishOnboarding()
        }
    )
}

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    val slideDistance = rememberSlideDistance()

    var currentStep by remember { mutableIntStateOf(0) }
    val steps = remember {
        listOf(
            ThemeStep(),
            PermissionStep(),
            GuideStep()
        )
    }
    val isLastStep = currentStep == steps.lastIndex

    BackHandler(enabled = currentStep != 0, onBack = { currentStep-- })

    InfoScreen(
        icon = R.drawable.ic_rocket_launch,
        headingText = stringResource(R.string.onboarding_heading),
        subtitleText = stringResource(R.string.onboarding_description),
        acceptText = stringResource(
            if (isLastStep) {
                R.string.onboarding_action_finish
            } else {
                R.string.onboarding_action_next
            },
        ),
        canAccept = steps[currentStep].isComplete,
        onAcceptClick = {
            if (isLastStep) {
                onComplete()
            } else {
                currentStep++
            }
        },
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = MaterialTheme.padding.small)
                .clip(MaterialTheme.shapes.small)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    materialSharedAxisX(
                        forward = targetState > initialState,
                        slideDistance = slideDistance,
                    )
                },
                label = "stepContent",
            ) {
                steps[it].Content()
            }
        }
    }

}