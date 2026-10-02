import os

path = r"app\src\main\java\com\pemmob\ulasbuku\ui\screens\AuthScreens.kt"
with open(path, 'r', encoding='utf-8') as f:
    text = f.read()

target = """
            Spacer(modifier = Modifier.height(12.dp))

            // ?? BACK BUTTON ??????????????????????????????????????????????????????????????????????????????????????????????????
            IconButton(
"""

# Let's just locate the indices directly to avoid exact string matching of comments
start_idx = text.find('Spacer(modifier = Modifier.height(12.dp))')
end_idx = text.find('Spacer(modifier = Modifier.height(24.dp))', start_idx)

# Let's print out what we found to be safe
if start_idx != -1 and end_idx != -1:
    old_block = text[start_idx:end_idx]
    
    new_block = """Spacer(modifier = Modifier.height(36.dp))

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
                
                // Teks judul di tengah (Center)
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
    text = text[:start_idx] + new_block + text[end_idx:]
    with open(path, 'w', encoding='utf-8') as f:
        f.write(text)
    print("Berhasil!")
else:
    print("Gagal menemukan block")

