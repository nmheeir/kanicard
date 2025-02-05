package com.nmheir.kanicard.core.presentation.components.flip

import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.snap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun Flippable(
    frontSide: @Composable () -> Unit,
    backSide: @Composable () -> Unit,
    flipController: FlipController,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    flipDurationMs: Int = 400,
    flipOnTouch: Boolean = true,
    flipEnabled: Boolean = true,
    autoFlip: Boolean = false,
    autoFlipDurationMs: Int = 1000,
    cameraDistance: Float = 30.0F,
    flipAnimationType: FlipAnimType = FlipAnimType.HORIZONTAL_CLOCKWISE,
    onFlippedListener: (currentSide: FlipState) -> Unit = { _ -> }
) {
    var prevViewState by remember { mutableStateOf(FlipState.INITIALIZED) }
    var flippableState by remember { mutableStateOf(FlipState.INITIALIZED) }
    val transition: Transition<FlipState> = updateTransition(
        targetState = flippableState,
        label = "Flip Transition",
    )

    flipController.setConfig(
        flipEnabled = flipEnabled
    )

    LaunchedEffect(key1 = flipController, block = {
        flipController.flipRequests
            .onEach {
                prevViewState = flippableState
                flippableState = it
            }
            .launchIn(this)
    })

    val flipCall: () -> Unit = {
        if (transition.isRunning.not() && flipEnabled) {
            prevViewState = flippableState
            if (flippableState == FlipState.FRONT)
                flipController.flipToBack()
            else flipController.flipToFront()
        }
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = transition.currentState, block = {
        if (transition.currentState == FlipState.INITIALIZED) {
            prevViewState = FlipState.INITIALIZED
            flippableState = FlipState.FRONT
            return@LaunchedEffect
        }

        if (prevViewState != FlipState.INITIALIZED && transition.currentState == flippableState) {
            onFlippedListener.invoke(flippableState)

            if (autoFlip && flippableState != FlipState.FRONT) {
                scope.launch {
                    delay(autoFlipDurationMs.toLong())
                    flipCall()
                }
            }
        }
    })

    val frontRotation: Float by transition.animateFloat(
        transitionSpec = {
            when {
                FlipState.FRONT isTransitioningTo FlipState.BACK -> {
                    keyframes {
                        durationMillis = flipDurationMs
                        0f at 0
                        90f at flipDurationMs / 2
                        90f at flipDurationMs
                    }
                }

                FlipState.BACK isTransitioningTo FlipState.FRONT -> {
                    keyframes {
                        durationMillis = flipDurationMs
                        90f at 0
                        90f at flipDurationMs / 2
                        0f at flipDurationMs
                    }
                }

                else -> snap()
            }
        },
        label = "Front Rotation"
    ) { state ->
        when (state) {
            FlipState.INITIALIZED, FlipState.FRONT -> 0f
            FlipState.BACK -> 180f
        }
    }

    val backRotation: Float by transition.animateFloat(
        transitionSpec = {
            when {
                FlipState.FRONT isTransitioningTo FlipState.BACK -> {
                    keyframes {
                        durationMillis = flipDurationMs
                        -90f at 0
                        -90f at flipDurationMs / 2
                        0f at flipDurationMs
                    }
                }

                FlipState.BACK isTransitioningTo FlipState.FRONT -> {
                    keyframes {
                        durationMillis = flipDurationMs
                        0f at 0
                        -90f at flipDurationMs / 2
                        -90f at flipDurationMs
                    }
                }

                else -> snap()
            }
        },
        label = "Back Rotation"
    ) { state ->
        when (state) {
            FlipState.INITIALIZED, FlipState.FRONT -> 180f
            FlipState.BACK -> 0f
        }
    }

    val frontOpacity: Float by transition.animateFloat(
        transitionSpec = {
            when {
                FlipState.FRONT isTransitioningTo FlipState.BACK -> {
                    keyframes {
                        durationMillis = flipDurationMs
                        1f at 0
                        1f at (flipDurationMs / 2) - 1
                        0f at flipDurationMs / 2
                        0f at flipDurationMs
                    }
                }

                FlipState.BACK isTransitioningTo FlipState.FRONT -> {
                    keyframes {
                        durationMillis = flipDurationMs
                        0f at 0
                        0f at (flipDurationMs / 2) - 1
                        1f at flipDurationMs / 2
                        1f at flipDurationMs
                    }
                }

                else -> snap()
            }
        },
        label = "Front Opacity"
    ) { state ->
        when (state) {
            FlipState.INITIALIZED, FlipState.FRONT -> 1f
            FlipState.BACK -> 0f
        }
    }

    val backOpacity: Float by transition.animateFloat(
        transitionSpec = {
            when {
                FlipState.FRONT isTransitioningTo FlipState.BACK -> {
                    keyframes {
                        durationMillis = flipDurationMs
                        0f at 0
                        0f at (flipDurationMs / 2) - 1
                        1f at flipDurationMs / 2
                        1f at flipDurationMs
                    }
                }

                FlipState.BACK isTransitioningTo FlipState.FRONT -> {
                    keyframes {
                        durationMillis = flipDurationMs
                        1f at 0
                        1f at (flipDurationMs / 2) - 1
                        0f at flipDurationMs / 2
                        0f at flipDurationMs
                    }
                }

                else -> snap()
            }
        },
        label = "Back Opacity"
    ) { state ->
        when(state) {
            FlipState.INITIALIZED, FlipState.FRONT -> 0f
            FlipState.BACK -> 1f
        }
    }

    Box(
        modifier = modifier
            .clickable(
                enabled = flipOnTouch,
                onClick = {
                    flipCall()
                },
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
        contentAlignment = contentAlignment
    ) {

        Box(modifier = Modifier
            .graphicsLayer {
                this.cameraDistance = cameraDistance
                when (flipAnimationType) {
                    FlipAnimType.HORIZONTAL_CLOCKWISE -> rotationY = backRotation
                    FlipAnimType.HORIZONTAL_ANTI_CLOCKWISE -> rotationY = -backRotation
                    FlipAnimType.VERTICAL_CLOCKWISE -> rotationX = backRotation
                    FlipAnimType.VERTICAL_ANTI_CLOCKWISE -> rotationX = -backRotation
                }
            }
            .alpha(backOpacity)
            .zIndex(1F - backOpacity)
        ) {
            backSide()
        }

        Box(modifier = Modifier
            .graphicsLayer {
                this.cameraDistance = cameraDistance
                when (flipAnimationType) {
                    FlipAnimType.HORIZONTAL_CLOCKWISE -> rotationY = frontRotation
                    FlipAnimType.HORIZONTAL_ANTI_CLOCKWISE -> rotationY = -frontRotation
                    FlipAnimType.VERTICAL_CLOCKWISE -> rotationX = frontRotation
                    FlipAnimType.VERTICAL_ANTI_CLOCKWISE -> rotationX = -frontRotation
                }
            }
            .alpha(frontOpacity)
            .zIndex(1F - frontRotation)
        ) {
            frontSide()
        }
    }
}