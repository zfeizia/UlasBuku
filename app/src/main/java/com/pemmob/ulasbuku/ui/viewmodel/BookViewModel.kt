package com.pemmob.ulasbuku.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pemmob.ulasbuku.data.model.Book
import com.pemmob.ulasbuku.data.model.Category
import com.pemmob.ulasbuku.data.model.Review
import com.pemmob.ulasbuku.data.model.User
import com.pemmob.ulasbuku.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Sealed interface buat representasi state halaman utama (loading, sukses, error)
// ViewModel bakal emit state ini, UI tinggal observe dan render sesuai state-nya
sealed interface BookUiState {
    data object Loading : BookUiState
    data class Success(
        val categories: List<Category>,
        val books: List<Book>
    ) : BookUiState
    data class Error(val message: String) : BookUiState
}

/** State untuk halaman Search **/
sealed interface SearchUiState {
    /** Belum mengetik apa-apa — tampilkan rekomendasi populer **/
    data object Idle : SearchUiState
    /** Sedang mengetik — tampilkan hasil filter **/
    data class Result(val books: List<Book>) : SearchUiState
}

class BookViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BookRepository(application.applicationContext)

    private val _uiState = MutableStateFlow<BookUiState>(BookUiState.Loading)
    val uiState: StateFlow<BookUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    val selectedCategoryId: StateFlow<Int?> = _selectedCategoryId.asStateFlow()

    private val _selectedBook = MutableStateFlow<Book?>(null)
    val selectedBook: StateFlow<Book?> = _selectedBook.asStateFlow()

    private val _userHistory = MutableStateFlow<List<com.pemmob.ulasbuku.data.model.UserHistoryItem>>(emptyList())
    val userHistory: StateFlow<List<com.pemmob.ulasbuku.data.model.UserHistoryItem>> = _userHistory.asStateFlow()

    private val _communityReviews = MutableStateFlow<List<Pair<Book, Review>>>(emptyList())
    val communityReviews: StateFlow<List<Pair<Book, Review>>> = _communityReviews.asStateFlow()

    // Filter buku real-time
    val filteredBooks: StateFlow<List<Book>> = combine(
        _uiState,
        _searchQuery,
        _selectedCategoryId
    ) { state, query, categoryId ->
        if (state is BookUiState.Success) {
            state.books.filter { book ->
                val matchesCategory = (categoryId == null || book.categoryId == categoryId)
                val matchesQuery = query.isBlank() ||
                        book.title.contains(query, ignoreCase = true) ||
                        book.author.contains(query, ignoreCase = true) ||
                        book.isbn.contains(query, ignoreCase = true)
                matchesCategory && matchesQuery
            }
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Buku yang disimpan / dibookmark oleh user
    val bookmarkedBooks: StateFlow<List<Book>> = combine(
        _uiState,
        _currentUser
    ) { state, user ->
        if (state is BookUiState.Success && user != null) {
            state.books.filter { user.bookmarkedBookIds.contains(it.id) }
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // init block dipanggil otomatis saat ViewModel pertama kali dibuat
    // langsung load data supaya UI langsung dapat data pas pertama dibuka
    init {
        loadBooks()
    }

    /**
     * Load data buku dan kategori dari repository.
     * Jalanin di coroutine scope ViewModel biar otomatis dibatalin kalau ViewModel-nya destroyed.
     * Kalau sukses, update state ke Success; kalau error, update ke Error.
     */
    fun loadBooks() {
        viewModelScope.launch {
            _uiState.value = BookUiState.Loading
            try {
                val (categories, books) = repository.loadInitialData()
                _uiState.value = BookUiState.Success(categories = categories, books = books)
                updateUserData()
            } catch (e: Exception) {
                _uiState.value = BookUiState.Error(e.localizedMessage ?: "Terjadi kesalahan saat memuat data")
            }
        }
    }

    // --- AUTENTIKASI ---

    /**
     * Login menggunakan email ATAU username.
     * [identifier] bisa berupa alamat email atau username (dengan/tanpa '@').
     */
    fun login(identifier: String, pass: String): Boolean {
        _authError.value = null
        if (identifier.isBlank() || pass.isBlank()) {
            _authError.value = "Identifier dan kata sandi wajib diisi."
            return false
        }
        val user = repository.login(identifier, pass)
        if (user != null) {
            _currentUser.value = user
            updateUserData()
            refreshDataState()
            return true
        } else {
            _authError.value = "Username/email atau kata sandi tidak cocok."
            return false
        }
    }

    /**
     * Register dengan username sebagai identitas unik.
     * [username] tidak boleh mengandung spasi (akan diganti '_').
     */
    fun register(name: String, username: String, email: String, pass: String): Boolean {
        _authError.value = null
        if (name.isBlank() || username.isBlank() || email.isBlank() || pass.isBlank()) {
            _authError.value = "Semua bidang wajib diisi."
            return false
        }
        if (!email.contains("@")) {
            _authError.value = "Format alamat email tidak valid."
            return false
        }
        if (pass.length < 6) {
            _authError.value = "Kata sandi minimal 6 karakter."
            return false
        }
        val user = repository.register(name, username, email, pass, "Semua Genre")
        if (user != null) {
            _currentUser.value = user
            updateUserData()
            refreshDataState()
            return true
        } else {
            _authError.value = "Username atau email sudah terdaftar."
            return false
        }
    }

    /**
     * Logout user: bersihkan currentUser, hapus histori dari state,
     * dan refresh data supaya status agree/bookmark ikut direset.
     */
    fun logout() {
        repository.logout()
        _currentUser.value = null
        _userHistory.value = emptyList()
        refreshDataState()
    }

    /**
     * Update profil user, terus sync currentUser dan refresh UI state.
     * Return true kalau berhasil, false kalau gagal (misalnya user belum login).
     */
    fun updateProfile(name: String, username: String, bio: String, favoriteGenre: String): Boolean {
        val success = repository.updateProfile(name, username, bio, favoriteGenre)
        if (success) {
            _currentUser.value = repository.getCurrentUser()
            refreshDataState()
        }
        return success
    }

    // Set status baca user untuk sebuah buku (fitur belum aktif, dipanggil tapi nggak ngapa-ngapain)
    fun setReadingStatus(bookId: Int, status: String) {
        repository.setReadingStatus(bookId, status)
        _currentUser.value = repository.getCurrentUser()?.copy()
        refreshDataState()
    }

    // Ambil status baca buku, selalu return NONE untuk saat ini
    fun getReadingStatus(bookId: Int): String {
        return repository.getReadingStatus(bookId)
    }

    // Hapus error autentikasi dari state supaya UI nggak terus-terusan munculin pesan error
    fun clearAuthError() {
        _authError.value = null
    }

    // --- BOOKMARK & FAVORIT ---
    /**
     * Toggle bookmark buku: kalau sudah disimpan, hapus; kalau belum, simpan.
     * Update currentUser setelah toggle supaya UI bookmark icon langsung berubah.
     */
    fun toggleBookmark(bookId: Int): Boolean {
        val result = repository.toggleBookmark(bookId)
        _currentUser.value = repository.getCurrentUser()?.copy()
        refreshDataState()
        return result
    }

    // Cek status bookmark sebuah buku dari currentUser
    fun isBookmarked(bookId: Int): Boolean {
        return repository.isBookmarked(bookId)
    }

    // --- PENCARIAN & KATEGORI ---

    /** SearchUiState untuk SearchScreen (terpisah dari Home filter) **/
    private val _searchUiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchUiState: StateFlow<SearchUiState> = _searchUiState.asStateFlow()

    /** Dipanggil dari SearchScreen setiap kali query berubah **/
    /**
     * Fungsi yang dipanggil setiap kali isi search bar berubah.
     * Update searchQuery dan kalkulasi SearchUiState secara langsung.
     * Kalau query kosong, balik ke Idle (tampil rekomendasi); kalau ada isi, tampil hasil pencarian.
     */
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        // Update SearchUiState
        val allBooks = (uiState.value as? BookUiState.Success)?.books ?: emptyList()
        _searchUiState.value = if (query.isBlank()) {
            SearchUiState.Idle
        } else {
            val results = allBooks.filter { book ->
                book.title.contains(query, ignoreCase = true) ||
                book.author.contains(query, ignoreCase = true) ||
                book.isbn.contains(query, ignoreCase = true) ||
                (book.categoryId.toString() == query)
            }
            SearchUiState.Result(results)
        }
    }

    // Reset pencarian ke kondisi awal (kosong dan Idle)
    fun clearSearch() {
        _searchQuery.value = ""
        _searchUiState.value = SearchUiState.Idle
    }

    // Toggle pilihan kategori — kalau klik kategori yang sama, deselect; kalau beda, select yang baru
    fun onCategorySelect(categoryId: Int?) {
        _selectedCategoryId.value = if (_selectedCategoryId.value == categoryId) null else categoryId
    }

    // Set buku yang dipilih (buat ditampilkan di halaman detail)
    fun selectBook(book: Book) {
        _selectedBook.value = repository.getBookById(book.id) ?: book
    }

    // Hapus buku yang dipilih pas user kembali dari halaman detail
    fun clearSelectedBook() {
        _selectedBook.value = null
    }

    // --- INTERAKSI ULASAN ---
    /**
     * Toggle agree di sebuah review — kalau berhasil, refresh semua state supaya
     * perubahan jumlah agree langsung keliatan di UI tanpa perlu reload.
     */
    fun toggleAgree(reviewId: Int) {
        val success = repository.toggleAgree(reviewId)
        if (success) {
            refreshDataState()
        }
    }

    /**
     * Kirim balasan ke sebuah review.
     * Pakai nama user yang login, kalau belum login pake nama default.
     * Nggak bisa kirim kalau replyText-nya kosong.
     */
    fun addReply(reviewId: Int, replyText: String, replierName: String = "Pembaca UlasBuku") {
        if (replyText.isBlank()) return
        val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale.forLanguageTag("id-ID")).format(Date())
        val name = if (_currentUser.value != null) _currentUser.value!!.name else replierName
        val reply = repository.addReply(reviewId, replyText.trim(), name.trim(), currentDate)
        if (reply != null) {
            refreshDataState()
        }
    }

    /**
     * Tambah review baru ke sebuah buku.
     * Validasi: komentar nggak boleh kosong.
     * Nama reviewer diambil dari user yang login; kalau anonim, diganti nama anonim.
     * Setelah berhasil, refresh state supaya review baru langsung muncul di UI.
     */
    fun addReview(
        bookId: Int,
        reviewerName: String,
        reviewerEmail: String,
        rating: Float,
        comment: String,
        isAnonymous: Boolean = false
    ): Boolean {
        if (comment.isBlank()) return false
        val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale.forLanguageTag("id-ID")).format(Date())
        val nameToUse = if (isAnonymous) "Pengulas Anonim" else (reviewerName.ifBlank { _currentUser.value?.name ?: "Pembaca" })
        val review = repository.addReview(bookId, nameToUse, rating, comment.trim(), isAnonymous, currentDate)
        if (review != null) {
            refreshDataState()
            updateUserData()
            return true
        }
        return false
    }

    /**
     * Update state userHistory dan communityReviews setelah ada perubahan data.
     * Dipanggil setelah login, add review, add reply, atau update profil.
     */
    private fun updateUserData() {
        val user = _currentUser.value
        if (user != null) {
            _userHistory.value = repository.getUserHistory(user.name)
        }
        _communityReviews.value = repository.getAllCommunityReviews()
    }

    /**
     * Rebuild ulang uiState dari data terbaru di repository.
     * Juga update selectedBook supaya halaman detail ikut refresh kalau lagi kebuka.
     * Dipanggil setiap kali ada perubahan data yang perlu direfleksikan ke UI.
     */
    private fun refreshDataState() {
        val currentCategories = repository.getCategories()
        val currentBooks = repository.getBooks()
        _uiState.value = BookUiState.Success(
            categories = currentCategories,
            books = currentBooks
        )
        _selectedBook.value?.let { current ->
            _selectedBook.value = repository.getBookById(current.id)
        }
        updateUserData()
    }
}
