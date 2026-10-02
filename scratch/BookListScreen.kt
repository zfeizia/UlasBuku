package com.pemmob.ulasbuku.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.pemmob.ulasbuku.data.model.Category
import com.pemmob.ulasbuku.ui.theme.*
import com.pemmob.ulasbuku.ui.viewmodel.BookUiState
import com.pemmob.ulasbuku.ui.viewmodel.BookViewModel

// ─────────────────────────────────────────────────────────────────────────────
// HOME / BERANDA SCREEN
// Urutan: Greeting → Kategori → Buku Paling Banyak Diulas → Penulis → Genre Spesifik
// SEARCH BAR sudah DIHAPUS dari sini — pindah ke SearchScreen
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(
    viewModel: BookViewModel,
    onBookClick: (Book) -> Unit,
    onProfileClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val allBooks = remember(uiState) {
        (uiState as? BookUiState.Success)?.books ?: emptyList()
    }
    val allCategories = remember(uiState) {
        (uiState as? BookUiState.Success)?.categories ?: emptyList()
    }

    // ── Data sections ──────────────────────────────────────────────────────
    // "Buku Paling Banyak Diulas" = sortir berdasarkan jumlah reviews DESC
    val mostReviewedBooks = remember(allBooks) {
        allBooks.sortedByDescending { it.reviews.size }.take(10)
    }
    // Penulis unik dari dataset
    val sampleAuthors = remember {
        listOf(
            Triple("Tere Liye", "Penulis Fiksi", 0),
            Triple("Andrea Hirata", "Penulis Sastra", 1),
            Triple("Leila Chudori", "Penulis Drama", 2),
            Triple("Dee Lestari", "Penulis Novel", 3),
            Triple("Pramoedya", "Sastra Klasik", 0)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(PastelBlueGradientStart, PastelPurpleGradientStart)
                                    )
                                )
                                .border(1.5.dp, BorderDark, RoundedCornerShape(9.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoStories,
                                contentDescription = "Logo",
                                tint = TextPrimary,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "UlasBuku",
                            color = TextPrimary,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.3).sp
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

            // ─────────────────────────────────────────────────────────────────
            // 1. GREETING HEADER
            // ─────────────────────────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Halo, ${currentUser?.name?.split(" ")?.firstOrNull() ?: "Pembaca"}",
                        color = TextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Buku apa yang ingin kamu baca & ulas hari ini?",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // ─────────────────────────────────────────────────────────────────
            // 2. PILIHAN KATEGORI (Filter Chips Langsung tanpa Header "Kategori")
            // ─────────────────────────────────────────────────────────────────
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item(key = "cat_all") {
                        val sel = selectedCategoryId == null
                        FilterChip(
                            selected = sel,
                            onClick = { viewModel.onCategorySelect(null) },
                            label = {
                                Text(
                                    "Semua",
                                    fontSize = 12.sp,
                                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PastelBlueGradientStart,
                                selectedLabelColor = TextPrimary,
                                containerColor = SoftGray,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true, selected = sel,
                                selectedBorderColor = BorderDark, borderColor = BorderSubtle
                            ),
                            shape = RoundedCornerShape(50.dp)
                        )
                    }
                    items(
                        items = allCategories,
                        key = { cat -> "cat_${cat.id}" }
                    ) { cat ->
                        val sel = selectedCategoryId == cat.id
                        FilterChip(
                            selected = sel,
                            onClick = { viewModel.onCategorySelect(cat.id) },
                            label = {
                                Text(
                                    cat.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PastelBlueGradientStart,
                                selectedLabelColor = TextPrimary,
                                containerColor = SoftGray,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true, selected = sel,
                                selectedBorderColor = BorderDark, borderColor = BorderSubtle
                            ),
                            shape = RoundedCornerShape(50.dp)
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(10.dp)) }

            // ─────────────────────────────────────────────────────────────────
            // 3. SECTION: BUKU PALING BANYAK DIULAS
            // ─────────────────────────────────────────────────────────────────
            item {
                HomeSectionHeader(title = "Buku Paling Banyak Diulas")
            }

            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(264.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = mostReviewedBooks,
                        key = { book -> "reviewed_${book.id}" }
                    ) { book ->
                        BookCardVertical(book = book, onClick = { onBookClick(book) })
                    }
                }
            }

            item { Spacer(Modifier.height(14.dp)) }

            // ─────────────────────────────────────────────────────────────────
            // 4. SECTION: PENULIS
            // ─────────────────────────────────────────────────────────────────
            item {
                HomeSectionHeader(title = "Penulis Populer")
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    itemsIndexed(
                        items = sampleAuthors,
                        key = { idx, _ -> "author_$idx" }
                    ) { _, (name, subtitle, colorIdx) ->
                        AuthorAvatarCard(
                            name = name,
                            subtitle = subtitle,
                            bgColor = getAuthorAvatarBg(colorIdx)
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(14.dp)) }

            // ─────────────────────────────────────────────────────────────────
            // 5. SECTION: KATEGORI REKOMENDASI (Netflix Style)
            // ─────────────────────────────────────────────────────────────────
            val categoriesToDisplay = if (selectedCategoryId != null) {
                allCategories.filter { it.id == selectedCategoryId }
            } else {
                allCategories
            }

            categoriesToDisplay.forEach { cat ->
                val booksForCat = allBooks.filter { it.categoryId == cat.id }
                if (booksForCat.isNotEmpty()) {
                    item(key = "header_cat_${cat.id}") {
                        HomeSectionHeader(
                            title = getCuratedCategoryTitle(cat.name)
                        )
                    }

                    item(key = "row_cat_${cat.id}") {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(264.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(
                                items = booksForCat,
                                key = { book -> "cat_${cat.id}_${book.id}" }
                            ) { book ->
                                BookCardVertical(book = book, onClick = { onBookClick(book) })
                            }
                        }
                    }

                    item(key = "spacer_cat_${cat.id}") {
                        Spacer(Modifier.height(14.dp))
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SHARED COMPOSABLES (dapat dipakai screen lain)
// ─────────────────────────────────────────────────────────────────────────────

fun getCuratedCategoryTitle(categoryName: String): String = when (categoryName) {
    "Fantasi & Petualangan" -> "Fantasi & Petualangan Pilihan"
    "Misteri & Thriller" -> "Misteri & Cerita Penuh Teka-Teki"
    "Inspiratif & Humaniora" -> "Kisah Inspiratif & Humaniora"
    "Sastra & Drama" -> "Karya Sastra & Drama Terbaik"
    "Non-Fiksi & Pengembangan Diri" -> "Pengembangan Diri & Wawasan"
    else -> categoryName
}

@Composable
fun HomeSectionHeader(title: String, onSeeAll: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black
        )
        if (onSeeAll != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onSeeAll() }
            )
        }
    }
}

/**
 * Kartu buku vertikal (cover + judul + penulis + rating pill)
 * Dipakai di Home sections dan Search results
 */
@Composable
fun BookCardVertical(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.width(150.dp)
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .wrapContentHeight(Alignment.Top)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.67f)
                .clip(RoundedCornerShape(18.dp))
                .background(getCategoryColor(book.categoryId))
                .border(1.5.dp, BorderDark, RoundedCornerShape(18.dp))
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
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.AutoStories,
                        null,
                        tint = TextPrimary.copy(0.7f),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            // Rating pill
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = PureWhite,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .border(1.dp, BorderDark, RoundedCornerShape(50.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, null, tint = AmberStar, modifier = Modifier.size(11.dp))
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = String.format("%.1f", book.rating),
                        color = TextPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            // Jumlah ulasan pill (bottom)
            if (book.reviews.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = DarkButton.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "${book.reviews.size} ulasan",
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = book.title,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = book.author,
            color = TextSecondary,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun AuthorAvatarCard(name: String, subtitle: String, bgColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp)
    ) {
        Box(
            modifier = Modifier
                .size(82.dp)
                .clip(CircleShape)
                .background(bgColor)
                .border(1.5.dp, BorderDark, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.firstOrNull()?.uppercase() ?: "?",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = name,
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = subtitle,
            color = TextSecondary,
            fontSize = 9.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
