package com.pemmob.ulasbuku.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.ulasbuku.data.model.Book
import com.pemmob.ulasbuku.ui.theme.TextPrimary
import com.pemmob.ulasbuku.ui.theme.TextSecondary
import com.pemmob.ulasbuku.ui.viewmodel.BookUiState
import com.pemmob.ulasbuku.ui.viewmodel.BookViewModel

/**
 * Halaman yang nampilin semua buku dari penulis tertentu.
 * Data buku di-filter dari semua buku yang ada, nyari yang nama penulisnya cocok.
 * Ditampilkan dalam grid 2 kolom pake chunked + LazyColumn biar performanya bagus.
 */
@Composable
fun AuthorBooksScreen(
    authorName: String,
    viewModel: BookViewModel,
    onBackClick: () -> Unit,
    onBookClick: (Book) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val allBooks = remember(uiState) {
        (uiState as? BookUiState.Success)?.books ?: emptyList()
    }
    val authorBooks = allBooks.filter { it.author.contains(authorName, ignoreCase = true) }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(start = 12.dp, end = 24.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Karya oleh",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = authorName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                }
            }

            // Grid Content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val chunkedBooks = authorBooks.chunked(2)
                items(chunkedBooks.size, key = { it }) { index ->
                    val rowBooks = chunkedBooks[index]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowBooks.forEach { book ->
                            BookCardVertical(
                                book = book,
                                onClick = { onBookClick(book) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowBooks.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
