@file:OptIn(ExperimentalMaterial3Api::class)

package com.nmheir.kanicard.ui.screen.deck

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.internal.enableLiveLiterals
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.IconButtonTooltip
import com.nmheir.kanicard.core.presentation.IconTooltip
import com.nmheir.kanicard.core.presentation.utils.hozPadding
import com.nmheir.kanicard.data.dto.deck.DeckOptionUsageDto
import com.nmheir.kanicard.data.entities.option.defaultDeckOption
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.ui.activities.LocalAwareWindowInset
import com.nmheir.kanicard.ui.component.InfoBanner
import com.nmheir.kanicard.ui.component.dialog.AlertDialog
import com.nmheir.kanicard.ui.component.dialog.DefaultDialog
import com.nmheir.kanicard.ui.component.widget.EditTextPreferenceWidget
import com.nmheir.kanicard.ui.component.widget.PreferenceGroupHeader
import com.nmheir.kanicard.ui.component.widget.SwitchPreferenceWidget
import com.nmheir.kanicard.ui.component.widget.TextPreferenceWidget
import com.nmheir.kanicard.ui.theme.KaniTheme
import com.nmheir.kanicard.ui.viewmodels.DeckOptionUiAction
import com.nmheir.kanicard.ui.viewmodels.DeckOptionViewModel
import kotlinx.coroutines.selects.select
import timber.log.Timber
import kotlin.text.split

@Composable
fun DeckOptionScreen(
    navController: NavHostController,
    viewModel: DeckOptionViewModel = hiltViewModel()
) {

    val selectedOption by viewModel.selectedDeckOption.collectAsStateWithLifecycle()
    val optionData by viewModel.optionData.collectAsStateWithLifecycle()
    val optionUsages by viewModel.optionUsages.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val (newPerDay, onNewPerDayChange) = remember(optionData) {
        mutableStateOf(optionData.newPerDay.toString())
    }
    val (revPerDay, onRevPerDayChange) = remember(optionData) {
        mutableStateOf(optionData.revPerDay.toString())
    }

    val (autoPlayAudio, onAutoPlayAudio) = remember(optionData) {
        mutableStateOf(optionData.autoAudio)
    }

    val (autoShowAnswer, onAutoShowAnswer) = remember(optionData) {
        mutableLongStateOf(optionData.autoShowAnswer)
    }

    val parameterState = remember(optionData) {
        TextFieldState(initialText = optionData.fsrsParams.joinToString(", "))
    }

    val shouldShowWarningDailyLimit by remember(optionData, newPerDay, revPerDay) {
        derivedStateOf {
            newPerDay.toLong() * 10 > revPerDay.toLong()
        }
    }

    var confirmChange by remember { mutableStateOf(false) }

    val checkData = {
        newPerDay.toLong() != optionData.newPerDay
                || revPerDay.toLong() != optionData.revPerDay
                || autoPlayAudio != optionData.autoAudio
                || autoShowAnswer != optionData.autoShowAnswer
                || parameterState.text.split(", ").map { it.toDouble() } != optionData.fsrsParams
    }

    BackHandler(enabled = checkData()) {
        if (checkData()) {
            confirmChange = true
        }
    }

    LaunchedEffect(isLoading) {
        if (isLoading == true) {
            navController.navigateUp()
        }
    }

    if (confirmChange) {
        ConfirmChangeDialog(
            onDismiss = {
                confirmChange = false
                navController.navigateUp()
            },
            onConfirm = {
                confirmChange = false
            }
        )
    }

    Scaffold(
//        modifier = Modifier.padding(LocalAwareWindowInset.current.asPaddingValues()),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Deck options"
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (checkData()) {
                                confirmChange = true
                            } else {
                                navController.navigateUp()
                            }
                        }
                    ) {
                        Icon(painterResource(R.drawable.ic_arrow_back_ios), null)
                    }
                }
            )
        }
    ) { pv ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = pv
        ) {
            item(
                key = "dropdown_option"
            ) {
                DropDownDeckOption(
                    selectedOption = selectedOption,
                    datas = optionUsages,
                    action = viewModel::onAction,
                    onSave = {
                        viewModel.onAction(
                            DeckOptionUiAction.Save(
                                newPerDay = newPerDay.toLong(),
                                revPerDay = revPerDay.toLong(),
                                autoPlayAudio = autoPlayAudio,
                                autoShowAnswer = autoShowAnswer,
                                fsrsParams = parameterState.text.split(", ").map { it.toDouble() }
                            )
                        )
                    }
                )
            }

            item(
                key = "daily_limit"
            ) {
                PreferenceGroupHeader(title = stringResource(R.string.pref_daily_limit))
                EditTextPreferenceWidget(
                    title = stringResource(R.string.label_new_card_days),
                    subtitle = newPerDay,
                    value = newPerDay,
                    onValueChange = onNewPerDayChange,
                    singleLine = true
                )
                EditTextPreferenceWidget(
                    title = stringResource(R.string.label_maximum_review_days),
                    subtitle = revPerDay,
                    value = revPerDay,
                    onValueChange = onRevPerDayChange,
                    singleLine = true
                )
                AnimatedVisibility(
                    visible = shouldShowWarningDailyLimit,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it }
                ) {
                    InfoBanner(
                        message = pluralStringResource(
                            R.plurals.daily_limit_warning,
                            1,
                            newPerDay.toInt(),
                            revPerDay.toInt() * 10
                        )
                    )
                }
            }

            item(
                key = "fsrs"
            ) {

                PreferenceGroupHeader(title = stringResource(R.string.pref_fsrs))
                TextPreferenceWidget(
                    title = stringResource(R.string.label_fsrs_param),
                    subtitle = "Optimize preset still in development",
                    widget = {
                        TextButton(
                            onClick = {}
                        ) {
                            Text(
                                text = stringResource(R.string.action_optimize_preset)
                            )
                        }
                    }
                )
                BasicTextField(
                    state = parameterState,
                    modifier = Modifier
                        .padding(12.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Decimal
                    ),
                    decorator = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Black)
                                .padding(12.dp)
                        ) {
                            innerTextField()
                        }
                    }
                )
                TextPreferenceWidget(
                    title = stringResource(R.string.label_fsrs_simulator),
                    subtitle = "Still in development",
                    widget = {
                        IconTooltip(
                            iconRes = R.drawable.ic_warning,
                            shortCutDescription = "Still in development",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                )
            }

            item(
                key = "audio_setting"
            ) {
                PreferenceGroupHeader(title = stringResource(R.string.pref_audio))
                SwitchPreferenceWidget(
                    title = "Don't play audio automatically",
                    checked = autoPlayAudio,
                    onCheckedChanged = onAutoPlayAudio
                )
            }

            item(
                key = "auto_advance"
            ) {
                PreferenceGroupHeader(title = stringResource(R.string.pref_auto_advance))
                EditTextPreferenceWidget(
                    title = "Seconds to show question for",
                    subtitle = autoShowAnswer.toString(),
                    value = autoShowAnswer.toString(),
                    onValueChange = {
                        onAutoShowAnswer(it.toLong())
                    }
                )
            }
        }
    }
}

