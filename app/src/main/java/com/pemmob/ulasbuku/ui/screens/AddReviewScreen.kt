package com.pemmob.ulasbuku.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.ulasbuku.data.model.Book
import com.pemmob.ulasbuku.ui.theme.*
import com.pemmob.ulasbuku.ui.viewmodel.BookUiState
import com.pemmob.ulasbuku.ui.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewScreen(
    viewModel: BookViewModel,
    initialSelectedBook: Book? = null,
    onBackClick: (() -> Unit)? = null,
    onSubmitSuccess: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val booksList = when (val state = uiState) {
        is BookUiState.Success -> state.books
        else -> emptyList()
    }

    var selectedBook by remember {
        mutableStateOf(initialSelectedBook ?: booksList.firstOrNull())
    }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    var reviewerName by remember(currentUser) {
        mutableStateOf(currentUser?.name ?: "")
    }
    var reviewerEmail by remember(currentUser) {
        mutableStateOf(currentUser?.email ?: "")
    }
    var userRating by remember { mutableFloatStateOf(5.0f) }
    var commentText by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var commentError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tulis Ulasan Buku",
                        color = Buttermilk,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = Buttermilk
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = OldBurgundy,
                    titleContentColor = Buttermilk
                )
            )
        },
        containerColor = PastelBlue
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Buttermilk),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Bagikan Refleksi Membaca Anda",
                        color = OldBurgundy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Ulasan yang jujur dan mendalam membantu pembaca lain menemukan karya terbaik.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Dropdown Buku
                    Text(
                        text = "Pilih Buku yang Diulas *",
                        color = OldBurgundy,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    ExposedDropdownMenuBox(
                        expanded = isDropdownExpanded,
                        onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedBook?.title ?: "Pilih buku dari katalog...",
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
                                .background(ButtermilkLight)
                                .height(280.dp)
                        ) {
                            booksList.forEach { book ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = book.title,
                                                fontWeight = FontWeight.Bold,
                                                color = OldBurgundy,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = book.author,
                                                fontSize = 11.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedBook = book
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Input Nama Pengulas
                    Text(
                        text = "Nama Peresensi *",
                        color = OldBurgundy,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = reviewerName,
                        onValueChange = {
                            reviewerName = it
                            if (it.isNotBlank()) nameError = false
                        },
                        placeholder = { Text("Nama lengkap Anda", color = TextMuted, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = OldBurgundy)
                        },
                        isError = nameError,
                        supportingText = {
                            if (nameError) Text("Nama wajib diisi", color = CoralRed)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = ulasBukuTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Input Email
                    Text(
                        text = "Email *",
                        color = OldBurgundy,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = reviewerEmail,
                        onValueChange = {
                            reviewerEmail = it
                            if (it.isNotBlank()) emailError = false
                        },
                        placeholder = { Text("Email aktif Anda", color = TextMuted, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = OldBurgundy)
                        },
                        isError = emailError,
                        supportingText = {
                            if (emailError) Text("Email wajib diisi", color = CoralRed)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = ulasBukuTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Rating Slider Interaktif
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Penilaian Bintang:",
                            color = OldBurgundy,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = AmberStar,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format("%.1f / 5.0", userRating),
                                color = OldBurgundy,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Slider(
                        value = userRating,
                        onValueChange = { userRating = (Math.round(it * 10f) / 10f).coerceIn(1.0f, 5.0f) },
                        valueRange = 1.0f..5.0f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = OldBurgundy,
                            activeTrackColor = OldBurgundy,
                            inactiveTrackColor = ButtermilkDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Input Teks Ulasan
                    Text(
                        text = "Teks Ulasan & Ulasan Pribadi *",
                        color = OldBurgundy,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = {
                            commentText = it
                            if (it.isNotBlank()) commentError = false
                        },
                        placeholder = {
                            Text(
                                text = "Tuliskan ulasan Anda mengenai pesan moral, alur cerita, karakter, atau gaya bahasa buku ini...",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        },
                        isError = commentError,
                        supportingText = {
                            if (commentError) Text("Ulasan tidak boleh kosong", color = CoralRed)
                        },
                        minLines = 4,
                        maxLines = 8,
                        shape = RoundedCornerShape(12.dp),
                        colors = ulasBukuTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Tombol Kirim Ulasan
                    Button(
                        onClick = {
                            val bookToReview = selectedBook
                            if (bookToReview == null) {
                                Toast.makeText(context, "Silakan pilih buku terlebih dahulu", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (reviewerName.isBlank()) {
                                nameError = true
                                return@Button
                            }
                            if (reviewerEmail.isBlank() || !reviewerEmail.contains("@")) {
                                emailError = true
                                return@Button
                            }
                            if (commentText.isBlank()) {
                                commentError = true
                                return@Button
                            }

                            val success = viewModel.addReview(
                                bookId = bookToReview.id,
                                reviewerName = reviewerName,
                                reviewerEmail = reviewerEmail,
                                rating = userRating,
                                comment = commentText
                            )

                            if (success) {
                                Toast.makeText(context, "Ulasan berhasil dikirimkan!", Toast.LENGTH_SHORT).show()
                                commentText = ""
                                onSubmitSuccess()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OldBurgundy,
                            contentColor = Buttermilk
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Kirim",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kirim Ulasan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
