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
import com.pemmob.ulasbuku.ui.theme.*
import com.pemmob.ulasbuku.ui.viewmodel.BookUiState
import com.pemmob.ulasbuku.ui.viewmodel.BookViewModel

@Composable
fun BookListScreen(
    viewModel: BookViewModel,
    onBookClick: (Book) -> Unit,
    onProfileClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredBooks by viewModel.filteredBooks.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val allBooks = when (val s = uiState) { is BookUiState.Success -> s.books; else -> emptyList() }
    val allCategories = when (val s = uiState) { is BookUiState.Success -> s.categories; else -> emptyList() }

    val popularBooks = remember(allBooks) { allBooks.sortedByDescending { it.rating }.take(10) }
    val collectionBooks = remember(allBooks) { allBooks.sortedBy { it.categoryId }.take(10) }
    val isFilterActive = searchQuery.isNotBlank() || selectedCategoryId != null

    // Sample authors list matching reference
    val sampleAuthors = remember {
        listOf(
            "Tere Liye" to "Penulis Fiksi",
            "Andrea Hirata" to "Penulis Sastra",
            "Leila Chudori" to "Penulis Drama",
            "Henry Manampiring" to "Penulis Non-Fiksi",
            "Dee Lestari" to "Penulis Novel"
        )
    }

    Scaffold(containerColor = PureWhite) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {

            // ── 1. TOP NAV & GREETING HEADER (Matching "Hello, Jenny" Reference) ─
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    // Action Bar Row (Menu Icon, Search, Profile)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextPrimary, modifier = Modifier.size(26.dp))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Search, contentDescription = "Cari", tint = TextPrimary, modifier = Modifier.size(24.dp))
                            }
                            Spacer(Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(PastelYellowGradientStart)
                                    .border(1.5.dp, BorderDark, CircleShape)
                                    .clickable { onProfileClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = "Profil", tint = TextPrimary, modifier = Modifier.size(22.dp))
                            }
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    // Hello Greeting Headline
                    Text(
                        text = "Hello, ${currentUser?.name?.split(" ")?.firstOrNull() ?: "Reader"}",
                        color = TextPrimary,
                        fontSize = 32.sp,
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

            // ── 2. SEARCH INPUT (If Active or Clicked) ──────────────────────
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    placeholder = { Text("Cari judul, penulis, atau genre...", color = TextMuted, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = TextPrimary) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, null, tint = TextSecondary)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(50.dp),
                    colors = ulasBukuTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 12.dp)
                )
            }

            // ── 3. CATEGORY CHIPS (Cute Pastel Chips) ───────────────────────
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        val sel = selectedCategoryId == null
                        FilterChip(
                            selected = sel,
                            onClick = { viewModel.onCategorySelect(null) },
                            label = { Text("Semua", fontSize = 12.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Medium) },
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
                    items(allCategories) { cat ->
                        val sel = selectedCategoryId == cat.id
                        FilterChip(
                            selected = sel,
                            onClick = { viewModel.onCategorySelect(cat.id) },
                            label = { Text(cat.name, fontSize = 12.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Medium) },
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

            // ── SEARCH RESULTS ──────────────────────────────────────────────
            if (isFilterActive) {
                item {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${filteredBooks.size} buku ditemukan", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
                items(filteredBooks.chunked(2)) { rowBooks ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        rowBooks.forEach { book ->
                            CuteBookCard(book = book, onClick = { onBookClick(book) }, modifier = Modifier.weight(1f))
                        }
                        if (rowBooks.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            } else {

                // ── 4. TRENDING HERO BANNER (Matching Reference) ─────────────────
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                            .height(140.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(PastelBlueGradientStart, PastelPeachGradientStart)
                                )
                            )
                            .border(1.5.dp, BorderDark, RoundedCornerShape(24.dp))
                            .padding(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "TRENDING",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "${allBooks.size} Buku Pilihan Populer Hari Ini",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                            }

                            // White Pill Button "Explore"
                            Surface(
                                shape = RoundedCornerShape(50.dp),
                                color = PureWhite,
                                modifier = Modifier
                                    .border(1.5.dp, BorderDark, RoundedCornerShape(50.dp))
                                    .align(Alignment.End)
                            ) {
                                Text(
                                    text = "Explore",
                                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 7.dp),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }

                // ── 5. NEW & NOTEWORTHY SECTION (Matching Reference) ───────────
                item {
                    SectionHeaderRow(title = "New & Noteworthy")
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(popularBooks) { book ->
                            CuteBookCardLarge(book = book, onClick = { onBookClick(book) })
                        }
                    }
                }

                item { Spacer(Modifier.height(20.dp)) }

                // ── 6. AUTHORS SECTION (Matching Reference Author Avatars) ──────
                item {
                    SectionHeaderRow(title = "Authors")
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        itemsIndexed(sampleAuthors) { index, (authorName, category) ->
                            AuthorAvatarCard(
                                name = authorName,
                                subtitle = category,
                                bgColor = getAuthorAvatarBg(index)
                            )
                        }
                    }
                }

                item { Spacer(Modifier.height(20.dp)) }

                // ── 7. POPULAR COLLECTION ───────────────────────────────────────
                item {
                    SectionHeaderRow(title = "Paling Banyak Diulas")
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(collectionBooks) { book ->
                            CuteBookCardLarge(book = book, onClick = { onBookClick(book) })
                        }
                    }
                }
            }
        }
    }
}

// ── CUTE COMPOSABLE HELPERS ──────────────────────────────────────────────────

@Composable
private fun SectionHeaderRow(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = TextPrimary,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * Cute Rounded Square Book Card matching "New & Noteworthy" in reference
 */
@Composable
private fun CuteBookCardLarge(book: Book, onClick: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(getCategoryColor(book.categoryId))
                .border(1.5.dp, BorderDark, RoundedCornerShape(24.dp))
        ) {
            if (book.coverImg.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(book.coverImg).crossfade(true).build(),
                    contentDescription = book.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.AutoStories, null, tint = TextPrimary.copy(0.7f), modifier = Modifier.size(42.dp))
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
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = book.title,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = book.author,
            color = TextSecondary,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Cute Author Avatar Card matching "Authors" section in reference
 */
@Composable
private fun AuthorAvatarCard(name: String, subtitle: String, bgColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        Box(
            modifier = Modifier
                .size(95.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(bgColor)
                .border(1.5.dp, BorderDark, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = name,
                tint = TextPrimary.copy(alpha = 0.8f),
                modifier = Modifier.size(44.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = name,
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = subtitle,
            color = TextSecondary,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CuteBookCard(book: Book, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, BorderDark)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(getCategoryColor(book.categoryId))
            ) {
                if (book.coverImg.isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context).data(book.coverImg).crossfade(true).build(),
                        contentDescription = book.title, contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.AutoStories, null, tint = TextPrimary.copy(0.7f), modifier = Modifier.size(36.dp))
                    }
                }
            }
            Column(Modifier.padding(10.dp)) {
                Text(book.title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(book.author, color = TextSecondary, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}
