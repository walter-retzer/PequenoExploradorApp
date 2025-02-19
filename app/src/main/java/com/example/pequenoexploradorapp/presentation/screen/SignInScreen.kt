package com.example.pequenoexploradorapp.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import com.example.pequenoexploradorapp.domain.util.MaskVisualTransformation
import com.example.pequenoexploradorapp.presentation.components.AnimatedLottieFile
import com.example.pequenoexploradorapp.presentation.components.ProgressButton
import com.example.pequenoexploradorapp.presentation.components.VerticalSpacer
import com.example.pequenoexploradorapp.presentation.components.snackBarOnlyMessage
import com.example.pequenoexploradorapp.presentation.theme.Pink80
import com.example.pequenoexploradorapp.presentation.theme.backgroundColor
import com.example.pequenoexploradorapp.presentation.theme.mainColor
import com.example.pequenoexploradorapp.presentation.viewmodel.SignInViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.SignInViewState
import org.koin.compose.koinInject


@Composable
fun SignInScreen(
    onNavigateToHome: () -> Unit,
    viewModel: SignInViewModel = koinInject()
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
            is SignInViewState.Init -> {
                SignInUI(
                    snackBarHostState = snackBarHostState,
                    onNavigateToHome = onNavigateToHome,
                    viewModel = viewModel,
                    paddingValues = paddingValues,
                    isLoading = false
                )
            }

            is SignInViewState.Loading -> {
                SignInUI(
                    snackBarHostState = snackBarHostState,
                    onNavigateToHome = onNavigateToHome,
                    viewModel = viewModel,
                    paddingValues = paddingValues,
                    isLoading = state.isLoading
                )
            }

            is SignInViewState.Success -> {
                SignInUI(
                    snackBarHostState = snackBarHostState,
                    onNavigateToHome = onNavigateToHome,
                    viewModel = viewModel,
                    paddingValues = paddingValues,
                    isLoading = false,
                    isSuccess = true,
                    hasMessage = true,
                    message = state.message
                )
            }

            is SignInViewState.Error -> {
                SignInUI(
                    snackBarHostState = snackBarHostState,
                    onNavigateToHome = onNavigateToHome,
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
fun SignInUI(
    snackBarHostState: SnackbarHostState,
    onNavigateToHome: () -> Unit,
    viewModel: SignInViewModel,
    paddingValues: PaddingValues,
    isLoading: Boolean,
    isSuccess: Boolean? = false,
    hasMessage: Boolean? = false,
    message: String? = null
) {
    val scope = rememberCoroutineScope()
    val newUserSignInState by viewModel.newUserSignInState.collectAsState()
    val nameError by viewModel.nameError.collectAsState()
    val phoneError by viewModel.phoneNumberError.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    val isVisiblePassword by remember { derivedStateOf { newUserSignInState.password.isNotBlank() } }
    val isVisibleName by remember { derivedStateOf { newUserSignInState.name.isNotBlank() } }
    val isVisibleEmail by remember { derivedStateOf { newUserSignInState.email.isNotBlank() } }
    val isVisiblePhoneNumber by remember { derivedStateOf { newUserSignInState.phoneNumber.isNotBlank() } }
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
                text = "Criar uma conta",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
            VerticalSpacer(16.dp)
            Row {
                AnimatedLottieFile(
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.CenterVertically),
                    file = R.raw.astronaut_blue,
                    speed = 1f,
                    contentScale = ContentScale.Crop
                )
            }
            VerticalSpacer(16.dp)
            Text(
                modifier = Modifier.align(alignment = Alignment.Start),
                text = "Inscreva-se para começar:",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            VerticalSpacer(16.dp)
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
                    capitalization = KeyboardCapitalization.Words,
                    autoCorrectEnabled = true,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(20.dp),
                value = newUserSignInState.name,
                isError = nameError,
                supportingText = {
                    if (nameError) Text(
                        text = viewModel.validateName(
                            newUserSignInState.name
                        )
                    )
                },
                placeholder = {
                    Text(
                        "Nome",
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                onValueChange = {
                    if (it.length <= ConstantsApp.NAME_MAX_NUMBER) viewModel.onNameChange(
                        it
                    )
                },
                trailingIcon = {
                    if (isVisibleName) {
                        IconButton(
                            onClick = { viewModel.onNameChange("") }
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
                            imageVector = Icons.Default.Person,
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
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(20.dp),
                value = newUserSignInState.phoneNumber,
                isError = phoneError,
                supportingText = {
                    if (phoneError) Text(
                        text = viewModel.validatePhoneNumber(
                            newUserSignInState.phoneNumber
                        )
                    )
                },
                placeholder = {
                    Text(
                        "Celular",
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                onValueChange = {
                    if (it.length <= ConstantsApp.PHONE_MAX_NUMBER) viewModel.onPhoneNumberChange(
                        it
                    )
                },
                visualTransformation = MaskVisualTransformation(
                    MaskVisualTransformation.PHONE
                ),
                trailingIcon = {
                    if (isVisiblePhoneNumber) {
                        IconButton(
                            onClick = { viewModel.onPhoneNumberChange("") }
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
                            imageVector = Icons.Default.Phone,
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
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(20.dp),
                value = newUserSignInState.email,
                isError = emailError,
                supportingText = {
                    if (emailError) Text(
                        text = viewModel.validateEmail(
                            newUserSignInState.email
                        )
                    )
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
                    imeAction = ImeAction.Done
                ),
                shape = RoundedCornerShape(20.dp),
                value = newUserSignInState.password,
                isError = passwordError,
                supportingText = {
                    if (passwordError)
                        Text(
                            text = viewModel.validatePassword(
                                newUserSignInState.password
                            )
                        )
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
            VerticalSpacer(10.dp)
            ProgressButton(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 26.dp)
                    .fillMaxWidth(),
                text = "Cadastrar",
                isLoading = false,
                onClick = {
                    viewModel.validateName(newUserSignInState.name)
                    viewModel.validatePhoneNumber(newUserSignInState.phoneNumber)
                    viewModel.validateEmail(newUserSignInState.email)
                    viewModel.validatePassword(newUserSignInState.password)

                    if (!nameError && !phoneError && !emailError && !passwordError &&
                        newUserSignInState.password.length == ConstantsApp.PASSWORD_MAX_NUMBER
                    )
                        viewModel.onSignInUser(
                            name = newUserSignInState.name,
                            email = newUserSignInState.email,
                            password = newUserSignInState.password,
                            phoneNumber = newUserSignInState.phoneNumber
                        )
                }
            )
        }
        if (isLoading) {
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
