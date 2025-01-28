package com.example.pequenoexploradorapp.presentation.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


fun snackBarOnlyMessage(
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    message: String,
    duration: SnackbarDuration = SnackbarDuration.Short
) {
    coroutineScope.launch {
        snackBarHostState.showSnackbar(
            message = message,
            duration = duration
        )
    }
}

fun snackBarWithActionButton(
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    onDismiss: () -> Unit,
) {
    coroutineScope.launch {
        snackBarHostState.showSnackbar(
            message = message,
            withDismissAction = true,
            duration = SnackbarDuration.Indefinite,
            actionLabel = actionLabel
        ).run {
            when (this) {
                SnackbarResult.Dismissed -> { onDismiss.invoke() }
                SnackbarResult.ActionPerformed -> { onAction.invoke() }
            }
        }
    }
}
