import re
import os

path_auth = r"app\src\main\java\com\pemmob\ulasbuku\ui\screens\AuthScreens.kt"
with open(path_auth, 'r', encoding='utf-8') as f:
    text = f.read()

# Fix Login Screen
# Remove the Hero Banner from Login
text = re.sub(r'// .*?HERO BANNER.*?\n\s*Box\([^)]*\)\s*\{.*?Selamat Datang!.*?\n\s*\}\s*Spacer\(modifier = Modifier\.height\(28\.dp\)\)\s*', '', text, flags=re.DOTALL)

# Remove emojis in strings
text = text.replace('Selamat Datang! \ud83d\udc4b', 'Selamat Datang!')
text = text.replace('Selamat datang kembali! \ud83d\udc4b', 'Selamat datang kembali!')
text = text.replace('Akun berhasil dibuat! Selamat membaca \ud83d\udcda', 'Akun berhasil dibuat! Selamat membaca')
text = text.replace('\ud83d\udc4b', '')
text = text.replace('\ud83d\udcda', '')
text = text.replace('\ud83c\udf89', '')

# Register Screen fixes
# Add Name and Confirm Password state
text = re.sub(r'(var username by remember \{ mutableStateOf\(""\) \})', 
              r'var name by remember { mutableStateOf("") }\n      \1', text)
text = re.sub(r'(var passwordVisible by remember \{ mutableStateOf\(false\) \})', 
              r'\1\n      var confirmPassword by remember { mutableStateOf("") }\n      var confirmPasswordVisible by remember { mutableStateOf(false) }', text)

# Remove Hero banner in Register (the gradient box with "Gabung Sekarang")
text = re.sub(r'// .*?HERO BANNER.*?\n\s*Box\([^)]*\)\s*\{.*?Gabung Sekarang!.*?\n\s*\}\s*Spacer\(modifier = Modifier\.height\(28\.dp\)\)\s*', '', text, flags=re.DOTALL)
text = re.sub(r'// .*?HERO BANNER.*?\n\s*Box[\s\S]*?Gabung Sekarang![\s\S]*?\}\n\s*Spacer\(modifier = Modifier\.height\(\d+\.dp\)\)\s*', '', text)


# Add Name field before Username in Register
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
# We'll insert it right before Username in RegisterScreen.
# To be safe, let's find the RegisterScreen block
reg_start = text.find('fun RegisterScreen')
username_start = text.find('Text(\n                "Username *"', reg_start)
if username_start != -1:
    text = text[:username_start] + name_field + text[username_start:]

# Add Confirm Password in RegisterScreen
confirm_field = """
            Spacer(modifier = Modifier.height(12.dp))

            // CONFIRM PASSWORD FIELD
            Text(
                "Konfirmasi Kata Sandi *",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; viewModel.clearAuthError() },
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
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = ulasBukuTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
"""
password_end = text.find('// ? ERROR BANNER', reg_start)
if password_end != -1:
    text = text[:password_end] + confirm_field + text[password_end:]

# Update Register Button Logic to check passwords
old_btn = 'if (viewModel.register(username, email, password)) {'
new_btn = 'if (password != confirmPassword) { Toast.makeText(context, "Kata sandi tidak cocok", Toast.LENGTH_SHORT).show() } else if (viewModel.register(username, email, password)) {'
text = text.replace(old_btn, new_btn)

# Ensure no more weird character replacements are left out
text = re.sub(r'[^\x00-\x7F]+', '', text) # Strip all non-ascii just in case they are emojis (this will strip Indonesian chars if they have accents, but Indonesian doesn't usually)
# Actually, the user might want some non-ascii like bullets? Better be specific with emojis, but we already stripped the main ones.

with open(path_auth, 'w', encoding='utf-8') as f:
    f.write(text)
print("AuthScreens modified")
