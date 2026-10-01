package com.pemmob.ulasbuku.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pemmob.ulasbuku.ui.theme.Buttermilk
import com.pemmob.ulasbuku.ui.theme.OldBurgundy
import com.pemmob.ulasbuku.ui.theme.PastelBlue

/**
 * Maskot Resmi UlasBuku: "Buki"
 * Mengusung gaya ilustrasi minimalis-retropop ala referensi 'Petal' (Aptivo).
 * Menggunakan palet resmi:
 * - Tubuh sampul: Pastel Blue (#C1DBE8)
 * - Perut / Lembaran buku & Pita: Buttermilk (#FFF1B5)
 * - Outline, Mata ekspresif, dan Senyuman: Old Burgundy (#43302E)
 *
 * Dilengkapi animasi idle bernapas halus dan kedipan mata natural.
 */
@Composable
fun BukiMascot(
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    animate: Boolean = true
) {
    // 1. Animasi mengapung / bernapas lembut
    val infiniteTransition = rememberInfiniteTransition(label = "BukiAnimation")
    
    val floatY by if (animate) {
        infiniteTransition.animateFloat(
            initialValue = -5f,
            targetValue = 5f,
            animationSpec = infiniteRepeatable(
                animation = tween(2400, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "floatY"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    // 2. Animasi kedipan mata berkala
    val blinkScaleY by if (animate) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 3500
                    1f at 0
                    1f at 3000
                    0.08f at 3100
                    1f at 3220
                    1f at 3500
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "blinkScaleY"
        )
    } else {
        remember { mutableFloatStateOf(1f) }
    }

    Box(
        modifier = modifier
            .size(size)
            .offset(y = floatY.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            val strokeWidth = w * 0.032f // Outline tegas berkarakter
            val burgundy = OldBurgundy
            val pastelBlue = PastelBlue
            val buttermilk = Buttermilk

            // ── A. PITA PEMBATAS BUKU (Bookmark Ribbon di Pojok Kiri Atas) ──
            val ribbonPath = Path().apply {
                val rx = w * 0.22f
                val ry = h * 0.04f
                val rw = w * 0.14f
                val rh = h * 0.24f
                moveTo(rx, ry)
                lineTo(rx + rw, ry)
                lineTo(rx + rw, ry + rh)
                lineTo(rx + (rw / 2f), ry + (rh * 0.78f)) // Lekukan v-notch pita
                lineTo(rx, ry + rh)
                close()
            }
            // Fill Ribbon
            drawPath(path = ribbonPath, color = buttermilk, style = Fill)
            // Outline Ribbon
            drawPath(
                path = ribbonPath,
                color = burgundy,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // ── B. TUBUH BUKU (Siluet Buku Empuk / Rounded Hardcover) ──
            val bodyLeft = w * 0.12f
            val bodyTop = h * 0.15f
            val bodyWidth = w * 0.76f
            val bodyHeight = h * 0.74f
            val cornerRadius = CornerRadius(w * 0.22f, w * 0.22f)

            val bodyRoundRect = RoundRect(
                Rect(
                    left = bodyLeft,
                    top = bodyTop,
                    right = bodyLeft + bodyWidth,
                    bottom = bodyTop + bodyHeight
                ),
                cornerRadius
            )
            val bodyPath = Path().apply { addRoundRect(bodyRoundRect) }

            // Fill Tubuh Utama (Pastel Blue)
            drawPath(path = bodyPath, color = pastelBlue, style = Fill)

            // ── C. PERUT / LEMBARAN BUKU DALAM (Buttermilk Cozy Inner Belly) ──
            val bellyLeft = w * 0.22f
            val bellyTop = h * 0.52f
            val bellyWidth = w * 0.56f
            val bellyHeight = h * 0.33f
            val bellyRoundRect = RoundRect(
                Rect(
                    left = bellyLeft,
                    top = bellyTop,
                    right = bellyLeft + bellyWidth,
                    bottom = bellyTop + bellyHeight
                ),
                CornerRadius(w * 0.16f, w * 0.16f)
            )
            val bellyPath = Path().apply { addRoundRect(bellyRoundRect) }
            drawPath(path = bellyPath, color = buttermilk.copy(alpha = 0.85f), style = Fill)

            // Garis Lembaran Halaman Halus di Perut
            val pageLineY1 = bellyTop + bellyHeight * 0.38f
            val pageLineY2 = bellyTop + bellyHeight * 0.65f
            drawLine(
                color = burgundy.copy(alpha = 0.25f),
                start = Offset(bellyLeft + w * 0.08f, pageLineY1),
                end = Offset(bellyLeft + bellyWidth - w * 0.08f, pageLineY1),
                strokeWidth = strokeWidth * 0.5f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = burgundy.copy(alpha = 0.25f),
                start = Offset(bellyLeft + w * 0.12f, pageLineY2),
                end = Offset(bellyLeft + bellyWidth - w * 0.12f, pageLineY2),
                strokeWidth = strokeWidth * 0.5f,
                cap = StrokeCap.Round
            )

            // Outline Tubuh Utama
            drawPath(
                path = bodyPath,
                color = burgundy,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Punggung Buku / Spine Accent (Garis lengkung di sisi kiri)
            val spinePath = Path().apply {
                moveTo(bodyLeft + w * 0.09f, bodyTop + h * 0.05f)
                quadraticTo(
                    bodyLeft + w * 0.06f, bodyTop + (bodyHeight / 2f),
                    bodyLeft + w * 0.09f, bodyTop + bodyHeight - h * 0.05f
                )
            }
            drawPath(
                path = spinePath,
                color = burgundy.copy(alpha = 0.3f),
                style = Stroke(width = strokeWidth * 0.7f, cap = StrokeCap.Round)
            )

            // ── D. PIPI MERONA (Soft Warm Rosy Blushes) ──
            val blushRadiusX = w * 0.065f
            val blushRadiusY = h * 0.035f
            val blushY = h * 0.44f

            drawOval(
                color = Color(0xFFF6B5A2).copy(alpha = 0.6f),
                topLeft = Offset(w * 0.24f - blushRadiusX, blushY - blushRadiusY),
                size = Size(blushRadiusX * 2, blushRadiusY * 2)
            )
            drawOval(
                color = Color(0xFFF6B5A2).copy(alpha = 0.6f),
                topLeft = Offset(w * 0.76f - blushRadiusX, blushY - blushRadiusY),
                size = Size(blushRadiusX * 2, blushRadiusY * 2)
            )

            // ── E. MATA KARTUN BESAR & BERSINAR (Gaya 'Petal' Aptivo) ──
            val eyeCenterY = h * 0.38f
            val eyeRadius = w * 0.11f
            val leftEyeCenterX = w * 0.38f
            val rightEyeCenterX = w * 0.62f

            // Menggambar satu mata lengkap dengan kelopak/bulu mata & glint
            fun drawPetalEye(centerX: Float, isLeft: Boolean) {
                // Tiga bulu mata lentik di atas mata (khas referensi Aptivo)
                val lashBaseY = eyeCenterY - eyeRadius * 0.95f
                val lashLength = eyeRadius * 0.55f

                // Bulu mata kiri
                drawLine(
                    color = burgundy,
                    start = Offset(centerX - eyeRadius * 0.55f, lashBaseY),
                    end = Offset(centerX - eyeRadius * 0.85f, lashBaseY - lashLength * 0.7f),
                    strokeWidth = strokeWidth * 0.7f,
                    cap = StrokeCap.Round
                )
                // Bulu mata tengah
                drawLine(
                    color = burgundy,
                    start = Offset(centerX, lashBaseY - eyeRadius * 0.1f),
                    end = Offset(centerX, lashBaseY - lashLength),
                    strokeWidth = strokeWidth * 0.7f,
                    cap = StrokeCap.Round
                )
                // Bulu mata kanan
                drawLine(
                    color = burgundy,
                    start = Offset(centerX + eyeRadius * 0.55f, lashBaseY),
                    end = Offset(centerX + eyeRadius * 0.85f, lashBaseY - lashLength * 0.7f),
                    strokeWidth = strokeWidth * 0.7f,
                    cap = StrokeCap.Round
                )

                // Pupil Hitam Burgundy (dengan animasi kedipan Y)
                val pupilRadiusY = eyeRadius * blinkScaleY
                drawOval(
                    color = burgundy,
                    topLeft = Offset(centerX - eyeRadius, eyeCenterY - pupilRadiusY),
                    size = Size(eyeRadius * 2, pupilRadiusY * 2)
                )

                // Pantulan Cahaya Putih (Specular Highlights) — hanya saat mata terbuka
                if (blinkScaleY > 0.4f) {
                    // Highlight utama (besar di kiri atas)
                    val glintMajorRadius = eyeRadius * 0.38f * blinkScaleY
                    drawCircle(
                        color = Color.White,
                        radius = glintMajorRadius,
                        center = Offset(centerX - eyeRadius * 0.32f, eyeCenterY - pupilRadiusY * 0.3f)
                    )

                    // Highlight kedua (kecil di kanan bawah)
                    val glintMinorRadius = eyeRadius * 0.16f * blinkScaleY
                    drawCircle(
                        color = Color.White,
                        radius = glintMinorRadius,
                        center = Offset(centerX + eyeRadius * 0.36f, eyeCenterY + pupilRadiusY * 0.34f)
                    )
                }
            }

            drawPetalEye(leftEyeCenterX, isLeft = true)
            drawPetalEye(rightEyeCenterX, isLeft = false)

            // ── F. SENYUMAN RAMAH & MANIS (Curved Smile Arc) ──
            val mouthPath = Path().apply {
                val mouthY = h * 0.47f
                val mouthWidth = w * 0.12f
                moveTo((w / 2f) - mouthWidth, mouthY)
                quadraticTo(
                    w / 2f, mouthY + h * 0.045f,
                    (w / 2f) + mouthWidth, mouthY
                )
            }
            drawPath(
                path = mouthPath,
                color = burgundy,
                style = Stroke(width = strokeWidth * 0.9f, cap = StrokeCap.Round)
            )
        }
    }
}
