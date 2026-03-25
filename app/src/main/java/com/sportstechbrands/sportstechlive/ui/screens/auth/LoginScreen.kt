package com.sportstechbrands.sportstechlive.ui.screens.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sportstechbrands.sportstechlive.navigation.AuthRoutes
import com.sportstechbrands.sportstechlive.navigation.Routes
import com.sportstechbrands.sportstechlive.ui.components.*
import com.sportstechbrands.sportstechlive.ui.theme.*
import com.sportstechbrands.sportstechlive.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError      by remember { mutableStateOf(false) }
    var passwordError   by remember { mutableStateOf(false) }

    // Navigate on success
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            navController.navigate(Routes.HOME) {
                popUpTo(AuthRoutes.LOGIN) { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(80.dp))
            SportstechLogo()
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Sportstech Live",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Your fitness journey awaits",
                style = MaterialTheme.typography.bodyMedium,
                color = TextTertiary
            )

            Spacer(Modifier.height(48.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                cornerRadius = 28.dp,
                fillAlpha = 0.13f,
                borderAlpha = 0.4f
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        Brush.linearGradient(listOf(AccentCyan.copy(0.04f), AccentPurple.copy(0.06f)))
                    )
                )
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Welcome Back",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("Sign in to continue", style = MaterialTheme.typography.bodySmall, color = TextTertiary)

                    // API error banner
                    if (state.error != null) {
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(ErrorRed.copy(0.12f))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Error, null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                            Text(state.error!!, style = MaterialTheme.typography.bodySmall, color = ErrorRed)
                        }
                    }

                    Spacer(Modifier.height(28.dp))

                    GlassTextField(
                        value = email,
                        onValueChange = { email = it; emailError = false; viewModel.resetState() },
                        placeholder = "Email address",
                        leadingIcon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                        isError = emailError,
                        errorMessage = "Enter a valid email",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(14.dp))

                    GlassTextField(
                        value = password,
                        onValueChange = { password = it; passwordError = false; viewModel.resetState() },
                        placeholder = "Password",
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                        onImeAction = { attemptLogin(email, password, viewModel, { emailError = true }, { passwordError = true }) },
                        isError = passwordError,
                        errorMessage = "Password must be at least 6 characters",
                        trailingContent = {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(18.dp).clickable { passwordVisible = !passwordVisible }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Forgot Password?",
                        style = MaterialTheme.typography.labelMedium,
                        color = AccentCyan,
                        modifier = Modifier.align(Alignment.End)
                            .clickable { navController.navigate(AuthRoutes.FORGOT_PASSWORD) }
                    )
                    Spacer(Modifier.height(24.dp))

                    GlassButton(
                        text = if (state.isLoading) "Signing in…" else "Sign In",
                        onClick = { attemptLogin(email, password, viewModel, { emailError = true }, { passwordError = true }) },
                        gradientColors = listOf(AccentCyan, AccentPurple),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = !state.isLoading
                    )

                    if (state.isLoading) {
                        Spacer(Modifier.height(12.dp))
                        CircularProgressIndicator(color = AccentCyan, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    }

                    Spacer(Modifier.height(20.dp))
                    GlassDivider(label = "or continue with")
                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                            .liquidGlass(cornerRadius = 26.dp, fillAlpha = 0.10f, borderAlpha = 0.3f)
                            .clickable { }
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("G", style = MaterialTheme.typography.titleMedium, color = AccentCyan, fontWeight = FontWeight.Black)
                        Spacer(Modifier.width(10.dp))
                        Text("Continue with Google", style = MaterialTheme.typography.labelLarge, color = TextPrimary)
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("New here? ", style = MaterialTheme.typography.bodyMedium, color = TextTertiary)
                Text(
                    "Create Account",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AccentCyan,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { navController.navigate(AuthRoutes.SIGNUP) }
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

private fun attemptLogin(
    email: String,
    password: String,
    viewModel: AuthViewModel,
    onEmailError: () -> Unit,
    onPasswordError: () -> Unit
) {
    val emailValid    = email.contains("@") && email.contains(".")
    val passwordValid = password.length >= 6
    if (!emailValid)    { onEmailError();    return }
    if (!passwordValid) { onPasswordError(); return }
    viewModel.login(email, password)
}

@Composable
fun SportstechLogo(size: Dp = 80.dp) {
    val transition = rememberInfiniteTransition(label = "logoGlow")
    val glow by transition.animateFloat(0.6f, 1f, infiniteRepeatable(tween(2000), RepeatMode.Reverse), "glow")

    Box(contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.size(size * 1.4f).background(Brush.radialGradient(listOf(AccentCyan.copy(0.15f * glow), Color.Transparent)), CircleShape))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(size)
                .liquidGlass(cornerRadius = size / 2, fillAlpha = 0.15f, borderAlpha = 0.5f, elevation = 12.dp)
                .background(Brush.linearGradient(listOf(AccentCyan.copy(0.2f), AccentPurple.copy(0.2f))), CircleShape)
        ) {
            Text("⚡", fontSize = (size.value * 0.4f).sp)
        }
    }
}

