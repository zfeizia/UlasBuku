package com.pemmob.ulasbuku.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.ulasbuku.data.model.Book
import com.pemmob.ulasbuku.ui.theme.PureWhite
import com.pemmob.ulasbuku.ui.theme.TextMuted
import com.pemmob.ulasbuku.ui.theme.TextPrimary
import com.pemmob.ulasbuku.ui.viewmodel.BookViewModel

/**
 * Halaman buat nampilin semua ulasan dari sebuah buku.
 * Data buku di-observe dari selectedBook ViewModel supaya ikut update kalau ada agree/reply baru.
 * Kalau belum ada ulasan, tampilkan pesan kosong; kalau ada, tampilkan list pakai ReviewThreadCard.
 */
@Composable
fun AllReviewsScreen(
    book: Book,
    viewModel: BookViewModel,
    onBackClick: () -> Unit
) {
    val currentBookState by viewModel.selectedBook.collectAsState()
    val activeBook = currentBookState ?: book
    val currentUser by viewModel.currentUser.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        containerColor = PureWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = "Semua Ulasan",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            if (activeBook.reviews.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada ulasan.",
                        color = TextMuted,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(
                        items = activeBook.reviews,
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
}
