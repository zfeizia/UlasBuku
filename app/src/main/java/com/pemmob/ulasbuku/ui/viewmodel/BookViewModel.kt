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

sealed interface BookUiState {
    object Loading : BookUiState
    data class Success(
        val categories: List<Category>,
        val books: List<Book>
    ) : BookUiState
    data class Error(val message: String) : BookUiState
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

    private val _userReviews = MutableStateFlow<List<Pair<Book, Review>>>(emptyList())
    val userReviews: StateFlow<List<Pair<Book, Review>>> = _userReviews.asStateFlow()

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

    init {
        loadBooks()
    }

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
    fun login(email: String, pass: String): Boolean {
        _authError.value = null
        if (email.isBlank() || pass.isBlank()) {
            _authError.value = "Email dan password wajib diisi."
            return false
        }
        val user = repository.login(email, pass)
        if (user != null) {
            _currentUser.value = user
            updateUserData()
            return true
        } else {
            _authError.value = "Email atau kata sandi tidak cocok."
            return false
        }
    }

    fun register(name: String, email: String, pass: String, favoriteGenre: String): Boolean {
        _authError.value = null
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            _authError.value = "Semua bidang bertanda bintang wajib diisi."
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
        val user = repository.register(name, email, pass, favoriteGenre)
        if (user != null) {
            _currentUser.value = user
            updateUserData()
            return true
        } else {
            _authError.value = "Email sudah terdaftar. Silakan gunakan email lain."
            return false
        }
    }

    fun logout() {
        repository.logout()
        _currentUser.value = null
        _userReviews.value = emptyList()
    }

    fun updateProfile(name: String, username: String, bio: String, favoriteGenre: String): Boolean {
        val success = repository.updateProfile(name, username, bio, favoriteGenre)
        if (success) {
            _currentUser.value = repository.getCurrentUser()?.copy()
            refreshDataState()
        }
        return success
    }

    fun setReadingStatus(bookId: Int, status: String) {
        repository.setReadingStatus(bookId, status)
        _currentUser.value = repository.getCurrentUser()?.copy()
        refreshDataState()
    }

    fun getReadingStatus(bookId: Int): String {
        return repository.getReadingStatus(bookId)
    }

    fun clearAuthError() {
        _authError.value = null
    }

    // --- BOOKMARK & FAVORIT ---
    fun toggleBookmark(bookId: Int): Boolean {
        val result = repository.toggleBookmark(bookId)
        _currentUser.value = repository.getCurrentUser()?.copy()
        refreshDataState()
        return result
    }

    fun isBookmarked(bookId: Int): Boolean {
        return repository.isBookmarked(bookId)
    }

    // --- PENCARIAN & KATEGORI ---
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(categoryId: Int?) {
        _selectedCategoryId.value = if (_selectedCategoryId.value == categoryId) null else categoryId
    }

    fun selectBook(book: Book) {
        _selectedBook.value = repository.getBookById(book.id) ?: book
    }

    fun clearSelectedBook() {
        _selectedBook.value = null
    }

    // --- INTERAKSI ULASAN ---
    fun toggleAgree(reviewId: Int) {
        val success = repository.toggleAgree(reviewId)
        if (success) {
            refreshDataState()
        }
    }

    fun addReply(reviewId: Int, replyText: String, replierName: String = "Pembaca UlasBuku") {
        if (replyText.isBlank()) return
        val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale.forLanguageTag("id-ID")).format(Date())
        val name = if (_currentUser.value != null) _currentUser.value!!.name else replierName
        val reply = repository.addReply(reviewId, replyText.trim(), name.trim(), currentDate)
        if (reply != null) {
            refreshDataState()
        }
    }

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

    private fun updateUserData() {
        val user = _currentUser.value
        if (user != null) {
            _userReviews.value = repository.getUserReviews(user.name)
        }
        _communityReviews.value = repository.getAllCommunityReviews()
    }

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
