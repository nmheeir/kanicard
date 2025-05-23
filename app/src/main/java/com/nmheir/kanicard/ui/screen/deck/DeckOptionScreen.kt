@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.nmheir.kanicard.ui.screen.deck

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.IconButtonTooltip
import com.nmheir.kanicard.core.presentation.IconTooltip
import com.nmheir.kanicard.core.presentation.utils.hozPadding
import com.nmheir.kanicard.data.dto.deck.DeckOptionUsageDto
import com.nmheir.kanicard.data.entities.option.DeckOptionEntity
import com.nmheir.kanicard.data.entities.option.defaultDeckOption
import com.nmheir.kanicard.ui.component.Gap
import com.nmheir.kanicard.ui.component.ImprovedDropdownMenu
import com.nmheir.kanicard.ui.component.InfoBanner
import com.nmheir.kanicard.ui.component.dialog.AlertDialog
import com.nmheir.kanicard.ui.component.dialog.DefaultDialog
import com.nmheir.kanicard.ui.component.dialog.TextFieldDialog
import com.nmheir.kanicard.ui.component.widget.EditTextPreferenceWidget
import com.nmheir.kanicard.ui.component.widget.PreferenceGroupHeader
import com.nmheir.kanicard.ui.component.widget.SwitchPreferenceWidget
import com.nmheir.kanicard.ui.component.widget.TextPreferenceWidget
import com.nmheir.kanicard.ui.viewmodels.DeckOptionUiAction
import com.nmheir.kanicard.ui.viewmodels.DeckOptionViewModel

