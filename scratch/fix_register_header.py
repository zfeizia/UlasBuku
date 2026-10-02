import re

path_auth = r"app\src\main\java\com\pemmob\ulasbuku\ui\screens\AuthScreens.kt"
with open(path_auth, 'r', encoding='utf-8') as f:
    text = f.read()

# Using regex to find the Row block precisely
regex = r'Spacer\(modifier = Modifier\.height\(12\.dp\)\)\s*// .*? HEADER ROW: Back \+ Title.*?\n\s*Row\([^)]*\)\s*\{[\s\S]*?Text\(\s*text = "Lengkapi data diri kamu di bawah ini"[\s\S]*?\)\s*\}\s*\}'

replacement = """
              // Tambahan jarak dari atas agar tidak terlalu mepet status bar
              Spacer(modifier = Modifier.height(36.dp))

              Box(
                  modifier = Modifier.fillMaxWidth(),
                  contentAlignment = Alignment.Center
              ) {
                  // Tombol Back di kiri
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
                  
                  // Teks judul di tengah (Center)
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
"""

if re.search(regex, text):
    text = re.sub(regex, replacement.strip(), text, count=1)
    with open(path_auth, 'w', encoding='utf-8') as f:
        f.write(text)
    print("Berhasil diubah!")
else:
    print("Gagal menemukan regex, mari kita coba cara lain.")

