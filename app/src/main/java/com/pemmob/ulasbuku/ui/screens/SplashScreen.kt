package com.pemmob.ulasbuku.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.ulasbuku.R
import com.pemmob.ulasbuku.ui.theme.PureWhite
import com.pemmob.ulasbuku.ui.theme.TextMuted
import com.pemmob.ulasbuku.ui.theme.TextPrimary
import com.pemmob.ulasbuku.ui.theme.TextSecondary
import com.pemmob.ulasbuku.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Single-Canvas Seamless Splash & Welcome Experience:
 * Satu halaman yang sama, dengan koreografi gerakan fisik kontinu (tanpa cut/ganti halaman):
 * 1. Awal: Kanvas biru pastel, mata maskot di tengah, tulisan 'UlasBuku' putih.
 * 2. Transisi: Warna kanvas mencair ke krem, tulisan 'UlasBuku' berubah ke cokelat pekat
 *    sambil meluncur naik ke atas, tagline muncul, dan maskot mulai naik mengintip dari bawah.
 * 3. Akhir: Maskot terus meluncur naik ke tengah layar, dan tombol aksi ikut meluncur naik dari bawah.
 */
enum class MotionStage {
    INITIAL_FACE,   // Layar biru, wajah di tengah, judul putih
    RISING_PEEK,    // Background berubah ke krem, judul naik ke atas, maskot mengintip dari bawah
    FINAL_COMPLETE  // Maskot naik penuh ke tengah, tombol meluncur naik dari bawah
}

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onContinueAsGuest: () -> Unit = {}
) {
    var stage by remember { mutableStateOf(MotionStage.INITIAL_FACE) }

    // Koreografi waktu gerakan kontinu di satu kanvas
    LaunchedEffect(Unit) {
        delay(1200) // Tampilan awal wajah di layar biru
        stage = MotionStage.RISING_PEEK
        delay(950)  // Maskot mengalir naik dari bawah
        stage = MotionStage.FINAL_COMPLETE // Maskot menetap di tengah & tombol muncul
    }

    val splash1Blue = Color(0xFFA8B9E4)
    val creamBg = Color(0xFFFAF7F2)
    val oldBurgundy = OldBurgundy // #43302E

    // 1. Interpolasi Warna Background (Mencair dari Biru ke Krem)
    val animatedBgColor by animateColorAsState(
        targetValue = if (stage == MotionStage.INITIAL_FACE) splash1Blue else creamBg,
        animationSpec = tween(durationMillis = 850, easing = EaseInOutCubic),
        label = "canvasBgAnim"
    )

    // 2. Warna Teks Judul (Berubah dari Putih ke Old Burgundy)
    val titleColor by animateColorAsState(
        targetValue = if (stage == MotionStage.INITIAL_FACE) Color.White else oldBurgundy,
        animationSpec = tween(durationMillis = 750, easing = EaseInOutCubic),
        label = "titleColorAnim"
    )

    // 3. Posisi Vertikal Judul 'UlasBuku' (Dari tengah layar meluncur naik ke atas)
    val titleOffsetY by animateFloatAsState(
        targetValue = when (stage) {
            MotionStage.INITIAL_FACE -> 80f  // Berada tepat di bawah wajah maskot di tengah
            MotionStage.RISING_PEEK -> -220f // Meluncur naik ke posisi header atas
            MotionStage.FINAL_COMPLETE -> -220f
        },
        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow),
        label = "titleOffsetAnim"
    )

    // 4. Alpha Wajah Awal (Memudar saat transisi dimulai)
    val faceAlpha by animateFloatAsState(
        targetValue = if (stage == MotionStage.INITIAL_FACE) 1f else 0f,
        animationSpec = tween(durationMillis = 500, easing = EaseInOut),
        label = "faceAlphaAnim"
    )

    // 5. Alpha Tagline (Muncul saat judul tiba di atas)
    val taglineAlpha by animateFloatAsState(
        targetValue = if (stage != MotionStage.INITIAL_FACE) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 200, easing = EaseInOut),
        label = "taglineAlphaAnim"
    )

    // 6. Posisi Vertikal Maskot Full (Mengalir naik dari bawah layar ke tengah)
    val mascotOffsetY by animateFloatAsState(
        targetValue = when (stage) {
            MotionStage.INITIAL_FACE -> 500f // Berada di bawah luar layar
            MotionStage.RISING_PEEK -> 210f  // Mengintip dari bawah layar (hanya bagian atas tubuh terlihat)
            MotionStage.FINAL_COMPLETE -> 10f // Berdiri tegak utuh di tengah layar
        },
        animationSpec = spring(
            dampingRatio = 0.82f,
            stiffness = Spring.StiffnessLow // Gerakan pegas halus & organik
        ),
        label = "mascotOffsetAnim"
    )

    val mascotAlpha by animateFloatAsState(
        targetValue = if (stage == MotionStage.INITIAL_FACE) 0f else 1f,
        animationSpec = tween(durationMillis = 400),
        label = "mascotAlphaAnim"
    )

    // 7. Posisi & Alpha Tombol Aksi (Meluncur naik dari bawah layar pada tahap akhir)
    val buttonsOffsetY by animateFloatAsState(
        targetValue = if (stage == MotionStage.FINAL_COMPLETE) 0f else 160f,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow),
        label = "buttonsOffsetAnim"
    )

    val buttonsAlpha by animateFloatAsState(
        targetValue = if (stage == MotionStage.FINAL_COMPLETE) 1f else 0f,
        animationSpec = tween(durationMillis = 500, delayMillis = 100),
        label = "buttonsAlphaAnim"
    )

    // =========================================================================
    // KANVAS TUNGGAL (SEMUA ELEMEN HIDUP BERSAMA SECARA KONTINU)
    // =========================================================================
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Sentuhan pengguna untuk langsung mempercepat ke posisi akhir jika ingin cepat login
                if (stage != MotionStage.FINAL_COMPLETE) {
                    stage = MotionStage.FINAL_COMPLETE
                }
            }
    ) {
        // ── A. WAJAH MASKOT AWAL (Hanya tampil di fase awal di tengah layar) ──
        if (faceAlpha > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(faceAlpha),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.buki_face),
                    contentDescription = "Buki Face",
                    modifier = Modifier
                        .size(270.dp)
                        .offset(y = (-60).dp)
                        .padding(horizontal = 8.dp)
                )
            }
        }

        // ── B. JUDUL 'UlasBuku' & TAGLINE (Satu kesatuan meluncur dari tengah ke atas) ──
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = titleOffsetY.dp)
            ) {
                Text(
                    text = "UlasBuku",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 42.sp,
                    color = titleColor,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tagline memudar muncul di bawah judul saat judul naik
                Text(
                    text = "Temukan bacaan tepat dari ulasan\npembaca terpercaya",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = oldBurgundy.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .alpha(taglineAlpha)
                        .padding(horizontal = 24.dp)
                )
            }
        }

        // ── C. MASKOT BUKU (Satu objek kontinu meluncur naik dari bawah ke tengah) ──
        if (mascotAlpha > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(mascotAlpha),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.buki_full),
                    contentDescription = "Buki Mascot Full",
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .aspectRatio(0.95f)
                        .offset(y = mascotOffsetY.dp)
                )
            }
        }

        // ── D. TOMBOL AKSI (Meluncur naik dari bawah kanvas di tahap akhir) ────
        if (buttonsAlpha > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = buttonsOffsetY.dp)
                        .alpha(buttonsAlpha)
                ) {
                    // Button 1: Daftar Akun Baru (Solid Old Burgundy)
                    Button(
                        onClick = onNavigateToRegister,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .border(1.5.dp, oldBurgundy, RoundedCornerShape(50.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = oldBurgundy,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            text = "Daftar Akun Baru",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Button 2: Masuk ke Akun (Pill Cream dengan Border Old Burgundy)
                    Button(
                        onClick = onNavigateToLogin,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .border(1.5.dp, oldBurgundy, RoundedCornerShape(50.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = creamBg,
                            contentColor = oldBurgundy
                        ),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            text = "Masuk ke Akun",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