@Composable
fun DeckOptionScreen(
    navController: NavHostController,
    scrollBehaviour: TopAppBarScrollBehavior,
    viewModel: DeckOptionViewModel = hiltViewModel()
) {

    val optionUsages by viewModel.optionUsages.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val optionData by viewModel.optionData.collectAsStateWithLifecycle()
    val alertCannotDelete by viewModel.alertCannotDelete.collectAsStateWithLifecycle()
    val saveOptionSuccess by viewModel.saveOptionSuccess.collectAsStateWithLifecycle()
    val showCheckConfirmSaveAction by viewModel.showCheckConfirmSaveAction.collectAsStateWithLifecycle()

    val selectedDto = remember(optionUsages, optionData.id) {
        optionUsages.find { it.option.id == optionData.id }
    }


    // State for editable values
    var newPerDay by rememberSaveable(optionData.id) { mutableStateOf(optionData.newPerDay.toString()) }
    var revPerDay by rememberSaveable(optionData.id) { mutableStateOf(optionData.revPerDay.toString()) }
    var autoPlayAudio by rememberSaveable(optionData.id) { mutableStateOf(optionData.autoAudio) }
    var autoShowAnswer by rememberSaveable(optionData.id) { mutableLongStateOf(optionData.autoShowAnswer) }

    val parameterState = remember(optionData.id) {
        TextFieldState(initialText = optionData.fsrsParams.joinToString(", "))
    }

    val shouldShowWarningDailyLimit by remember(newPerDay, revPerDay) {
        derivedStateOf {
            newPerDay.toLongOrNull()?.times(10)
                ?.let { it > (revPerDay.toLongOrNull() ?: Long.MAX_VALUE) } == true
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

    if (alertCannotDelete) {
        DefaultDialog(
            onDismiss = { viewModel.onAction(DeckOptionUiAction.ConfirmAlertCannotDelete) },
            icon = {
                Icon(
                    painterResource(R.drawable.ic_error), null
                )
            }
        ) {
            Text(text = "Cannot delete default preset")
        }
    }

    if (saveOptionSuccess) {
        AlertDialog(
            onDismiss = { viewModel.onAction(DeckOptionUiAction.ConfirmSaveSuccess) },
            onConfirm = navController::navigateUp,
            confirmText = "Leave",
            dismissText = "Keep editing",
            icon = {
                Icon(
                    painterResource(R.drawable.ic_check_circle), null
                )
            }
        ) {
            Text(text = "Save success. Do you want to continue or leave out?")
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

    if (showCheckConfirmSaveAction) {
        ConfirmSaveActionDialog(
            onDismiss = { viewModel.onAction(DeckOptionUiAction.DismissCheckConfirmSaveAction) },
            onApplyAllPreset = {
                viewModel.onAction(
                    DeckOptionUiAction.Save(
                        newPerDay = newPerDay.toLong(),
                        revPerDay = revPerDay.toLong(),
                        autoPlayAudio = autoPlayAudio,
                        autoShowAnswer = autoShowAnswer,
                        fsrsParams = parameterState.text.split(", ").map { it.toDouble() }
                    ))
            },
            onClonePreset = {
                viewModel.onAction(
                    DeckOptionUiAction.CloneToNewPreset(
                        newPerDay = newPerDay.toLong(),
                        revPerDay = revPerDay.toLong(),
                        autoPlayAudio = autoPlayAudio,
                        autoShowAnswer = autoShowAnswer,
                        fsrsParams = parameterState.text.split(", ").map { it.toDouble() }
                    ))
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehaviour,
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
                },
                actions = {
                    TopAppBarActionButton(
                        optionData = optionData,
                        action = viewModel::onAction
                    )
                }
            )
        }
    ) { pv ->
        LazyColumn(
            contentPadding = pv,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(
                key = "deck_option"
            ) {
                DropDownDeckOption(
                    modifier = Modifier.padding(top = 8.dp),
                    datas = optionUsages,
                    isDataChange = checkData,
                    action = viewModel::onAction,
                    optionData = selectedDto,
                    onSave = {
                        viewModel.onAction(DeckOptionUiAction.CheckConfirmSaveAction(
                            newPerDay = newPerDay.toLong(),
                            revPerDay = revPerDay.toLong(),
                            autoPlayAudio = autoPlayAudio,
                            autoShowAnswer = autoShowAnswer,
                            fsrsParams = parameterState.text.split(", ").map { it.toDouble() }
                        ))
                        /*viewModel.onAction(
                            DeckOptionUiAction.Save(
                                newPerDay = newPerDay.toLong(),
                                revPerDay = revPerDay.toLong(),
                                autoPlayAudio = autoPlayAudio,
                                autoShowAnswer = autoShowAnswer,
                                fsrsParams = parameterState.text.split(", ").map { it.toDouble() }
                            )
                        )*/
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
                    onValueChange = { newPerDay = it },
                    singleLine = true
                )
                EditTextPreferenceWidget(
                    title = stringResource(R.string.label_maximum_review_days),
                    subtitle = revPerDay,
                    value = revPerDay,
                    onValueChange = { revPerDay = it },
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
                    onCheckedChanged = { autoPlayAudio = it }
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
                        autoShowAnswer = it.toLong()
                    }
                )
            }
        }
    }
}

@Composable
private fun DropDownDeckOption(
    modifier: Modifier = Modifier,
    isDataChange: () -> Boolean,
    optionData: DeckOptionUsageDto?,
    datas: List<DeckOptionUsageDto>,
    onSave: () -> Unit,
    action: (DeckOptionUiAction) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .hozPadding()
    ) {
        ImprovedDropdownMenu(
            items = datas,
            selectedItem = optionData,
            onItemSelected = {
                action(DeckOptionUiAction.ChangeSelectedOption(it.option.id))
            },
            itemLabel = {
                it.option.name + " (${it.usage})"
            },
            itemSubLabel = {
                if (it == null) {
                    "No used"
                } else {
                    pluralStringResource(
                        R.plurals.deck_used_by,
                        it.usage.toInt(),
                        it.usage.toInt()
                    )
                }
            },
            modifier = Modifier.weight(1f)
        )

        TextButton(
            enabled = isDataChange(),
            onClick = onSave,
//            modifier = Modifier.weight(2f)
        ) {
            Text(
                text = stringResource(R.string.action_save)
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

@Composable
private fun RowScope.TopAppBarActionButton(
    optionData: DeckOptionEntity,
    action: (DeckOptionUiAction) -> Unit
) {
    var showAddNewPreset by remember { mutableStateOf(false) }
    var showClonePreset by remember { mutableStateOf(false) }
    var showRenamePreset by remember { mutableStateOf(false) }
    var showDeletePreset by remember { mutableStateOf(false) }

    IconButtonTooltip(
        iconRes = R.drawable.ic_add,
        shortCutDescription = "Add new preset"
    ) {
        showAddNewPreset = true
    }

    IconButtonTooltip(
        iconRes = R.drawable.ic_content_copy,
        shortCutDescription = "Clone preset"
    ) {
        showClonePreset = true
    }

    IconButtonTooltip(
        iconRes = R.drawable.ic_edit,
        shortCutDescription = "Rename preset"
    ) {
        showRenamePreset = true
    }

    IconButtonTooltip(
        iconRes = R.drawable.ic_delete,
        shortCutDescription = "Delete preset"
    ) {
        showDeletePreset = true
    }

    if (showClonePreset) {
        AlertDialog(
            onDismiss = { showClonePreset = false },
            onConfirm = {
                action(DeckOptionUiAction.ClonePreset)
                showClonePreset = false
            }
        ) {
            Text(text = "Do you want to clone this preset")
        }
    }

    if (showRenamePreset) {
        TextFieldDialog(
            onDismiss = { showRenamePreset = false },
            onDone = {
                action(DeckOptionUiAction.RenamePreset(it))
                showRenamePreset = false
            },
            initialTextFieldValue = TextFieldValue(
                text = optionData.name,
                selection = TextRange(optionData.name.length)
            )
        )
    }

    if (showDeletePreset) {
        AlertDialog(
            onDismiss = { showDeletePreset = false },
            onConfirm = {
                action(DeckOptionUiAction.DeletePreset)
                showDeletePreset = false
            }
        ) {
            Text(text = "Do you want to delete this preset")
        }
    }

    var showNewNameDialog by remember { mutableStateOf(false) }
    if (showAddNewPreset) {
        AlertDialog(
            onDismiss = { showAddNewPreset = false },
            onConfirm = {
                showNewNameDialog = true
                showAddNewPreset = false
            }
        ) {
            Text(text = "Do you want to add new preset")
        }
    }
    if (showNewNameDialog) {
        TextFieldDialog(
            onDismiss = { showNewNameDialog = false },
            onDone = {
                action(DeckOptionUiAction.NewPreset(it))
                showNewNameDialog = false
            }
        )
    }
}

@Composable
private fun ConfirmSaveActionDialog(
    onDismiss: () -> Unit,
    onApplyAllPreset: () -> Unit,
    onClonePreset: () -> Unit
) {
    DefaultDialog(
        onDismiss = onDismiss,
        buttons = {
            TextButton(
                onClick = {
                    onClonePreset()
                    onDismiss()
                }
            ) {
                Text(text = "Clone to new preset")
            }
            TextButton(
                onClick = {
                    onApplyAllPreset()
                    onDismiss()
                }
            ) {
                Text(text = "Apply all preset")
            }
        }
    ) {
        Text(text = "Do you want to apply all preset or copy to new preset ?")
    }
}

val fakeDeckOptionUsages = List(10) {
    DeckOptionUsageDto(
        option = defaultDeckOption.copy(
            id = it.toLong(),
            name = "Deck $it"
        ),
        usage = it.toLong()
    )
}