@Composable
private fun DropDownDeckOption(
    modifier: Modifier = Modifier,
    selectedOption: Long,
    datas: List<DeckOptionUsageDto>,
    onSave: () -> Unit,
    action: (DeckOptionUiAction) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .hozPadding()
    ) {
        var showList by remember { mutableStateOf(false) }
        var boxWidth by remember { mutableIntStateOf(0) }

        Box(
            modifier = Modifier
                .weight(7f)
                .shadow(4.dp, shape = MaterialTheme.shapes.medium, clip = false)
                .clip(MaterialTheme.shapes.medium)
                .clickable { showList = true }
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .onGloballyPositioned { coordinates ->
                    boxWidth = coordinates.size.width
                }
        ) {
            Text(
                text = datas.find { it.option.id == selectedOption }?.option?.name ?: "No deck",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = showList,
                onDismissRequest = { showList = false },
                modifier = Modifier.width(
                    with(LocalDensity.current) { boxWidth.toDp() }
                ) // <-- match width
            ) {
                datas.fastForEach {
                    DropdownMenuItem(
                        text = {
                            Text(text = "${it.option.name} used by ${it.usage}")
                        },
                        onClick = {
                            action(DeckOptionUiAction.UpdateSelectedOption(it.option.id))
                            showList = false
                        }
                    )
                }
            }
        }


        TextButton(
            onClick = onSave,
            modifier = Modifier.weight(2f)
        ) {
            Text(
                text = "Save"
            )
        }
    }
}

@Composable
private fun ConfirmChangeDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        confirmText = "Keep editing",
        dismissText = "Discard"
    ) {
        Text(text = "Are you sure you want to discard your changes?")
    }
}

val fakeDeckOption = List(10) {
    DeckOptionUsageDto(
        option = defaultDeckOption.copy(
            id = it.toLong(),
            name = "Deck $it"
        ),
        usage = it.toLong()
    )
}