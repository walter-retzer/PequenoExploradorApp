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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.domain.util.formattedDayMonth
import com.example.pequenoexploradorapp.domain.util.formattedMonth
import com.example.pequenoexploradorapp.domain.util.formattedToMillis
import com.example.pequenoexploradorapp.domain.util.formattedYear
import com.example.pequenoexploradorapp.domain.util.toBrazilianDateFormat
import com.example.pequenoexploradorapp.presentation.components.AnimatedLottieFile
import com.example.pequenoexploradorapp.presentation.components.MenuToolbar
import com.example.pequenoexploradorapp.presentation.components.ProgressButton
import com.example.pequenoexploradorapp.presentation.components.parallax.ParallaxView
import com.example.pequenoexploradorapp.presentation.components.parallax.model.ContainerSettings
import com.example.pequenoexploradorapp.presentation.components.parallax.model.ParallaxOrientation
import com.example.pequenoexploradorapp.presentation.theme.primaryDark


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoverSearchImageScreen(
    dateInitial: String,
    dateFinal: String,
    onNavigateToLoadRoverImage: (imageSearch: String?) -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val toolbarBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val stateScroll = rememberScrollState()
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
                title = "Rover",
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
                        painter = painterResource(id = R.drawable.perseverance_on_mars),
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
                text = "Year:${dateInitial.formattedYear()}  mes:${dateInitial.formattedMonth()} dia:${dateInitial.formattedDayMonth()}As imagens da Missão Rover Perseverance estão disponíveis do dia $dateInitial ao dia $dateFinal",
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Box {
                AnimatedLottieFile(
                    modifier = Modifier
                        .size(190.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.DarkGray, CircleShape)
                        .background(Color.LightGray, CircleShape)
                        .align(Alignment.TopCenter),
                    file = R.raw.funny_rover
                )
            }
            Row {
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, bottom = 10.dp)
                        .wrapContentWidth()
                        .clickable { isShowDatePickerDialog = true },
                    text = "Escolha uma data: ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start,
                    color = Color.White
                )
                Image(
                    painter = painterResource(R.drawable.icon_date),
                    contentDescription = null,
                    modifier = Modifier
                        .size(54.dp)
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
                        .border(1.dp, primaryDark, RoundedCornerShape(20.dp))
                        .background(Color.Black, RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    text = "Data Selecionada: $selectedDate",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = primaryDark
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
                    isLoading = false,
                    onClick = {
                        onNavigateToLoadRoverImage(selectedDate)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
class FutureSelectableDates(
    private val dateInitial: String,
    private val dateFinal: String,
) : SelectableDates {
    private val adjustInitialDate = dateInitial.formattedToMillis(1)
    private val adjustEndDate = dateFinal.formattedToMillis()
    private val yearInitial = dateInitial.formattedYear()
    private val yearFinal = dateFinal.formattedYear()

    @ExperimentalMaterial3Api
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis in adjustInitialDate..adjustEndDate
    }

    @ExperimentalMaterial3Api
    override fun isSelectableYear(year: Int): Boolean {
        return year in yearInitial..yearFinal
    }
}
