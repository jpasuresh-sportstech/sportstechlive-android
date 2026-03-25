package com.sportstechbrands.sportstechlive.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.sportstechbrands.sportstechlive.ui.components.*
import com.sportstechbrands.sportstechlive.ui.theme.*

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    var email    by remember { mutableStateOf("") }
    var isError  by remember { mutableStateOf(false) }
    var sent     by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(56.dp))

            // Back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(42.dp)
                        .liquidGlass(cornerRadius = 21.dp, fillAlpha = 0.12f, borderAlpha = 0.35f)
                        .clickable { navController.popBackStack() }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(48.dp))

            // Animated icon — switches between envelope and checkmark
            AnimatedContent(
                targetState = sent,
                transitionSpec = {
                    (scaleIn(initialScale = 0.6f) + fadeIn()) togetherWith
                    (scaleOut(targetScale = 0.6f) + fadeOut())
                },
                label = "iconTransition"
            ) { isSent ->
                ForgotPasswordIcon(success = isSent)
            }

            Spacer(Modifier.height(28.dp))

            // Heading + subtitle
            AnimatedContent(
                targetState = sent,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "headingTransition"
            ) { isSent ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isSent) "Check Your Email" else "Reset Password",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (isSent)
                            "We've sent a password reset link to\n$email"
                        else
                            "Enter your email and we'll send\nyou a reset link",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextTertiary,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            // Form card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                cornerRadius = 28.dp,
                fillAlpha = 0.13f,
                borderAlpha = 0.4f
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(AccentCyan.copy(0.04f), AccentPurple.copy(0.04f))
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!sent) {
                        // Email field
                        GlassTextField(
                            value = email,
                            onValueChange = { email = it; isError = false },
                            placeholder = "Email address",
                            leadingIcon = Icons.Default.Email,
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done,
                            onImeAction = { sendReset(email, onError = { isError = true }, onSuccess = { sent = true }) },
                            isError = isError,
                            errorMessage = "Enter a valid email address",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(24.dp))

                        GlassButton(
                            text = "Send Reset Link",
                            onClick = {
                                sendReset(
                                    email,
                                    onError   = { isError = true },
                                    onSuccess = { sent = true }
                                )
                            },
                            gradientColors = listOf(AccentCyan, AccentPurple),
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        )
                    } else {
                        // Success state content
                        SuccessDetails(email = email)

                        Spacer(Modifier.height(24.dp))

                        // Resend button
                        GlassButton(
                            text = "Resend Email",
                            onClick = { /* resend */ },
                            gradientColors = listOf(GlassSurfaceHi, GlassSurfaceMid),
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        )

                        Spacer(Modifier.height(12.dp))

                        GlassButton(
                            text = "Back to Sign In",
                            onClick = { navController.popBackStack() },
                            gradientColors = listOf(AccentCyan, AccentPurple),
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        )
                    }
                }
            }

            if (!sent) {
                Spacer(Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Remembered it? ", style = MaterialTheme.typography.bodyMedium, color = TextTertiary)
                    Text(
                        "Sign In",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AccentCyan,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { navController.popBackStack() }
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Animated icon (envelope / checkmark)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ForgotPasswordIcon(success: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "fpGlow")
    val glow by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "fpGlowF"
    )
    val accentColor = if (success) AccentGreen else AccentCyan

    Box(contentAlignment = Alignment.Center) {
        // Glow ring
        Box(
            modifier = Modifier
                .size(110.dp)
                .background(
                    Brush.radialGradient(
                        listOf(accentColor.copy(alpha = 0.18f * glow), Color.Transparent)
                    ),
                    CircleShape
                )
        )
        // Icon circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .liquidGlass(cornerRadius = 40.dp, fillAlpha = 0.15f, borderAlpha = 0.5f)
                .background(
                    Brush.linearGradient(listOf(accentColor.copy(0.2f), accentColor.copy(0.05f))),
                    CircleShape
                )
        ) {
            if (success) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = AccentGreen,
                    modifier = Modifier.size(36.dp)
                )
            } else {
                Icon(
                    Icons.Default.MailOutline,
                    contentDescription = null,
                    tint = AccentCyan,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Success detail rows
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SuccessDetails(email: String) {
    listOf(
        "📧" to "Email sent to $email",
        "⏱" to "Link expires in 15 minutes",
        "🔍" to "Check your spam folder if not received"
    ).forEach { (icon, text) ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                    .background(GlassSurface)
            ) {
                Text(icon, fontSize = 16.sp)
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Submit logic
// ─────────────────────────────────────────────────────────────────────────────

private fun sendReset(email: String, onError: () -> Unit, onSuccess: () -> Unit) {
    if (!email.contains("@") || !email.contains(".")) {
        onError(); return
    }
    onSuccess()
}
