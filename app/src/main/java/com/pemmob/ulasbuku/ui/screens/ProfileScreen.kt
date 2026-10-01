package com.pemmob.ulasbuku.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.ulasbuku.data.model.Book
import com.pemmob.ulasbuku.data.model.Review
import com.pemmob.ulasbuku.ui.theme.*
import com.pemmob.ulasbuku.ui.viewmodel.BookViewModel

@Composable
fun ProfileScreen(
    viewModel: BookViewModel,
    onBookClick: (Book) -> Unit,
    onLogoutClick: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val userReviews by viewModel.userReviews.collectAsState()
    val bookmarkedBooks by viewModel.bookmarkedBooks.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Count completed books
    val completedCount = remember(currentUser) {
        currentUser?.readingStatusMap?.values?.count { it == "COMPLETED" } ?: 0
    }

    Scaffold(containerColor = PureWhite) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {

            // ── 1. HEADER TITLE ──────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Profil Saya",
                        color = TextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Keluar dari Akun",
                            tint = TextMuted,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // ── 2. PROFILE CARD (Identitas & Statistik Ringkas) ──────────────
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.5.dp, BorderDark, RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = PureWhite)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Avatar Initial
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(PastelYellowGradientStart)
                                .border(2.dp, BorderDark, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUser?.name?.firstOrNull()?.uppercase() ?: "?",
                                color = TextPrimary,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = currentUser?.name ?: "Pengguna",
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            // Edit Profile Button Icon
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(SoftGray)
                                    .border(1.dp, BorderSubtle, CircleShape)
                                    .clickable { showEditProfileDialog = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit Profil",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }

                        // Username (@username)
                        Text(
                            text = currentUser?.username ?: "@feizia_reads",
                            color = VividBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(6.dp))

                        // Bio Ringkas
                        Text(
                            text = "\"${currentUser?.bio}\"",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Italic,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )


                    }

                }
            }
        }

        item { Spacer(Modifier.height(20.dp)) }

            // ── 3. HISTORI ULASAN SAYA TITLE ─────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Histori Ulasan Saya",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "${userReviews.size} Ulasan",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ── 4. HISTORI LIST ──────────────────────────────────────────
            if (userReviews.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada ulasan yang kamu tulis. Mulai ulas buku favoritmu!",
                            color = TextMuted,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(
                    items = userReviews,
                    key = { (_, review) -> "review_${review.id}" }
                ) { (book, review) ->
                    ProfileReviewCard(book = book, review = review, onClick = { onBookClick(book) })
                }
            }

            item { Spacer(Modifier.height(20.dp)) }

            // ── 5. BUKU DISIMPAN SECTION ──────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Buku Disimpan",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "${bookmarkedBooks.size} buku",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                if (bookmarkedBooks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada buku yang disimpan. Simpan buku favoritmu!",
                            color = TextMuted,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(264.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(
                            items = bookmarkedBooks,
                            key = { book -> "saved_${book.id}" }
                        ) { book ->
                            BookCardVertical(
                                book = book,
                                onClick = { onBookClick(book) }
                            )
                        }
                    }
                }
            }

        }
    }

    // ── EDIT PROFILE DIALOG ──────────────────────────────────────────────────
    if (showEditProfileDialog) {
        EditProfileDialog(
            currentUser = currentUser,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, username, bio, favGenre ->
                if (viewModel.updateProfile(name, username, bio, favGenre)) {
                    Toast.makeText(context, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                }
                showEditProfileDialog = false
            }
        )
    }

    // ── LOGOUT CONFIRMATION DIALOG ───────────────────────────────────────────
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = PureWhite,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text("Keluar dari Akun", fontWeight = FontWeight.Black, color = TextPrimary)
            },
            text = {
                Text("Kamu yakin mau keluar dari akun ini?", color = TextSecondary, fontSize = 14.sp)
            },
            confirmButton = {
                Button(
                    onClick = { showLogoutDialog = false; onLogoutClick() },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text("Keluar", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun StatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
        Text(label, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ProfileReviewCard(book: Book, review: Review, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(1.5.dp, BorderDark, RoundedCornerShape(18.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = PureWhite)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(book.title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = AmberStar, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(2.dp))
                    Text(String.format("%.1f", review.userRating), color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text(review.comment, color = TextSecondary, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun EditProfileDialog(
    currentUser: com.pemmob.ulasbuku.data.model.User?,
    onDismiss: () -> Unit,
    onSave: (name: String, username: String, bio: String, favGenre: String) -> Unit
) {
    var name by remember { mutableStateOf(currentUser?.name ?: "") }
    var username by remember { mutableStateOf(currentUser?.username ?: "@feizia_reads") }
    var bio by remember { mutableStateOf(currentUser?.bio ?: "") }
    var favoriteGenre by remember { mutableStateOf(currentUser?.favoriteGenre ?: "Sastra & Drama") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profil Pengguna", fontWeight = FontWeight.Black, fontSize = 18.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Tampilan") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = ulasBukuTextFieldColors()
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username (@username)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = ulasBukuTextFieldColors()
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio Ringkas") },
                    maxLines = 2,
                    shape = RoundedCornerShape(12.dp),
                    colors = ulasBukuTextFieldColors()
                )
                OutlinedTextField(
                    value = favoriteGenre,
                    onValueChange = { favoriteGenre = it },
                    label = { Text("Genre Favorit") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = ulasBukuTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, username, bio, favoriteGenre) },
                colors = ButtonDefaults.buttonColors(containerColor = DarkButton),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text("Simpan Perubahan", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary)
            }
        },
        containerColor = PureWhite,
        shape = RoundedCornerShape(24.dp)
    )
}
