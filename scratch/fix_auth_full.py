import re
import os

path_orig = r"scratch\AuthScreens.kt"
path_target = r"app\src\main\java\com\pemmob\ulasbuku\ui\screens\AuthScreens.kt"

with open(path_orig, 'r', encoding='utf-8') as f:
    text = f.read()

# ----------------- LOGIN SCREEN -----------------

# Replace the Login Header (Back Button + Hero Banner + Masuk Akun text)
login_header_pattern = r'Spacer\(modifier = Modifier\.height\(12\.dp\)\)\s*// .*? BACK BUTTON.*?Text\(\s*text = "Gunakan username atau email kamu"[^\)]*\)\s*'

new_login_header = """
            Spacer(modifier = Modifier.height(36.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                // Tombol Back di kiri
                IconButton(
                    onClick = onBackToWelcome,
                    modifier = Modifier
                        .align(androidx.compose.ui.Alignment.CenterStart)
                        .size(40.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(com.pemmob.ulasbuku.ui.theme.SoftGray)
                        .border(1.dp, com.pemmob.ulasbuku.ui.theme.BorderSubtle, androidx.compose.foundation.shape.CircleShape)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = com.pemmob.ulasbuku.ui.theme.TextPrimary
                    )
                }
                
                // Teks judul di tengah
                Column(
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Masuk Akun",
                        color = com.pemmob.ulasbuku.ui.theme.TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Gunakan username atau email kamu",
                        color = com.pemmob.ulasbuku.ui.theme.TextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
"""
text = re.sub(login_header_pattern, new_login_header.strip(), text, count=1, flags=re.DOTALL)


# ----------------- REGISTER SCREEN -----------------

# Replace the Register Header (Back Button + Hero Banner + Buat Akun Baru text)
register_header_pattern = r'Spacer\(modifier = Modifier\.height\(12\.dp\)\)\s*// .*? BACK BUTTON.*?Text\(\s*text = "Lengkapi data diri kamu di bawah ini"[^\)]*\)\s*'

new_register_header = """
            Spacer(modifier = Modifier.height(36.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                // Tombol Back di kiri
                IconButton(
                    onClick = onBackToWelcome,
                    modifier = Modifier
                        .align(androidx.compose.ui.Alignment.CenterStart)
                        .size(40.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(com.pemmob.ulasbuku.ui.theme.SoftGray)
                        .border(1.dp, com.pemmob.ulasbuku.ui.theme.BorderSubtle, androidx.compose.foundation.shape.CircleShape)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = com.pemmob.ulasbuku.ui.theme.TextPrimary
                    )
                }
                
                // Teks judul di tengah
                Column(
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Buat Akun Baru",
                        color = com.pemmob.ulasbuku.ui.theme.TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Lengkapi data diri kamu di bawah ini",
                        color = com.pemmob.ulasbuku.ui.theme.TextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
"""
text = re.sub(register_header_pattern, new_register_header.strip(), text, count=1, flags=re.DOTALL)

# Add Name field before Username
name_state = r'var name by remember { mutableStateOf("") }\n      '
text = re.sub(r'(var username by remember \{ mutableStateOf\(""\) \})', name_state + r'\1', text)

name_field = """
            // NAME FIELD
            Text(
                "Nama Lengkap *",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; viewModel.clearAuthError() },
                placeholder = { Text("Contoh: Feizia", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Person, null, tint = TextPrimary) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = ulasBukuTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

"""
reg_start = text.find('fun RegisterScreen')
username_start = text.find('Text(\n                "Username *"', reg_start)
if username_start != -1:
    text = text[:username_start] + name_field + text[username_start:]

# Add Confirm Password field
cp_state = r'\1\n      var confirmPassword by remember { mutableStateOf("") }\n      var confirmPasswordVisible by remember { mutableStateOf(false) }\n      var confirmPasswordError by remember { mutableStateOf(false) }'
text = re.sub(r'(var passwordVisible by remember \{ mutableStateOf\(false\) \})', cp_state, text)

confirm_field = """
            Spacer(modifier = Modifier.height(12.dp))

            // KONFIRMASI PASSWORD
            Text(
                "Konfirmasi Kata Sandi *",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { 
                    confirmPassword = it
                    confirmPasswordError = false
                    viewModel.clearAuthError() 
                },
                placeholder = { Text("Ulangi kata sandi", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = TextPrimary) },
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
"""
err_banner_idx = text.find('// ? ERROR BANNER', reg_start)
if err_banner_idx == -1: # Fallback if emoji was stripped
    err_banner_idx = text.find('if (authError != null) {', reg_start)
    if err_banner_idx != -1:
        # Go back up a bit to insert before the spacer
        err_banner_idx = text.rfind('Spacer', reg_start, err_banner_idx)

if err_banner_idx != -1:
    text = text[:err_banner_idx] + confirm_field + text[err_banner_idx:]

# Update Register Button logic for password check
old_btn = 'if (viewModel.register(username, email, password)) {'
new_btn = 'if (password != confirmPassword) { confirmPasswordError = true } else if (viewModel.register(name, username, email, password)) {'
text = text.replace(old_btn, new_btn)

# Remove Emojis from Toasts and Strings
text = text.replace('Selamat Datang! \ud83d\udc4b', 'Selamat Datang!')
text = text.replace('Selamat datang kembali! \ud83d\udc4b', 'Selamat datang kembali!')
text = text.replace('Akun berhasil dibuat! Selamat membaca \ud83d\udcda', 'Akun berhasil dibuat! Selamat membaca')
text = text.replace('\ud83d\udc4b', '')
text = text.replace('\ud83d\udcda', '')
text = text.replace('\ud83c\udf89', '')

# Replace `.navigationBarsPadding()` with `.imePadding()` inside Column for better keyboard handling
text = text.replace('.navigationBarsPadding()', '.imePadding()')

with open(path_target, 'w', encoding='utf-8') as f:
    f.write(text)

print("AuthScreens full rebuild done!")
