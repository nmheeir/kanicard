package com.nmheir.kanicard.ui.screen.onboarding

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class PermissionStep : OnboardingStep {
    override val isComplete: Boolean = true

    @Composable
    override fun Content() {
        Text(text = "Permission Step")
    }

}