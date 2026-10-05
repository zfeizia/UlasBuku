package com.pemmob.ulasbuku.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pemmob.ulasbuku.data.model.Book
import com.pemmob.ulasbuku.ui.theme.BorderSubtle
import com.pemmob.ulasbuku.ui.theme.DarkButton
import com.pemmob.ulasbuku.ui.theme.PureWhite
import com.pemmob.ulasbuku.ui.theme.SoftGray
import com.pemmob.ulasbuku.ui.theme.TextMuted
import com.pemmob.ulasbuku.ui.theme.TextPrimary
import com.pemmob.ulasbuku.ui.theme.TextSecondary
import com.pemmob.ulasbuku.ui.theme.*
import com.pemmob.ulasbuku.ui.viewmodel.BookUiState
import com.pemmob.ulasbuku.ui.viewmodel.BookViewModel
import com.pemmob.ulasbuku.ui.viewmodel.SearchUiState

/**
 * Halaman pencarian buku.
 * Punya dua state: Idle (nampilin rekomendasi berdasarkan jumlah review terbanyak)
 * dan Result (nampilin hasil filter berdasarkan query yang diketik user).
 * Query pencarian bisa match judul, penulis, maupun ISBN buku.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: BookViewModel,
    onBookClick: (Book) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchUiState by viewModel.searchUiState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val recommendedBooks = remember(uiState) {
        val books = (uiState as? BookUiState.Success)?.books ?: emptyList()
        books.sortedByDescending { it.reviews.size }.take(20)
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.clearSearch() }
    }

    Scaffold(
        containerColor = PureWhite,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cari",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PureWhite,
                    titleContentColor = TextPrimary
                ),
                windowInsets = WindowInsets(0)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PureWhite)
                .padding(innerPadding)
        ) {

        // ── Search bar ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PureWhite)
                .padding(start = 16.dp, end = 16.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = {
                    Text(
                        "Cari judul, penulis, atau ISBN...",
                        color = TextMuted,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        null,
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.clearSearch() }) {
                            Icon(
                                Icons.Default.Close,
                                null,
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(50.dp),
                colors = ulasBukuTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)

        // ── Konten ────────────────────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            when (val state = searchUiState) {
                // IDLE: Rekomendasi Untukmu
                is SearchUiState.Idle -> {
                    item {
                        Text(
                            text = "Rekomendasi Untukmu",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(
                                start = 20.dp, end = 20.dp,
                                top = 16.dp, bottom = 4.dp
                            )
                        )
                    }
                    items(
                        items = recommendedBooks,
                        key = { book -> "rec_${book.id}" }
                    ) { book ->
                        BookListRow(book = book, onClick = { onBookClick(book) })
                    }
                }

                // RESULT: Hasil Pencarian
                is SearchUiState.Result -> {
                    val results = state.books
                    item {
                        Text(
                            text = if (results.isEmpty())
                                "Tidak ditemukan hasil untuk \"$searchQuery\""
                            else
                                "${results.size} hasil untuk \"$searchQuery\"",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                        )
                    }
                    if (results.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 56.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.Search,
                                        null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        text = "Coba kata kunci lain",
                                        color = TextMuted,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    } else {
                        items(
                            items = results,
                            key = { book -> "res_${book.id}" }
                        ) { book ->
                            BookListRow(book = book, onClick = { onBookClick(book) })
                        }
                    }
                }
            }
        }
    }
}
}

// ─────────────────────────────────────────────────────────────────────────────
// BOOK LIST ROW — cover kiri, judul + penulis + rating kanan
// ─────────────────────────────────────────────────────────────────────────────
/**
 * Komponen reusable buat nampilin satu buku dalam bentuk baris (list row).
 * Layout: cover buku di kiri, judul + penulis + rating di kanan.
 * Cover dibuat dengan efek layering 3 box buat kesan ketebalan buku yang realistis.
 */
@Composable
fun BookListRow(book: Book, onClick: () -> Unit) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val bookShape = RoundedCornerShape(6.dp)
        Box(
            modifier = Modifier
                .width(72.dp)
                .height(100.dp)
        ) {
            // Pages stack (ketebalan buku) — offset kanan-bawah
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 3.dp, top = 3.dp)
                    .shadow(elevation = 0.dp, shape = bookShape)
                    .clip(bookShape)
                    .background(Color(0xFFE0DDD8))
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 1.5.dp, top = 1.5.dp)
                    .shadow(elevation = 0.dp, shape = bookShape)
                    .clip(bookShape)
                    .background(Color(0xFFEDEAE5))
            )
            // Cover utama dengan drop shadow eksternal
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(
                        elevation = 8.dp,
                        shape = bookShape
                    )
                    .clip(bookShape)
                    .background(getCategoryColor(book.categoryId)),
                contentAlignment = Alignment.Center
            ) {
                if (book.displayCoverImg.isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(book.displayCoverImg)
                            .crossfade(true)
                            .build(),
                        contentDescription = book.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Default.AutoStories,
                        null,
                        tint = TextPrimary.copy(0.55f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = book.title,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = book.author,
                color = TextSecondary,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    null,
                    tint = AmberStar,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(Modifier.width(3.dp))
                Text(
                    text = String.format("%.1f", book.rating),
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                if (book.reviews.isNotEmpty()) {
                    Text(
                        text = "  \u00b7  ${book.reviews.size} ulasan",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }

    HorizontalDivider(
        modifier = Modifier.padding(start = 106.dp, end = 20.dp),
        thickness = 0.5.dp,
        color = BorderSubtle
    )
}
