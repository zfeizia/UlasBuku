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
            Spacer(modifier = Modifier.height(36.dp))

            // Header: tombol back kiri, judul tengah
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onBackToWelcome,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Masuk Akun",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Gunakan username atau email kamu",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

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
                        "Username atau email kamu",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                },
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
// Field: Nama, Username, Email, Password, Konfirmasi Password
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

    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            // Header: tombol back kiri, judul tengah
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onBackToWelcome,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Buat Akun Baru",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Lengkapi data diri kamu di bawah ini",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── NAMA LENGKAP ─────────────────────────────────────────────────
            Text("Nama Lengkap *", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; viewModel.clearAuthError() },
                placeholder = { Text("Contoh: Feizia Alverina", color = TextMuted, fontSize = 13.sp) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = ulasBukuTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── USERNAME ─────────────────────────────────────────────────────
            Text("Username *", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it; viewModel.clearAuthError() },
                placeholder = { Text("Contoh: feizia_reads (tanpa spasi)", color = TextMuted, fontSize = 13.sp) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = ulasBukuTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── EMAIL ────────────────────────────────────────────────────────
            Text("Alamat Email *", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; viewModel.clearAuthError() },
                placeholder = { Text("Contoh: feizia@gmail.com", color = TextMuted, fontSize = 13.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(16.dp),
                colors = ulasBukuTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── PASSWORD ─────────────────────────────────────────────────────
            Text("Kata Sandi *", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; viewModel.clearAuthError() },
                placeholder = { Text("Buat kata sandi (min. 6 karakter)", color = TextMuted, fontSize = 13.sp) },
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

            Spacer(modifier = Modifier.height(12.dp))

            // ── KONFIRMASI PASSWORD ──────────────────────────────────────────
            Text("Konfirmasi Kata Sandi *", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = false
                    viewModel.clearAuthError()
                },
                placeholder = { Text("Ulangi kata sandi kamu", color = TextMuted, fontSize = 13.sp) },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = TextMuted
                        )
                    }
                },
                isError = confirmPasswordError,
                supportingText = if (confirmPasswordError) {
                    { Text("Kata sandi tidak cocok", color = CoralRed, fontSize = 11.sp) }
                } else null,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, null, tint = CoralRed, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = authError ?: "", color = CoralRed, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── TOMBOL DAFTAR ────────────────────────────────────────────────
            Button(
                onClick = {
                    if (password != confirmPassword) {
                        confirmPasswordError = true
                    } else if (viewModel.register(name, username, email, password)) {
                        Toast.makeText(context, "Akun berhasil dibuat! Selamat membaca", Toast.LENGTH_SHORT).show()
                        onRegisterSuccess()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkButton, contentColor = Color.White),
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
