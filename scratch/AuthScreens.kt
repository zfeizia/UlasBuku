package com.pemmob.ulasbuku.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.ulasbuku.ui.theme.*
import com.pemmob.ulasbuku.ui.viewmodel.BookViewModel

// ─────────────────────────────────────────────────────────────────────────────
// LOGIN SCREEN
// Mendukung identifier: email ATAU username (dengan/tanpa '@')
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun LoginScreen(
    viewModel: BookViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onBackToWelcome: () -> Unit = {}
) {
    val context = LocalContext.current
    val authError by viewModel.authError.collectAsState()

    // rememberSaveable agar state bertahan saat rotasi
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // ── BACK BUTTON ─────────────────────────────────────────────────
            IconButton(
                onClick = onBackToWelcome,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SoftGray)
                    .border(1.dp, BorderSubtle, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── HERO BANNER ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PastelBlueGradientStart, PastelPurpleGradientStart)
                        )
                    )
                    .border(1.5.dp, BorderDark, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Selamat Datang! 👋",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Masuk untuk melanjutkan ulasan bukumu",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Masuk Akun",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Gunakan username atau email kamu",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── USERNAME / EMAIL ─────────────────────────────────────────────
            Text(
                "Username atau Email",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = identifier,
                onValueChange = { identifier = it; viewModel.clearAuthError() },
                placeholder = {
                    Text(
                        "Contoh: feizia_reads atau feizia@ulasbuku.id",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                },
                leadingIcon = { Icon(Icons.Default.AlternateEmail, null, tint = TextPrimary) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = ulasBukuTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ── PASSWORD ─────────────────────────────────────────────────────
            Text("Kata Sandi", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; viewModel.clearAuthError() },
                placeholder = { Text("Masukkan kata sandi", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = TextPrimary) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = TextMuted
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = ulasBukuTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            // ── ERROR BANNER ─────────────────────────────────────────────────
            if (authError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CoralRed.copy(alpha = 0.1f),
                    modifier = Modifier.border(1.dp, CoralRed.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, null, tint = CoralRed, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = authError ?: "",
                            color = CoralRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── TOMBOL MASUK ─────────────────────────────────────────────────
            Button(
                onClick = {
                    if (viewModel.login(identifier, password)) {
                        Toast.makeText(context, "Selamat datang kembali! 👋", Toast.LENGTH_SHORT).show()
                        onLoginSuccess()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkButton,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .border(1.5.dp, BorderDark, RoundedCornerShape(50.dp))
            ) {
                Text("Masuk Sekarang", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── DEMO ACCOUNT ─────────────────────────────────────────────────
            OutlinedButton(
                onClick = {
                    if (viewModel.login("feizia@ulasbuku.id", "password123")) {
                        Toast.makeText(context, "Masuk sebagai akun demo!", Toast.LENGTH_SHORT).show()
                        onLoginSuccess()
                    }
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, BorderDark),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gunakan Akun Demo", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Belum punya akun? ", color = TextSecondary, fontSize = 13.sp)
                Text(
                    text = "Daftar di sini",
                    color = VividBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// REGISTER SCREEN
// Field: Username, Email, Password
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun RegisterScreen(
    viewModel: BookViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onBackToWelcome: () -> Unit = {}
) {
    val context = LocalContext.current
    val authError by viewModel.authError.collectAsState()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // ── BACK BUTTON ─────────────────────────────────────────────────
            IconButton(
                onClick = onBackToWelcome,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SoftGray)
                    .border(1.dp, BorderSubtle, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── HERO BANNER ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PastelPeachGradientStart, PastelYellowGradientStart)
                        )
                    )
                    .border(1.5.dp, BorderDark, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Gabung Sekarang! 🎉",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Buat akunmu dan mulai eksplorasi ulasan buku",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Buat Akun Baru",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Lengkapi data diri kamu di bawah ini",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // ── USERNAME ─────────────────────────────────────────────────────
            Text(
                "Username *",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it; viewModel.clearAuthError() },
                placeholder = {
                    Text(
                        "Contoh: feizia_reads (tanpa spasi)",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = { Icon(Icons.Default.AlternateEmail, null, tint = TextPrimary) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = ulasBukuTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── EMAIL ────────────────────────────────────────────────────────
            Text(
                "Alamat Email *",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; viewModel.clearAuthError() },
                placeholder = { Text("Contoh: feizia@gmail.com", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Email, null, tint = TextPrimary) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(16.dp),
                colors = ulasBukuTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── PASSWORD ─────────────────────────────────────────────────────
            Text(
                "Kata Sandi *",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; viewModel.clearAuthError() },
                placeholder = {
                    Text(
                        "Buat kata sandi (min. 6 karakter)",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = TextPrimary) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = TextMuted
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = ulasBukuTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            // ── ERROR BANNER ─────────────────────────────────────────────────
            if (authError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CoralRed.copy(alpha = 0.1f),
                    modifier = Modifier.border(
                        1.dp,
                        CoralRed.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            null,
                            tint = CoralRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = authError ?: "",
                            color = CoralRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── TOMBOL DAFTAR ────────────────────────────────────────────────
            Button(
                onClick = {
                    if (viewModel.register(username, email, password)) {
                        Toast.makeText(
                            context,
                            "Akun berhasil dibuat! Selamat membaca 📚",
                            Toast.LENGTH_SHORT
                        ).show()
                        onRegisterSuccess()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkButton,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .border(1.5.dp, BorderDark, RoundedCornerShape(50.dp))
            ) {
                Text("Daftar Akun Sekarang", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sudah punya akun? ", color = TextSecondary, fontSize = 13.sp)
                Text(
                    text = "Masuk di sini",
                    color = VividBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
