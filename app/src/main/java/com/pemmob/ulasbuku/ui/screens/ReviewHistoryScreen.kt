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
import com.pemmob.ulasbuku.data.model.UserHistoryItem
import com.pemmob.ulasbuku.ui.theme.PureWhite
import com.pemmob.ulasbuku.ui.theme.TextMuted
import com.pemmob.ulasbuku.ui.theme.TextPrimary
import com.pemmob.ulasbuku.ui.viewmodel.BookViewModel

/**
 * Halaman yang nampilin seluruh histori aktivitas user: review yang pernah ditulis
 * dan balasan yang pernah dikirim ke review orang lain.
 * List di-reverse supaya aktivitas paling baru muncul duluan.
 * Tiap item bisa diklik buat langsung lompat ke detail buku yang bersangkutan.
 */
@Composable
fun ReviewHistoryScreen(
    viewModel: BookViewModel,
    onBackClick: () -> Unit,
    onBookClick: (Book) -> Unit
) {
    val userHistory by viewModel.userHistory.collectAsState()

    Scaffold(
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
                    text = "Semua Histori Ulasan",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            if (userHistory.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada ulasan yang kamu tulis.",
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
                        items = userHistory.reversed(),
                        key = { item ->
                            when (item) {
                                is UserHistoryItem.ReviewItem -> "review_${item.review.id}"
                                is UserHistoryItem.ReplyItem -> "reply_${item.reply.id}"
                            }
                        }
                    ) { item ->
                        when (item) {
                            is UserHistoryItem.ReviewItem -> {
                                ProfileReviewCard(
                                    book = item.book,
                                    review = item.review,
                                    onClick = { onBookClick(item.book) }
                                )
                            }
                            is UserHistoryItem.ReplyItem -> {
                                ProfileReplyCard(
                                    book = item.book,
                                    review = item.originalReview,
                                    reply = item.reply,
                                    onClick = { onBookClick(item.book) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
