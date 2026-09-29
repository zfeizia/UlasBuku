package com.pemmob.ulasbuku.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.pemmob.ulasbuku.ui.viewmodel.BookViewModel

/**
 * Saved / Rak Buku Screen — Pengelompokan Rak Buku & Akses Cepat
 * Tabs: Ingin Dibaca | Sedang Dibaca | Selesai Dibaca
 */
@Composable
fun SavedScreen(
    viewModel: BookViewModel,
    onBookClick: (Book) -> Unit
) {
    val bookmarkedBooks by viewModel.bookmarkedBooks.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var selectedTab by remember { mutableStateOf("WANT_TO_READ") } // WANT_TO_READ, READING, COMPLETED

    val wantToReadBooks = remember(bookmarkedBooks, currentUser) {
        bookmarkedBooks.filter { (currentUser?.readingStatusMap?.get(it.id) ?: "WANT_TO_READ") == "WANT_TO_READ" }
    }
    val readingBooks = remember(bookmarkedBooks, currentUser) {
        bookmarkedBooks.filter { currentUser?.readingStatusMap?.get(it.id) == "READING" }
    }
    val completedBooks = remember(bookmarkedBooks, currentUser) {
        bookmarkedBooks.filter { currentUser?.readingStatusMap?.get(it.id) == "COMPLETED" }
    }

    val currentList = when (selectedTab) {
        "READING" -> readingBooks
        "COMPLETED" -> completedBooks
        else -> wantToReadBooks
    }

    Scaffold(containerColor = PureWhite) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Header Title
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Rak Buku Saya 🔖",
                        color = TextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Pantau koleksi impian & riwayat bacaan pribadi",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── RAK BUKU TABS ───────────────────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RakTabPill(
                            title = "Ingin Dibaca (${wantToReadBooks.size})",
                            isSelected = selectedTab == "WANT_TO_READ",
                            onClick = { selectedTab = "WANT_TO_READ" },
                            modifier = Modifier.weight(1f)
                        )
                        RakTabPill(
                            title = "Sedang Dibaca (${readingBooks.size})",
                            isSelected = selectedTab == "READING",
                            onClick = { selectedTab = "READING" },
                            modifier = Modifier.weight(1f)
                        )
                        RakTabPill(
                            title = "Selesai (${completedBooks.size})",
                            isSelected = selectedTab == "COMPLETED",
                            onClick = { selectedTab = "COMPLETED" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // List Content
            if (currentList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AutoStories, null, tint = TextMuted, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = when (selectedTab) {
                                    "READING" -> "Belum ada buku yang sedang dibaca."
                                    "COMPLETED" -> "Belum ada buku yang selesai dibaca."
                                    else -> "Belum ada buku di daftar impian."
                                },
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Simpan buku dari katalog untuk menambahkannya ke rak!",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                items(currentList) { book ->
                    SavedBookCard(
                        book = book,
                        currentStatus = currentUser?.readingStatusMap?.get(book.id) ?: "WANT_TO_READ",
                        onClick = { onBookClick(book) },
                        onStatusChange = { newStatus -> viewModel.setReadingStatus(book.id, newStatus) },
                        onRemoveBookmark = { viewModel.toggleBookmark(book.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RakTabPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50.dp),
        color = if (isSelected) DarkButton else SoftGray,
        modifier = modifier
            .height(42.dp)
            .border(1.dp, if (isSelected) BorderDark else BorderSubtle, RoundedCornerShape(50.dp))
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 4.dp)) {
            Text(
                text = title,
                color = if (isSelected) Color.White else TextSecondary,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SavedBookCard(
    book: Book,
    currentStatus: String,
    onClick: () -> Unit,
    onStatusChange: (String) -> Unit,
    onRemoveBookmark: () -> Unit
) {
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.5.dp, BorderDark, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = PureWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cover
            Box(
                modifier = Modifier
                    .size(width = 65.dp, height = 90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(getCategoryColor(book.categoryId))
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
                        Icon(Icons.Default.AutoStories, null, tint = TextPrimary.copy(0.7f), modifier = Modifier.size(24.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Information & Status selector
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.author,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = AmberStar, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = String.format("%.1f", book.rating),
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Reading Status Selector Chip
                Box {
                    Surface(
                        onClick = { menuExpanded = true },
                        shape = RoundedCornerShape(50.dp),
                        color = when (currentStatus) {
                            "READING" -> PastelPeachGradientStart
                            "COMPLETED" -> PastelGreenGradientStart
                            else -> PastelBlueGradientStart
                        },
                        modifier = Modifier.border(1.dp, BorderDark, RoundedCornerShape(50.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (currentStatus) {
                                    "READING" -> "📖 Sedang Dibaca"
                                    "COMPLETED" -> "✅ Selesai Dibaca"
                                    else -> "📌 Ingin Dibaca"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("▾", fontSize = 10.sp, color = TextPrimary)
                        }
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(PureWhite)
                    ) {
                        DropdownMenuItem(
                            text = { Text("📌 Ingin Dibaca", fontSize = 12.sp) },
                            onClick = { onStatusChange("WANT_TO_READ"); menuExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("📖 Sedang Dibaca", fontSize = 12.sp) },
                            onClick = { onStatusChange("READING"); menuExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("✅ Selesai Dibaca", fontSize = 12.sp) },
                            onClick = { onStatusChange("COMPLETED"); menuExpanded = false }
                        )
                    }
                }
            }

            // Remove Button
            IconButton(onClick = onRemoveBookmark) {
                Icon(Icons.Default.BookmarkRemove, "Hapus", tint = CoralRed)
            }
        }
    }
}
