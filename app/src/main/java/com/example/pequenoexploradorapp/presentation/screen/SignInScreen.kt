package com.example.pequenoexploradorapp.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import com.example.pequenoexploradorapp.domain.util.MaskVisualTransformation
import com.example.pequenoexploradorapp.presentation.components.ProgressButton
import com.example.pequenoexploradorapp.presentation.components.snackBarOnlyMessage
import com.example.pequenoexploradorapp.presentation.theme.mainColor
import com.example.pequenoexploradorapp.presentation.viewmodel.SignInViewModel
import com.example.pequenoexploradorapp.presentation.viewmodel.SignInViewState
import kotlinx.coroutines.delay
import org.koin.compose.koinInject


@Composable
fun SignInScreen(
    onNavigateToHome: () -> Unit,
    viewModel: SignInViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    var isLoading by remember { mutableStateOf(false) }
    var snackBarIsActivated by remember { mutableStateOf(false) }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { paddingValues ->
        when (val state = uiState) {
            is SignInViewState.Init -> {
                SignInUI(
                    viewModel = viewModel,
                    paddingValues = paddingValues,
                    isLoading = isLoading
                )
            }

            is SignInViewState.Success -> {
                isLoading = true
                SignInUI(
                    viewModel = viewModel,
                    paddingValues = paddingValues,
                    isLoading = isLoading
                )
                LaunchedEffect(key1 = true) {
                    delay(2000L)
                    isLoading = false
                    snackBarOnlyMessage(
                        snackBarHostState = snackBarHostState,
                        coroutineScope = scope,
                        message = state.message
                    )
                    delay(2000L)
                    onNavigateToHome()
                }
            }

            is SignInViewState.Error -> {
                snackBarIsActivated = true
                SignInUI(
                    viewModel = viewModel,
                    paddingValues = paddingValues,
                    isLoading = false
                )
                LaunchedEffect(key1 = snackBarIsActivated) {
                    snackBarOnlyMessage(
                        snackBarHostState = snackBarHostState,
                        coroutineScope = scope,
                        message = state.message,
                        duration = SnackbarDuration.Long
                    )
                    snackBarIsActivated = false
                }
            }
        }
    }
}

@Composable
fun SignInUI(
    viewModel: SignInViewModel,
    paddingValues: PaddingValues,
    isLoading: Boolean
) {
    val newUserSignInState by viewModel.newUserSignInState.collectAsState()
    val nameError by viewModel.nameError.collectAsState()
    val phoneError by viewModel.phoneNumberError.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()

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
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Criar uma conta",
                fontSize = 22.sp,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(R.drawable.splash),
                contentDescription = null,
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
                    .background(Color.White)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                modifier = Modifier.align(alignment = Alignment.Start),
                text = "Inscreva-se para começar:",
                fontSize = 18.sp,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black),
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
                placeholder = { Text("Nome") },
                onValueChange = {
                    if (it.length <= ConstantsApp.NAME_MAX_NUMBER) viewModel.onNameChange(
                        it
                    )
                }
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black),
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
                placeholder = { Text("Celular") },
                onValueChange = {
                    if (it.length <= ConstantsApp.PHONE_MAX_NUMBER) viewModel.onPhoneNumberChange(
                        it
                    )
                },
                visualTransformation = MaskVisualTransformation(
                    MaskVisualTransformation.PHONE
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black),
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
                placeholder = { Text(text = "Email") },
                onValueChange = { viewModel.onEmailChange(it) },
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black),
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
                placeholder = { Text("Senha") },
                onValueChange = {
                    if (it.length <= ConstantsApp.PASSWORD_MAX_NUMBER) viewModel.onPasswordChange(
                        it
                    )
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
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
                color = mainColor
            )
        }
    }
}
