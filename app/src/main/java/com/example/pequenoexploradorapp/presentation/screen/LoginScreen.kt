package com.example.pequenoexploradorapp.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import com.example.pequenoexploradorapp.presentation.components.AnimatedLottieFile
import com.example.pequenoexploradorapp.presentation.components.ProgressButton
import com.example.pequenoexploradorapp.presentation.components.VerticalSpacer
import com.example.pequenoexploradorapp.presentation.components.snackBarOnlyMessage
import com.example.pequenoexploradorapp.presentation.theme.Pink80
import com.example.pequenoexploradorapp.presentation.theme.PurpleGrey40
import com.example.pequenoexploradorapp.presentation.theme.backgroundColor
import com.example.pequenoexploradorapp.presentation.theme.mainColor
import com.example.pequenoexploradorapp.presentation.viewmodel.LoginUserViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.LoginUserViewState
import org.koin.compose.koinInject


@Composable
fun LoginScreen(
    onGoogleSignInClick: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: LoginUserViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }


    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState) { data ->
                Snackbar(
                    contentColor = mainColor,
                    snackbarData = data
                )
            }
        },
    ) { paddingValues ->
        when (val state = uiState) {
            is LoginUserViewState.Init -> {
                LoginUI(
                    snackBarHostState = snackBarHostState,
                    onNavigateToHome = onNavigateToHome,
                    onGoogleSignInClick = onGoogleSignInClick,
                    onNavigateToSignIn = onNavigateToSignIn,
                    viewModel = viewModel,
                    paddingValues = paddingValues,
                    isLoading = false
                )
            }

            is LoginUserViewState.Loading -> {
                LoginUI(
                    snackBarHostState = snackBarHostState,
                    onNavigateToHome = onNavigateToHome,
                    onGoogleSignInClick = onGoogleSignInClick,
                    onNavigateToSignIn = onNavigateToSignIn,
                    viewModel = viewModel,
                    paddingValues = paddingValues,
                    isLoading = state.isLoading
                )
            }

            is LoginUserViewState.Success -> {
                LoginUI(
                    snackBarHostState = snackBarHostState,
                    onNavigateToHome = onNavigateToHome,
                    onGoogleSignInClick = onGoogleSignInClick,
                    onNavigateToSignIn = onNavigateToSignIn,
                    viewModel = viewModel,
                    paddingValues = paddingValues,
                    isLoading = false,
                    isSuccess = true
                )
            }

            is LoginUserViewState.SuccessResetPassword -> {
                LoginUI(
                    snackBarHostState = snackBarHostState,
                    onNavigateToHome = onNavigateToHome,
                    onGoogleSignInClick = onGoogleSignInClick,
                    onNavigateToSignIn = onNavigateToSignIn,
                    viewModel = viewModel,
                    paddingValues = paddingValues,
                    isLoading = false,
                    hasMessage = true,
                    message = state.message
                )
            }

            is LoginUserViewState.Error -> {
                LoginUI(
                    snackBarHostState = snackBarHostState,
                    onNavigateToHome = onNavigateToHome,
                    onGoogleSignInClick = onGoogleSignInClick,
                    onNavigateToSignIn = onNavigateToSignIn,
                    viewModel = viewModel,
                    paddingValues = paddingValues,
                    isLoading = false,
                    hasMessage = true,
                    message = state.message
                )
            }
        }
    }
}


