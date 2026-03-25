package com.sportstechbrands.sportstechlive.ui.screens.auth

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
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
fun SignupScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    var fullName       by remember { mutableStateOf("") }
    var email          by remember { mutableStateOf("") }
    var password       by remember { mutableStateOf("") }
    var confirmPass    by remember { mutableStateOf("") }
    var passVisible    by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    var termsAccepted  by remember { mutableStateOf(false) }

    var nameError    by remember { mutableStateOf(false) }
    var emailError   by remember { mutableStateOf(false) }
    var passError    by remember { mutableStateOf(false) }
    var confirmError by remember { mutableStateOf(false) }
    var termsError   by remember { mutableStateOf(false) }

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
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(56.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(42.dp)
                        .liquidGlass(cornerRadius = 21.dp, fillAlpha = 0.12f, borderAlpha = 0.35f)
                        .clickable { navController.popBackStack() }
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextSecondary, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Create Account", style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("Join the fitness revolution", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                }
            }

            Spacer(Modifier.height(24.dp))
            SportstechLogo(size = 60.dp)
            Spacer(Modifier.height(24.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                cornerRadius = 28.dp,
                fillAlpha = 0.13f,
                borderAlpha = 0.4f
            ) {
                Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(AccentPurple.copy(0.04f), AccentCyan.copy(0.06f)))))

                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // API error banner
                    if (state.error != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
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

                    GlassTextField(
                        value = fullName,
                        onValueChange = { fullName = it; nameError = false; viewModel.resetState() },
                        placeholder = "Full Name",
                        leadingIcon = Icons.Default.Person,
                        imeAction = ImeAction.Next,
                        isError = nameError,
                        errorMessage = "Please enter your full name",
                        modifier = Modifier.fillMaxWidth()
                    )

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

                    Column {
                        GlassTextField(
                            value = password,
                            onValueChange = { password = it; passError = false; viewModel.resetState() },
                            placeholder = "Password",
                            leadingIcon = Icons.Default.Lock,
                            isPassword = true,
                            passwordVisible = passVisible,
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next,
                            isError = passError,
                            errorMessage = "At least 6 characters required",
                            trailingContent = {
                                Icon(
                                    if (passVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    null, tint = TextTertiary,
                                    modifier = Modifier.size(18.dp).clickable { passVisible = !passVisible }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (password.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            PasswordStrengthBar(password = password, modifier = Modifier.fillMaxWidth())
                        }
                    }

                    GlassTextField(
                        value = confirmPass,
                        onValueChange = { confirmPass = it; confirmError = false },
                        placeholder = "Confirm Password",
                        leadingIcon = Icons.Default.LockOpen,
                        isPassword = true,
                        passwordVisible = confirmVisible,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                        isError = confirmError,
                        errorMessage = "Passwords do not match",
                        trailingContent = {
                            Icon(
                                if (confirmVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                null, tint = TextTertiary,
                                modifier = Modifier.size(18.dp).clickable { confirmVisible = !confirmVisible }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(4.dp))

                    // Terms checkbox
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (termsError) ErrorRed.copy(0.08f) else Color.Transparent)
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(22.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (termsAccepted) Brush.linearGradient(listOf(AccentCyan, AccentPurple)) else Brush.linearGradient(listOf(GlassSurface, GlassSurface)))
                                .border(1.dp, if (termsAccepted) AccentCyan.copy(0.5f) else GlassBorderTop, RoundedCornerShape(6.dp))
                                .clickable { termsAccepted = !termsAccepted; termsError = false }
                        ) {
                            if (termsAccepted) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Row {
                            Text("I agree to the ", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                            Text("Terms of Service", style = MaterialTheme.typography.bodySmall, color = AccentCyan, modifier = Modifier.clickable { })
                            Text(" & ", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                            Text("Privacy Policy", style = MaterialTheme.typography.bodySmall, color = AccentCyan, modifier = Modifier.clickable { })
                        }
                    }
                    if (termsError) {
                        Text("Please accept the terms to continue", style = MaterialTheme.typography.labelSmall, color = ErrorRed, modifier = Modifier.padding(start = 4.dp))
                    }

                    Spacer(Modifier.height(8.dp))

                    GlassButton(
                        text = if (state.isLoading) "Creating account…" else "Create Account",
                        onClick = {
                            when {
                                fullName.isBlank()         -> nameError    = true
                                !email.contains("@")       -> emailError   = true
                                password.length < 6        -> passError    = true
                                password != confirmPass    -> confirmError = true
                                !termsAccepted             -> termsError   = true
                                else -> viewModel.signup(fullName, email, password)
                            }
                        },
                        gradientColors = listOf(AccentPurple, AccentPink),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = !state.isLoading
                    )

                    if (state.isLoading) {
                        CircularProgressIndicator(color = AccentPurple, modifier = Modifier.size(24.dp).align(Alignment.CenterHorizontally), strokeWidth = 2.dp)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account? ", style = MaterialTheme.typography.bodyMedium, color = TextTertiary)
                Text("Sign In", style = MaterialTheme.typography.bodyMedium, color = AccentCyan, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { navController.popBackStack() })
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
