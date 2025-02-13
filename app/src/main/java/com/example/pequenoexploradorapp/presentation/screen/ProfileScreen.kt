package com.example.pequenoexploradorapp.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.domain.secure.SharedPrefApp
import com.example.pequenoexploradorapp.domain.secure.UserPreferences
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import com.example.pequenoexploradorapp.domain.util.formattedAsPhone
import com.example.pequenoexploradorapp.domain.util.restartApp
import com.example.pequenoexploradorapp.presentation.components.LoadingWithLine
import com.example.pequenoexploradorapp.presentation.components.SimpleToolbar
import com.example.pequenoexploradorapp.presentation.components.snackBarOnlyMessage
import com.example.pequenoexploradorapp.presentation.components.snackBarWithActionButton
import com.example.pequenoexploradorapp.presentation.theme.Pink40
import com.example.pequenoexploradorapp.presentation.viewmodel.ProfileViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.ProfileViewState
import org.koin.compose.koinInject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToChooseAvatar: () -> Unit,
    viewModel: ProfileViewModel = koinInject(),
    sharedPref: SharedPrefApp = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val uid: String = sharedPref.readString(UserPreferences.UID)
    val name: String = sharedPref.readString(UserPreferences.NAME)
    val email: String = sharedPref.readString(UserPreferences.EMAIL)
    val phone: String = sharedPref.readString(UserPreferences.PHONE)
    val image: Int = sharedPref.readInt(UserPreferences.IMAGE)
    var isLoading by remember { mutableStateOf(false) }


    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            SimpleToolbar(
                title = "Perfil",
                onNavigationToBack = { onNavigateBack() },
                onNavigationClose = { onNavigateBack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .paint(
                    painterResource(id = R.drawable.simple_background),
                    contentScale = ContentScale.FillBounds
                )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(image),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                        .background(Color.Black, CircleShape)
                        .clickable {
                            onNavigateToChooseAvatar()
                        }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name,
                    style = TextStyle(
                        color = Pink40,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center
                    ),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    modifier = Modifier
                        .padding(8.dp),
                    imageVector = Icons.Filled.Mail,
                    contentDescription = null,
                    tint = Color.White
                )
                Text(
                    text = email,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center
                    ),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    modifier = Modifier
                        .padding(8.dp),
                    imageVector = Icons.Filled.Phone,
                    contentDescription = null,
                    tint = Color.White
                )
                Text(
                    text = phone.formattedAsPhone(),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center
                    )
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    modifier = Modifier
                        .padding(8.dp),
                    imageVector = Icons.Filled.CheckCircleOutline,
                    contentDescription = null,
                    tint = Color.White
                )
                Text(
                    text = "ID: $uid",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center
                    ),
                )
            }
            LoadingWithLine(isLoading)
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            snackBarWithActionButton(
                                coroutineScope = scope,
                                snackBarHostState = snackBarHostState,
                                message = ConstantsApp.MESSAGE_DELETE_ACCOUNT,
                                actionLabel = "Excluir",
                                onAction = { viewModel.onDeleteAccount() },
                                onDismiss = { }
                            )
                        },
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null,
                    tint = Color.White
                )
                Text(
                    modifier = Modifier.clickable {
                        snackBarWithActionButton(
                            coroutineScope = scope,
                            snackBarHostState = snackBarHostState,
                            message = ConstantsApp.MESSAGE_DELETE_ACCOUNT,
                            actionLabel = "Excluir",
                            onAction = { viewModel.onDeleteAccount() },
                            onDismiss = { }
                        )
                    },
                    text = "Excluir Conta",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center
                    ),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            snackBarWithActionButton(
                                coroutineScope = scope,
                                snackBarHostState = snackBarHostState,
                                message = ConstantsApp.MESSAGE_SIGN_OUT_ACCOUNT,
                                actionLabel = "Sair",
                                onAction = { viewModel.onSignOut() },
                                onDismiss = { }
                            )
                        },
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    tint = Color.White
                )
                Text(
                    modifier = Modifier.clickable {
                        snackBarWithActionButton(
                            coroutineScope = scope,
                            snackBarHostState = snackBarHostState,
                            message = ConstantsApp.MESSAGE_SIGN_OUT_ACCOUNT,
                            actionLabel = "Sair",
                            onAction = { viewModel.onSignOut() },
                            onDismiss = { }
                        )
                    },
                    text = "Sair",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center
                    ),
                )
            }
            when (val state = uiState) {
                is ProfileViewState.Dashboard -> {}

                is ProfileViewState.Loading -> {
                    isLoading = true
                }

                is ProfileViewState.Success -> {
                    isLoading = false
                    restartApp(context = LocalContext.current)
                }

                is ProfileViewState.SuccessClearInfoUser -> {
                    snackBarOnlyMessage(
                        snackBarHostState = snackBarHostState,
                        coroutineScope = scope,
                        message = state.message
                    )
                }

                is ProfileViewState.Error -> {
                    snackBarOnlyMessage(
                        snackBarHostState = snackBarHostState,
                        coroutineScope = scope,
                        message = state.message
                    )
                }
            }
        }
    }
}
