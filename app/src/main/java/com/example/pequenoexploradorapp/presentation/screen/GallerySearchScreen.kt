package com.example.pequenoexploradorapp.presentation.screen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.data.FutureSelectableDates
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import com.example.pequenoexploradorapp.domain.util.formattedRequestDateApi
import com.example.pequenoexploradorapp.domain.util.formattedToMillis
import com.example.pequenoexploradorapp.domain.util.getLocalDate
import com.example.pequenoexploradorapp.domain.util.toBrazilianDateFormat
import com.example.pequenoexploradorapp.presentation.components.AnimatedLottieFile
import com.example.pequenoexploradorapp.presentation.components.MenuToolbar
import com.example.pequenoexploradorapp.presentation.components.ProgressButton
import com.example.pequenoexploradorapp.presentation.components.parallax.ParallaxView
import com.example.pequenoexploradorapp.presentation.components.parallax.model.ContainerSettings
import com.example.pequenoexploradorapp.presentation.components.parallax.model.ParallaxOrientation
import com.example.pequenoexploradorapp.presentation.theme.backgroundColor


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GallerySearchScreen(
    onNavigateToLoadImage: (imageSearch: String?) -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val toolbarBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val stateScroll = rememberScrollState()
    val dateFinal = getLocalDate()
    val dateInitial = ConstantsApp.DATE_INITIAL_APOD
    val dateState = rememberDatePickerState(
        initialSelectedDateMillis = dateFinal.formattedToMillis(),
        selectableDates = FutureSelectableDates(
            dateInitial = dateInitial,
            dateFinal = dateFinal,
        )
    )
    var isShowDatePickerDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            MenuToolbar(
                title = "Galeria",
                onNavigationToMenu = { },
                onNavigationToProfile = { },
                onNavigateToNotifications = { },
                toolbarBehavior = toolbarBehavior,
                isActivatedBadge = false
            )
        },
        containerColor = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .paint(
                    painterResource(id = R.drawable.simple_background),
                    contentScale = ContentScale.FillBounds
                )
                .fillMaxSize()
                .verticalScroll(stateScroll),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ParallaxView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.Black),
                backgroundContent = {
                    Image(
                        painter = painterResource(id = R.drawable.mars),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                },
                middleContent = { },
                foregroundContent = { },
                backgroundContainerSettings = ContainerSettings(
                    scale = 1.2f,
                ),
                foregroundContainerSettings = ContainerSettings(
                    scale = 1.1f,
                    alignment = Alignment.BottomCenter
                ),
                verticalOffsetLimit = 0.15f,
                horizontalOffsetLimit = 0.5f,
                movementIntensityMultiplier = 40,
                orientation = ParallaxOrientation.Full
            )
            Text(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                text = "As Imagens do Dia Nasa estão disponíveis do dia $dateInitial ao dia $dateFinal",
                textAlign = TextAlign.Center,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
            Box {
                AnimatedLottieFile(
                    modifier = Modifier
                        .size(190.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color.White, RoundedCornerShape(16.dp))
                        .background(Color.Black, RoundedCornerShape(16.dp))
                        .align(Alignment.TopCenter),
                    file = R.raw.looking_at_stars
                )
            }
            Row {
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                        .wrapContentWidth()
                        .clickable { isShowDatePickerDialog = true },
                    text = "Escolha uma data: ",
                    textAlign = TextAlign.Start,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
                Image(
                    painter = painterResource(R.drawable.icon_date),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { isShowDatePickerDialog = true }
                )

            }
            if (isShowDatePickerDialog) {
                DatePickerDialog(
                    onDismissRequest = { isShowDatePickerDialog = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                dateState.selectedDateMillis?.let { millis ->
                                    selectedDate = millis.toBrazilianDateFormat()
                                }
                                isShowDatePickerDialog = false
                            }
                        ) {
                            Text(text = "Escolher data")
                        }
                    }
                ) {
                    DatePicker(state = dateState)
                }
            }
            AnimatedVisibility(
                visible = selectedDate.isNotBlank(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LaunchedEffect(Unit) { stateScroll.animateScrollTo(1000) }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { isShowDatePickerDialog = true }
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, Color.White, RoundedCornerShape(20.dp))
                        .background(backgroundColor, RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    text = "Data Selecionada: $selectedDate",
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            AnimatedVisibility(
                visible = selectedDate.isNotBlank(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ProgressButton(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    text = "Pesquisar",
                    onClick = {
                        onNavigateToLoadImage(selectedDate.formattedRequestDateApi())
                    }
                )
            }
        }
    }
}
