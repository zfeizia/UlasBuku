package com.pemmob.ulasbuku.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pemmob.ulasbuku.data.model.Book
import com.pemmob.ulasbuku.data.model.Review
import com.pemmob.ulasbuku.ui.theme.*
import com.pemmob.ulasbuku.ui.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    book: Book,
    viewModel: BookViewModel,
    onBackClick: () -> Unit,
    onWriteReviewClick: (Book) -> Unit = {}
) {
    val context = LocalContext.current
    val currentBookState by viewModel.selectedBook.collectAsState()
    val activeBook = currentBookState ?: book
    val isBookmarked = viewModel.isBookmarked(activeBook.id)
    val currentUser by viewModel.currentUser.collectAsState()
    val currentReadingStatus = viewModel.getReadingStatus(activeBook.id)

    // Write review state (Inline on Detail Page)
    var userRating by remember { mutableFloatStateOf(5.0f) }
    var reviewComment by remember { mutableStateOf("") }
    var isAnonymous by remember { mutableStateOf(false) }

    // Reading status dropdown state
    var statusMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Buku",
                        color = TextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleBookmark(activeBook.id) }) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Simpan",
                            tint = if (isBookmarked) VividBlue else TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PureWhite,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = PureWhite
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // ── 1. HEADER INFO BUKU ──────────────────────────────────────────
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.5.dp, BorderDark, RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = PureWhite)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Cover HD
                        Box(
                            modifier = Modifier
                                .size(width = 130.dp, height = 180.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(getCategoryColor(activeBook.categoryId))
                                .border(1.5.dp, BorderDark, RoundedCornerShape(16.dp))
                        ) {
                            if (activeBook.coverImg.isNotBlank()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context).data(activeBook.coverImg).crossfade(true).build(),
                                    contentDescription = activeBook.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.AutoStories, null, tint = TextPrimary.copy(0.7f), modifier = Modifier.size(44.dp))
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Judul & Penulis
                        Text(
                            text = activeBook.title,
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = activeBook.author,
                            color = TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(Modifier.height(10.dp))

                        // Metadata (Penerbit & Tahun Terbit)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50.dp),
                                color = SoftGray,
                                modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(50.dp))
                            ) {
                                Text(
                                    text = "🏢 ${activeBook.publisher}",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(50.dp),
                                color = SoftGray,
                                modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(50.dp))
                            ) {
                                Text(
                                    text = "📅 ${activeBook.releaseYear}",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        // Rating Stats Bar
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Star, null, tint = AmberStar, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = String.format("%.1f", activeBook.rating),
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "(${activeBook.reviews.size} Ulasan Komunitas)",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        // ── STATUS BACAAN SELECTOR DROPDOWN ─────────────────
                        Box {
                            Button(
                                onClick = { statusMenuExpanded = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = when (currentReadingStatus) {
                                        "READING" -> PastelPeachGradientStart
                                        "COMPLETED" -> PastelGreenGradientStart
                                        else -> PastelBlueGradientStart
                                    },
                                    contentColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(50.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .border(1.5.dp, BorderDark, RoundedCornerShape(50.dp))
                            ) {
                                Text(
                                    text = when (currentReadingStatus) {
                                        "READING" -> "📖 Sedang Dibaca"
                                        "COMPLETED" -> "✅ Selesai Dibaca"
                                        "WANT_TO_READ" -> "📌 Ingin Dibaca"
                                        else -> "+ Tambahkan ke Rak Buku"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("▾", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            DropdownMenu(
                                expanded = statusMenuExpanded,
                                onDismissRequest = { statusMenuExpanded = false },
                                modifier = Modifier.background(PureWhite)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("📌 Ingin Dibaca", fontSize = 13.sp, fontWeight = FontWeight.Medium) },
                                    onClick = {
                                        viewModel.setReadingStatus(activeBook.id, "WANT_TO_READ")
                                        statusMenuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("📖 Sedang Dibaca", fontSize = 13.sp, fontWeight = FontWeight.Medium) },
                                    onClick = {
                                        viewModel.setReadingStatus(activeBook.id, "READING")
                                        statusMenuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("✅ Selesai Dibaca", fontSize = 13.sp, fontWeight = FontWeight.Medium) },
                                    onClick = {
                                        viewModel.setReadingStatus(activeBook.id, "COMPLETED")
                                        statusMenuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("❌ Hapus dari Rak", fontSize = 13.sp, color = CoralRed) },
                                    onClick = {
                                        viewModel.setReadingStatus(activeBook.id, "")
                                        statusMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ── 2. SINOPSIS BUKU ───────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Sinopsis Lengkap 📖",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = activeBook.synopsis,
                        color = TextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            // ── 3. TULIS ULASAN SAYA (LANGSUNG DI TEMPAT) ───────────────────
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 14.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.5.dp, BorderDark, RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = PureWhite)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Tulis Ulasan Saya ✍️",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Berikan ulasan & rating bintang untuk buku ini",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(Modifier.height(14.dp))

                        // Interactive 5-Star Rating Selector
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            (1..5).forEach { starIndex ->
                                val isFilled = starIndex <= userRating
                                IconButton(
                                    onClick = { userRating = starIndex.toFloat() },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isFilled) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "Rating $starIndex",
                                        tint = if (isFilled) AmberStar else TextMuted,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "${userRating.toInt()}.0",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        // Text Area Ulasan
                        OutlinedTextField(
                            value = reviewComment,
                            onValueChange = { reviewComment = it },
                            placeholder = { Text("Tulis pendapatmu tentang buku ini...", color = TextMuted, fontSize = 13.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ulasBukuTextFieldColors(),
                            maxLines = 4
                        )

                        Spacer(Modifier.height(10.dp))

                        // Toggle Checkbox Kirim sebagai Anonim
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isAnonymous = !isAnonymous }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = isAnonymous,
                                onCheckedChange = { isAnonymous = it },
                                colors = CheckboxDefaults.colors(checkedColor = DarkButton)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Kirim sebagai Anonim (Tanpa nama akun)",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                if (reviewComment.isBlank()) {
                                    Toast.makeText(context, "Silakan ketik ulasan terlebih dahulu", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val success = viewModel.addReview(
                                    bookId = activeBook.id,
                                    reviewerName = currentUser?.name ?: "Pembaca",
                                    reviewerEmail = currentUser?.email ?: "",
                                    rating = userRating,
                                    comment = reviewComment,
                                    isAnonymous = isAnonymous
                                )
                                if (success) {
                                    Toast.makeText(context, "Ulasan berhasil dikirim! 🎉", Toast.LENGTH_SHORT).show()
                                    reviewComment = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkButton,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .border(1.5.dp, BorderDark, RoundedCornerShape(50.dp))
                        ) {
                            Text("Kirim Ulasan", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            // ── 4. DAFTAR ULASAN & THREAD KOMENTAR ─────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Diskusi Ulasan Pembaca 💬",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "${activeBook.reviews.size} Ulasan",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (activeBook.reviews.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Belum ada ulasan untuk buku ini. Jadilah yang pertama!", color = TextMuted, fontSize = 13.sp)
                    }
                }
            } else {
                items(activeBook.reviews) { review ->
                    ReviewThreadCard(
                        review = review,
                        onLikeClick = { viewModel.toggleAgree(review.id) },
                        onSendReply = { replyText ->
                            viewModel.addReply(
                                reviewId = review.id,
                                replyText = replyText,
                                replierName = currentUser?.name ?: "Pembaca UlasBuku"
                            )
                        }
                    )
                }
            }
        }
    }
}

/**
 * Review Card with Inline Reply Thread
 */
@Composable
private fun ReviewThreadCard(
    review: Review,
    onLikeClick: () -> Unit,
    onSendReply: (String) -> Unit
) {
    var replyInput by remember { mutableStateOf("") }
    var isReplying by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.5.dp, BorderDark, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = PureWhite)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Reviewer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(PastelYellowGradientStart)
                            .border(1.dp, BorderDark, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (review.isAnonymous) "Pengulas Anonim 🤫" else review.reviewerName,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(review.date, color = TextMuted, fontSize = 10.sp)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = SoftGray,
                    modifier = Modifier.border(1.dp, BorderDark, RoundedCornerShape(50.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, null, tint = AmberStar, modifier = Modifier.size(11.dp))
                        Spacer(Modifier.width(2.dp))
                        Text(String.format("%.1f", review.userRating), color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Text(review.comment, color = TextPrimary, fontSize = 13.sp, lineHeight = 19.sp)
            Spacer(Modifier.height(12.dp))

            // Action Row (Suka & Balas Button)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .clickable { onLikeClick() }
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (review.isAgreedByUser) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (review.isAgreedByUser) CoralRed else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${review.agreeCount} Suka",
                        color = if (review.isAgreedByUser) CoralRed else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "💬 Balas Komentar (${review.replies.size})",
                    color = VividBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .clickable { isReplying = !isReplying }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }

            // Existing Replies Thread List
            if (review.replies.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SoftGray)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    review.replies.forEach { reply ->
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(reply.replierName, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.width(6.dp))
                                Text(reply.date, color = TextMuted, fontSize = 9.sp)
                            }
                            Text(reply.replyText, color = TextSecondary, fontSize = 11.sp, lineHeight = 15.sp)
                        }
                    }
                }
            }

            // Inline Reply Input Form
            if (isReplying) {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = replyInput,
                        onValueChange = { replyInput = it },
                        placeholder = { Text("Tulis balasan komentar...", fontSize = 12.sp, color = TextMuted) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(50.dp),
                        colors = ulasBukuTextFieldColors()
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (replyInput.isNotBlank()) {
                                onSendReply(replyInput)
                                replyInput = ""
                                isReplying = false
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(DarkButton)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, "Kirim", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
