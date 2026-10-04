package com.pemmob.ulasbuku.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.draw.shadow
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

// ─────────────────────────────────────────────────────────────────────────────
// BOOK DETAIL SCREEN
// Urutan: Cover → Judul → Penulis → ISBN → Rating → Jumlah Ulasan
//         → Sinopsis → Daftar Ulasan + Tombol "Tulis Ulasan" (pass bookId)
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    book: Book,
    viewModel: BookViewModel,
    onBackClick: () -> Unit,
    onWriteReviewClick: (Int) -> Unit = {},
    onSeeAllReviewsClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val currentBookState by viewModel.selectedBook.collectAsState()
    val activeBook = currentBookState ?: book
    val bookmarkedBooks by viewModel.bookmarkedBooks.collectAsState()
    val isBookmarked = bookmarkedBooks.any { it.id == activeBook.id }
    val currentUser by viewModel.currentUser.collectAsState()

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
                    // Bookmark toggle
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

            // ─────────────────────────────────────────────────────────────
            // 1. HEADER KARTU: Cover + Judul + Penulis + ISBN + Rating + Jumlah Ulasan
            // ─────────────────────────────────────────────────────────────
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(24.dp)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.35f)
                                .aspectRatio(0.67f)
                                .shadow(
                                    elevation = 12.dp,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .clip(RoundedCornerShape(6.dp))
                                .background(getCategoryColor(activeBook.categoryId))
                        ) {
                            if (activeBook.displayCoverImg.isNotBlank()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(activeBook.displayCoverImg)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = activeBook.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Box(
                                    Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.AutoStories,
                                        null,
                                        tint = TextPrimary.copy(0.7f),
                                        modifier = Modifier.size(44.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // ── Judul ────────────────────────────────────────
                        Text(
                            text = activeBook.title,
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(4.dp))

                        // ── Penulis ───────────────────────────────────────
                        Text(
                            text = activeBook.author,
                            color = TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(Modifier.height(10.dp))

                        // ── ISBN ──────────────────────────────────────────
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = SoftGray,
                            modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(50.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Tag,
                                    null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "ISBN: ${activeBook.isbn}",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // ── Rating + Jumlah Ulasan ────────────────────────
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(5) { idx ->
                                Icon(
                                    imageVector = if (idx < activeBook.rating.toInt()) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = AmberStar,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = String.format("%.1f", activeBook.rating),
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "· ${activeBook.reviews.size} ulasan",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        // ── Metadata pills (Penerbit & Tahun) ─────────────
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(50.dp),
                                color = SoftGray,
                                modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(50.dp))
                            ) {
                                Text(
                                    text = activeBook.displayPublisher,
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
                                    text = activeBook.displayReleaseYear,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // ── Tombol Tulis Ulasan (pass bookId) ────────────────
                        Button(
                            onClick = { onWriteReviewClick(activeBook.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkButton,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .border(1.5.dp, BorderDark, RoundedCornerShape(50.dp))
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Tulis Ulasan",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // ─────────────────────────────────────────────────────────────
            // 2. SINOPSIS
            // ─────────────────────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Sinopsis",
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
                        textAlign = TextAlign.Justify
                    )
                }
            }

            // ─────────────────────────────────────────────────────────────
            // 3. HEADER DAFTAR ULASAN + TOMBOL "TULIS ULASAN"
            // ─────────────────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Ulasan Pembaca",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "${activeBook.reviews.size} ulasan ditulis",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = "Semua",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onSeeAllReviewsClick() }
                    )
                }
            }

            // ─────────────────────────────────────────────────────────────
            // 4. DAFTAR ULASAN
            // ─────────────────────────────────────────────────────────────
            if (activeBook.reviews.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Belum ada ulasan. Jadilah yang pertama!",
                            color = TextMuted,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(
                    items = activeBook.reviews.take(3),
                    key = { review -> "review_${review.id}" }
                ) { review ->
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

// ─────────────────────────────────────────────────────────────────────────────
// REVIEW THREAD CARD — Ulasan + thread balasan inline
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun ReviewThreadCard(
    review: Review,
    onLikeClick: () -> Unit,
    onSendReply: (String) -> Unit
) {
    var replyInput by remember { mutableStateOf("") }
    var isReplying by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, BorderDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // ── Header Reviewer ──────────────────────────────────────────
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
                        Text(
                            text = if (review.isAnonymous) "?" else
                                review.reviewerName.firstOrNull()?.uppercase() ?: "?",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (review.isAnonymous) "Pengulas Anonim" else review.reviewerName,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(review.date, color = TextMuted, fontSize = 10.sp)
                    }
                }
                // Rating pill
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
                        Text(
                            String.format("%.1f", review.userRating),
                            color = TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Text(review.comment, color = TextPrimary, fontSize = 13.sp, lineHeight = 19.sp)
            Spacer(Modifier.height(12.dp))

            // ── Aksi: Suka & Balas ───────────────────────────────────────
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
                    text = "Balas (${review.replies.size})",
                    color = VividBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .clickable { isReplying = !isReplying }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }

            // ── Thread Balasan ───────────────────────────────────────────
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

            // ── Input Balasan Inline ─────────────────────────────────────
            if (isReplying) {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = replyInput,
                        onValueChange = { replyInput = it },
                        placeholder = {
                            Text("Tulis balasan...", fontSize = 12.sp, color = TextMuted)
                        },
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
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            "Kirim",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
