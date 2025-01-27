package com.nmheir.kanicard.ui.screen.onboarding

import androidx.compose.runtime.Composable

interface OnboardingStep {
    val isComplete: Boolean

    @Composable
    fun Content()
}