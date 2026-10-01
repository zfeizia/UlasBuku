package com.pemmob.ulasbuku.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.ulasbuku.ui.theme.*
import com.pemmob.ulasbuku.ui.viewmodel.BookUiState
import com.pemmob.ulasbuku.ui.viewmodel.BookViewModel

// ─────────────────────────────────────────────────────────────────────────────
// ADD REVIEW SCREEN
// Dipanggil dari BookDetailScreen dengan bookId yang sudah dipilih.
// Field: Rating Bintang 1-5 (klik), OutlinedTextField ulasan, Tombol Submit
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewScreen(
    viewModel: BookViewModel,
    /** ID buku yang akan diulas. Jika null, tampilkan dropdown pemilih buku. **/
    bookId: Int? = null,
    onBackClick: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val allBooks = remember(uiState) {
        (uiState as? BookUiState.Success)?.books ?: emptyList()
    }

    // Tentukan buku target berdasarkan bookId
    val targetBook = remember(bookId, allBooks) {
        if (bookId != null) allBooks.find { it.id == bookId } else allBooks.firstOrNull()
    }

    // ── State Form ──────────────────────────────────────────────────────────
    // rememberSaveable agar state bertahan saat rotasi layar
    var selectedBookId by rememberSaveable { mutableIntStateOf(bookId ?: allBooks.firstOrNull()?.id ?: -1) }
    var userRating by rememberSaveable { mutableIntStateOf(5) }        // 1-5, integer klik
    var commentText by rememberSaveable { mutableStateOf("") }
    var isAnonymous by rememberSaveable { mutableStateOf(false) }
    var commentError by rememberSaveable { mutableStateOf(false) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val selectedBook = remember(selectedBookId, allBooks) {
        allBooks.find { it.id == selectedBookId }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tulis Ulasan",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PureWhite,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = PureWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {

            // ── Info Buku ─────────────────────────────────────────────────
            if (bookId != null && targetBook != null) {
                // Buku sudah dipilih — tampilkan info saja
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SoftGray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Mengulas Buku",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = targetBook.title,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = targetBook.author,
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                // Tidak ada bookId — tampilkan dropdown pemilih buku
                Text(
                    "Pilih Buku *",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(6.dp))
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedBook?.title ?: "Pilih buku...",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ulasBukuTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                        modifier = Modifier
                            .background(PureWhite)
                            .height(260.dp)
                    ) {
                        allBooks.forEach { book ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(book.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(book.author, fontSize = 11.sp, color = TextSecondary)
                                    }
                                },
                                onClick = {
                                    selectedBookId = book.id
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Rating Bintang 1-5 (klik) ─────────────────────────────────
            Text(
                text = "Berikan Rating",
                color = TextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )
            Text(
                text = "Ketuk bintang untuk memberi penilaian",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                (1..5).forEach { star ->
                    val isFilled = star <= userRating
                    Icon(
                        imageVector = if (isFilled) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Bintang $star",
                        tint = if (isFilled) AmberStar else TextMuted,
                        modifier = Modifier
                            .size(44.dp)
                            .clickable { userRating = star }
                            .padding(4.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "$userRating/5",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Teks Ulasan ───────────────────────────────────────────────
            Text(
                text = "Tulis Ulasanmu *",
                color = TextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = commentText,
                onValueChange = {
                    commentText = it
                    if (it.isNotBlank()) commentError = false
                },
                placeholder = {
                    Text(
                        "Ceritakan pendapatmu tentang buku ini...\n(alur, karakter, pesan moral, gaya bahasa)",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                },
                isError = commentError,
                supportingText = {
                    if (commentError) {
                        Text("Ulasan tidak boleh kosong", color = CoralRed, fontSize = 12.sp)
                    }
                },
                minLines = 5,
                maxLines = 10,
                shape = RoundedCornerShape(16.dp),
                colors = ulasBukuTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))



            Spacer(Modifier.height(24.dp))

            // ── Tombol Submit ─────────────────────────────────────────────
            Button(
                onClick = {
                    if (commentText.isBlank()) {
                        commentError = true
                        return@Button
                    }
                    val bookToReview = selectedBook
                    if (bookToReview == null) {
                        Toast.makeText(context, "Pilih buku terlebih dahulu", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val success = viewModel.addReview(
                        bookId = bookToReview.id,
                        reviewerName = currentUser?.name ?: "Pembaca",
                        reviewerEmail = currentUser?.email ?: "",
                        rating = userRating.toFloat(),
                        comment = commentText,
                        isAnonymous = isAnonymous
                    )
                    if (success) {
                        Toast.makeText(context, "Ulasan berhasil dikirim! 🎉", Toast.LENGTH_SHORT).show()
                        onSubmitSuccess()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkButton,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .border(1.5.dp, BorderDark, RoundedCornerShape(50.dp))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Kirim Ulasan", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}



