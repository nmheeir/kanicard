package com.nmheir.kanicard.core.presentation.components.flip

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FlipController {
    private val _flipRequests = MutableSharedFlow<FlipState>(extraBufferCapacity = 1)
    internal val flipRequests = _flipRequests.asSharedFlow()

    private var _currentSide: FlipState = FlipState.FRONT
    private var _flipEnabled: Boolean = true

    /**
     * Flips the view to the [FlipState.FRONT] side
     */
    fun flipToFront() {
        flip(FlipState.FRONT)
    }

    /**
     * Flips the view to the [FlipState.BACK] side
     */
    fun flipToBack() {
        flip(FlipState.BACK)
    }

    /**
     * Flips the view to the passed [flipState]
     * @param flipState The side to flip the view to.
     */
    fun flip(flipState: FlipState) {
        if (_flipEnabled.not())
            return

        _currentSide = flipState
        _flipRequests.tryEmit(flipState)
    }

    /**
     * Flips the view to the other side. If it's [FlipState.FRONT] it goes to [FlipState.BACK] and vice-versa
     */
    fun flip() {
        if (_currentSide == FlipState.FRONT)
            flipToBack()
        else flipToFront()
    }

    internal fun setConfig(
        flipEnabled: Boolean = true,
    ) {
        _flipEnabled = flipEnabled
    }
}

@Composable
fun rememberFlipController(): FlipController = remember { FlipController() }