package com.pemmob.ulasbuku.ui.screens

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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.ulasbuku.data.model.Book
import com.pemmob.ulasbuku.data.model.Reply
import com.pemmob.ulasbuku.data.model.Review
import com.pemmob.ulasbuku.data.model.User
import com.pemmob.ulasbuku.data.model.UserHistoryItem
import com.pemmob.ulasbuku.ui.theme.BorderDark
import com.pemmob.ulasbuku.ui.theme.BorderSubtle
import com.pemmob.ulasbuku.ui.theme.DarkButton
import com.pemmob.ulasbuku.ui.theme.PureWhite
import com.pemmob.ulasbuku.ui.theme.SoftGray
import com.pemmob.ulasbuku.ui.theme.TextMuted
import com.pemmob.ulasbuku.ui.theme.TextPrimary
import com.pemmob.ulasbuku.ui.theme.TextSecondary
import com.pemmob.ulasbuku.ui.theme.*
import com.pemmob.ulasbuku.ui.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: BookViewModel,
    onBookClick: (Book) -> Unit,
    onSeeAllHistoryClick: () -> Unit,
    onSeeAllSavedBooksClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val userHistory by viewModel.userHistory.collectAsState()
    val bookmarkedBooks by viewModel.bookmarkedBooks.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = PureWhite,
        contentWindowInsets = WindowInsets(0),
        modifier = Modifier.then(if (showEditProfileDialog) Modifier.blur(12.dp) else Modifier),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profil Saya",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Keluar dari Akun",
                            tint = TextMuted,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PureWhite,
                    titleContentColor = TextPrimary
                ),
                windowInsets = WindowInsets(0)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {

            item { Spacer(Modifier.height(12.dp)) }

            // ── 2. PROFILE CARD (Identitas & Statistik Ringkas) ──────────────
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 6.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(18.dp)
                        )
                        .clip(RoundedCornerShape(18.dp)),
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
                                    .shadow(
                                        elevation = 4.dp,
                                        shape = CircleShape
                                    )
                                    .clip(CircleShape)
                                    .background(PastelYellowGradientStart),
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
                                        .clickable { showEditProfileDialog = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
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
                                text = "\"${currentUser?.bio ?: ""}\"",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                fontStyle = FontStyle.Italic,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            // Genre Favorit Badge
                            val favGenre = currentUser?.favoriteGenre?.takeIf { it.isNotBlank() } ?: "Sastra & Drama"
                            Spacer(Modifier.height(10.dp))
                            Surface(
                                color = SoftGray,
                                shape = RoundedCornerShape(50.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = "Genre Favorit",
                                        tint = VividBlue,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = favGenre,
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
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
                        text = "Semua",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onSeeAllHistoryClick() }
                    )
                }
            }

            // ── 4. HISTORI LIST (Menyamping / Horizontal) ────────────────
            item {
                if (userHistory.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada ulasan yang kamu tulis. Mulai ulas buku favoritmu!",
                            color = TextMuted,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(
                            items = userHistory.reversed().take(5),
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
                                        modifier = Modifier.width(260.dp),
                                        onClick = { onBookClick(item.book) }
                                    )
                                }
                                is UserHistoryItem.ReplyItem -> {
                                    ProfileReplyCard(
                                        book = item.book,
                                        review = item.originalReview,
                                        reply = item.reply,
                                        modifier = Modifier.width(260.dp),
                                        onClick = { onBookClick(item.book) }
                                    )
                                }
                            }
                        }
                    }
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
                        text = "Semua",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onSeeAllSavedBooksClick() }
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
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(
                            items = bookmarkedBooks.take(5),
                            key = { book -> book.id }
                        ) { book ->
                            BookCardVertical(book = book, onClick = { onBookClick(book) })
                        }
                    }
                }
            }
        }
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            currentUser = currentUser,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, username, bio, favGenre ->
                viewModel.updateProfile(name, username, bio, favGenre)
                showEditProfileDialog = false
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Keluar dari Akun", fontWeight = FontWeight.Black, fontSize = 18.sp) },
            text = { Text("Apakah kamu yakin ingin keluar dari akun ini?", fontSize = 14.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                        onLogoutClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text("Keluar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            },
            containerColor = PureWhite,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun StatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ProfileReviewCard(
    book: Book,
    review: Review,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 6.dp),
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(18.dp)
            )
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = PureWhite)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth()
                .defaultMinSize(minHeight = 85.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = book.title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = AmberStar,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${review.userRating}",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                text = review.comment,
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun ProfileReplyCard(
    book: Book,
    review: Review,
    reply: Reply,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 6.dp),
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(18.dp)
            )
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = PureWhite)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth()
                .defaultMinSize(minHeight = 85.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = book.title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Membalas",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "Membalas ulasan dari ${review.reviewerName}",
                color = TextSecondary,
                fontSize = 11.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = reply.replyText,
                color = TextPrimary,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun EditProfileDialog(
    currentUser: User?,
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