@Composable
fun LoginUI(
    snackBarHostState: SnackbarHostState,
    onNavigateToHome: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    viewModel: LoginUserViewModel,
    paddingValues: PaddingValues,
    isLoading: Boolean,
    isSuccess: Boolean? = false,
    hasMessage: Boolean? = false,
    message: String? = null
) {
    val scope = rememberCoroutineScope()
    val userLogin by viewModel.userLoginState.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    val isVisiblePassword by remember { derivedStateOf { userLogin.password.isNotBlank() } }
    val isVisibleEmail by remember { derivedStateOf { userLogin.email.isNotBlank() } }
    var showBottomSheet by remember { mutableStateOf(false) }
    var initLoading by remember { mutableStateOf(false) }
    var snackBarIsActivated by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .paint(
                painterResource(id = R.drawable.simple_background),
                contentScale = ContentScale.FillBounds
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VerticalSpacer(16.dp)
            Text(
                text = "Seja Bem Vindo!",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
            Row {
                AnimatedLottieFile(
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.CenterVertically),
                    file = R.raw.astronaut_moon,
                    speed = 2f,
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                modifier = Modifier.align(alignment = Alignment.Start),
                text = "Informe seus dados:",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            VerticalSpacer(10.dp)
            OutlinedTextField(
                textStyle = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black, RoundedCornerShape(20.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.Gray,
                    focusedContainerColor = backgroundColor
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = true,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(20.dp),
                value = userLogin.email,
                isError = emailError,
                supportingText = {
                    if (emailError) Text(text = viewModel.validateEmail(userLogin.email))
                },
                placeholder = {
                    Text(
                        "Email",
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                onValueChange = { viewModel.onEmailChange(it) },
                trailingIcon = {
                    if (isVisibleEmail) {
                        IconButton(
                            onClick = { viewModel.onEmailChange("") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear"
                            )
                        }
                    }
                },
                leadingIcon = {
                    IconButton(
                        onClick = { }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Search"
                        )
                    }
                }
            )
            VerticalSpacer(6.dp)
            OutlinedTextField(
                textStyle = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black, RoundedCornerShape(20.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.Gray,
                    focusedContainerColor = backgroundColor
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = true,
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(20.dp),
                value = userLogin.password,
                isError = passwordError,
                supportingText = {
                    if (passwordError)
                        Text(text = viewModel.validatePassword(userLogin.password))
                },
                visualTransformation = PasswordVisualTransformation(),
                placeholder = {
                    Text(
                        "Senha",
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                onValueChange = {
                    if (it.length <= ConstantsApp.PASSWORD_MAX_NUMBER) viewModel.onPasswordChange(
                        it
                    )
                },
                trailingIcon = {
                    if (isVisiblePassword) {
                        IconButton(
                            onClick = { viewModel.onPasswordChange("") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear"
                            )
                        }
                    }
                },
                leadingIcon = {
                    IconButton(
                        onClick = { }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Password,
                            contentDescription = "Search"
                        )
                    }
                }
            )
            Text(
                modifier = Modifier
                    .align(alignment = Alignment.End)
                    .clickable { showBottomSheet = true },
                text = "Esqueceu a senha?",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
            VerticalSpacer(10.dp)
            ProgressButton(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                text = "Entrar",
                onClick = {
                    initLoading = true
                    viewModel.validateEmail(userLogin.email)
                    viewModel.validatePassword(userLogin.password)
                    if (!emailError && !passwordError && userLogin.password.length == ConstantsApp.PASSWORD_MAX_NUMBER) {
                        viewModel.onFirebaseAuthSignIn(
                            userLogin.email,
                            userLogin.password
                        )
                    }
                }
            )
            VerticalSpacer(16.dp)
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(PurpleGrey40),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                onClick = {
                    initLoading = true
                    onGoogleSignInClick()
                },
            ) {
                Text(
                    text = "Google",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            VerticalSpacer(32.dp)
            Text(
                modifier = Modifier.clickable { },
                text = "Ainda não tem uma conta?",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
            )
            VerticalSpacer(10.dp)
            Text(
                modifier = Modifier
                    .clickable { onNavigateToSignIn() },
                text = "Cadastre-se",
                style = MaterialTheme.typography.bodyMedium,
                color = mainColor,
            )
            VerticalSpacer(32.dp)
            if (showBottomSheet) {
                BottomSheet(viewModel) {
                    showBottomSheet = false
                }
            }
        }
        if (isLoading || initLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(64.dp)
                    .align(Alignment.Center),
                color = Pink80
            )
        }
    }
    if (isConnected == false && !snackBarIsActivated) {
        LaunchedEffect(Unit) {
            snackBarOnlyMessage(
                snackBarHostState = snackBarHostState,
                coroutineScope = scope,
                message = ConstantsApp.ERROR_WITHOUT_INTERNET,
                duration = SnackbarDuration.Long
            )
            snackBarIsActivated = true
        }
    }
    if (isSuccess == true) {
        LaunchedEffect(Unit) {
            onNavigateToHome()
        }
    }
    if (hasMessage == true && !snackBarIsActivated) {
        LaunchedEffect(Unit) {
            snackBarOnlyMessage(
                snackBarHostState = snackBarHostState,
                coroutineScope = scope,
                message = message.toString(),
                duration = SnackbarDuration.Long
            )
            snackBarIsActivated = true
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(viewModel: LoginUserViewModel, onDismiss: () -> Unit) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    val userLogin by viewModel.userLoginState.collectAsState()
    val emailError by viewModel.emailResetError.collectAsState()
    val isVisibleEmail by remember { derivedStateOf { userLogin.email.isNotBlank() } }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .height(250.dp)
        ) {
            Text(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                text = "Recuperar senha",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            VerticalSpacer(16.dp)
            Text(
                modifier = Modifier.align(alignment = Alignment.Start),
                text = "Informe seu email:",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            VerticalSpacer(10.dp)
            OutlinedTextField(
                textStyle = MaterialTheme.typography.labelMedium,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.Gray,
                    focusedContainerColor = backgroundColor
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = true,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                shape = RoundedCornerShape(20.dp),
                value = userLogin.emailForResetPassword,
                isError = emailError,
                supportingText = {
                    if (emailError) Text(text = viewModel.validateEmail(userLogin.email))
                },
                placeholder = {
                    Text(
                        "Email",
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                onValueChange = { viewModel.onEmailForResetPasswordChange(it) },
                trailingIcon = {
                    if (isVisibleEmail) {
                        IconButton(
                            onClick = { viewModel.onEmailForResetPasswordChange("") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear"
                            )
                        }
                    }
                },
                leadingIcon = {
                    IconButton(
                        onClick = { }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Search"
                        )
                    }
                }
            )
            ProgressButton(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 50.dp)
                    .fillMaxWidth(),
                text = "Enviar",
                isLoading = false,
                onClick = {
                    viewModel.onFirebaseAuthResetPassword(userLogin.emailForResetPassword)
                    onDismiss()
                }
            )
        }
    }
}
