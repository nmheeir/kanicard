package com.nmheir.kanicard.core.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp

val topSmallPaddingValues = PaddingValues(top = MaterialTheme.padding.small)

const val DISABLED_ALPHA = .38f
const val SECONDARY_ALPHA = .78f

class Padding {

    val extraLarge = 32.dp

    val large = 24.dp

    val mediumLarge = 28.dp

    val medium = 16.dp

    val mediumSmall = 12.dp

    val small = 8.dp

    val extraSmall = 4.dp
}

val MaterialTheme.padding: Padding
    get() = Padding()

object Constants {
    object File {
        const val KANI_CARD = "KaniCard"
        const val KANI_CARD_BACKUP = "Backup"
        const val KANI_CARD_IMAGE = "Images"
        const val KANI_CARD_AUDIO = "Audios"
        const val KANI_CARD_VIDEO = "Videos"
    }
}
