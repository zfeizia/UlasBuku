package com.pemmob.ulasbuku.data.repository

import android.content.Context
import com.pemmob.ulasbuku.data.database.AppDatabaseHelper
import com.pemmob.ulasbuku.data.model.Book
import com.pemmob.ulasbuku.data.model.Category
import com.pemmob.ulasbuku.data.model.Reply
import com.pemmob.ulasbuku.data.model.Review
import com.pemmob.ulasbuku.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Repository ini jadi penghubung antara ViewModel dan database
// Semua operasi data (buku, user, review) lewat sini
class BookRepository(private val context: Context) {

    private val dbHelper = AppDatabaseHelper(context.applicationContext)
    // Cache lokal biar nggak perlu query database terus-terusan
    private val categories = mutableListOf<Category>()
    private val books = mutableListOf<Book>()
    private var isInitialized = false

    // Cache untuk data user yang sedang login
    private val users = mutableListOf<User>()
    private var currentUser: User? = null

    /**
     * Load semua data awal (kategori, buku, user) dari database SQLite.
     * Dijalankan di background thread (IO dispatcher) biar nggak nge-block UI.
     * Punya flag isInitialized biar data cuma dimuat sekali, nggak reload terus.
     */
    suspend fun loadInitialData(): Pair<List<Category>, List<Book>> = withContext(Dispatchers.IO) {
        if (!isInitialized) {
            try {
                val loadedCategories = dbHelper.getAllCategories()
                val loadedBooks = dbHelper.getAllBooks()
                val loadedUsers = dbHelper.getAllUsers()

                categories.clear()
                categories.addAll(loadedCategories)

                books.clear()
                books.addAll(loadedBooks)

                users.clear()
                users.addAll(loadedUsers)

                // Set default user if available
                if (currentUser == null && users.isNotEmpty()) {
                    // Current user is kept null until explicitly logged in, or can be tracked
                }

                isInitialized = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        Pair(categories.toList(), getBooks())
    }

    /**
     * Fungsi internal buat mapping satu review ke state user yang login.
     * Ngecek apakah user yang login udah agree ke review ini atau belum,
     * dengan cara cek apakah userId ada di list agreedUserIds.
     */
    private fun mapReviewForCurrentUser(review: Review): Review {
        val currentUserId = currentUser?.id
        val userIds = review.agreedUserIds ?: mutableListOf()
        val isAgreed = currentUserId != null && userIds.contains(currentUserId)
        return review.copy(
            agreedUserIds = userIds,
            isAgreedByUser = isAgreed,
            agreeCount = review.agreeCount
        )
    }

    /**
     * Mapping semua review di sebuah buku ke state user yang login.
     * Dipanggil setiap kali data buku mau dikembaliin ke ViewModel.
     */
    private fun mapBookForCurrentUser(book: Book): Book {
        return book.copy(
            reviews = book.reviews.map { mapReviewForCurrentUser(it) }.toMutableList()
        )
    }

    // Getter untuk data yang dipake ViewModel — selalu di-mapping dulu ke current user
    fun getBooks(): List<Book> = books.map { mapBookForCurrentUser(it) }
    fun getCategories(): List<Category> = categories.toList()
    fun getBookById(id: Int): Book? = books.find { it.id == id }?.let { mapBookForCurrentUser(it) }

    // Getter buat ambil data user yang lagi login saat ini
    fun getCurrentUser(): User? = currentUser

    /**
     * Login menggunakan email ATAU username (tanpa '@'), tidak case-sensitive.
     */
    fun login(identifier: String, pass: String): User? {
        val trimmed = identifier.trim()
        val user = users.find { u ->
            (u.email.equals(trimmed, ignoreCase = true) ||
             u.username.trimStart('@').equals(trimmed.trimStart('@'), ignoreCase = true)) &&
            u.password == pass
        }
        if (user != null) {
            currentUser = user
            return user
        }
        return null
    }

    /**
     * Register dengan username eksplisit.
     * Mengembalikan null jika email atau username sudah terdaftar.
     */
    fun register(
        name: String,
        username: String,
        email: String,
        pass: String,
        favoriteGenre: String
    ): User? {
        val trimmedUsername = "@" + username.trim().trimStart('@').lowercase().replace(" ", "_")
        val trimmedEmail = email.trim()
        if (users.any { it.email.equals(trimmedEmail, ignoreCase = true) }) return null
        if (users.any { it.username.equals(trimmedUsername, ignoreCase = true) }) return null

        val newUser = User(
            id = (users.maxOfOrNull { it.id } ?: 0) + 1,
            name = name.trim(),
            email = trimmedEmail,
            password = pass,
            username = trimmedUsername,
            bio = "Pembaca aktif UlasBuku",
            joinedDate = "Oktober 2026",
            favoriteGenre = favoriteGenre.ifBlank { "Semua Genre" },
            bookmarkedBookIds = mutableListOf()
        )

        val insertedId = dbHelper.insertUser(newUser)
        val finalUser = if (insertedId > 0) newUser.copy(id = insertedId.toInt()) else newUser

        users.add(finalUser)
        currentUser = finalUser
        return finalUser
    }

    /**
     * Logout user: hapus currentUser dari memory.
     * Data di database nggak berubah, cuma sesi login-nya yang dibersihkan.
     */
    fun logout() {
        currentUser = null
    }

    /**
     * Update profil user yang sedang login.
     * Kalau nama berubah, semua review dan reply yang namanya cocok ikut diupdate juga
     * supaya tampilan di UI tetap konsisten.
     * Return false kalau user belum login.
     */
    fun updateProfile(name: String, username: String, bio: String, favoriteGenre: String): Boolean {
        val user = currentUser ?: return false
        val oldName = user.name
        val trimmedUsername = if (username.trim().startsWith("@")) username.trim() else "@${username.trim()}"
        val updatedUser = user.copy(
            name = name.trim(),
            username = trimmedUsername,
            bio = bio.trim(),
            favoriteGenre = favoriteGenre.trim()
        )
        currentUser = updatedUser
        val idx = users.indexOfFirst { it.id == updatedUser.id }
        if (idx >= 0) users[idx] = updatedUser

        // Persist user in SQLite
        dbHelper.updateUser(updatedUser)

        if (!oldName.equals(updatedUser.name, ignoreCase = true)) {
            dbHelper.updateReviewerAndReplierName(oldName, updatedUser.name)
            for (book in books) {
                val updatedReviews = book.reviews.map { review ->
                    val newReviewer = if (review.reviewerName.equals(oldName, ignoreCase = true)) updatedUser.name else review.reviewerName
                    val updatedReplies = review.replies.map { reply ->
                        if (reply.replierName.equals(oldName, ignoreCase = true)) reply.copy(replierName = updatedUser.name) else reply
                    }.toMutableList()
                    review.copy(reviewerName = newReviewer, replies = updatedReplies)
                }
                book.reviews.clear()
                book.reviews.addAll(updatedReviews)
            }
        }
        return true
    }

    // Fungsi ini belum diimplementasi — status membaca buku (misal: sedang baca, sudah selesai)
    fun setReadingStatus(bookId: Int, status: String): Boolean {
        return true
    }

    // Belum diimplementasi, selalu return NONE untuk sekarang
    fun getReadingStatus(bookId: Int): String {
        return "NONE"
    }

    /**
     * Toggle bookmark sebuah buku — kalau udah dibookmark, hapus; kalau belum, tambahkan.
     * Update juga ke database dan cache user biar state-nya sinkron.
     * Return true kalau buku sekarang di-bookmark, false kalau dibatalkan.
     */
    fun toggleBookmark(bookId: Int): Boolean {
        val user = currentUser ?: return false
        val isBookmarked = user.bookmarkedBookIds.contains(bookId)
        
        val newBookmarkedList = user.bookmarkedBookIds.toMutableList()
        
        if (isBookmarked) {
            newBookmarkedList.remove(bookId)
        } else {
            newBookmarkedList.add(bookId)
        }
        
        // Re-assign user with a copy
        val newUser = user.copy(
            bookmarkedBookIds = newBookmarkedList
        )
        
        currentUser = newUser
        
        // Update user in users list
        val userIndex = users.indexOfFirst { it.id == user.id }
        if (userIndex != -1) {
            users[userIndex] = newUser
        }
        
        dbHelper.updateUser(newUser)
        return !isBookmarked
    }

    // Cek apakah buku dengan ID tertentu sudah ada di daftar bookmark user
    fun isBookmarked(bookId: Int): Boolean {
        return currentUser?.bookmarkedBookIds?.contains(bookId) == true
    }

    /**
     * Ambil semua review yang ditulis oleh user tertentu (berdasarkan nama).
     * Return-nya list pasangan buku + review buat ditampilkan di halaman profil.
     */
    fun getUserReviews(userName: String): List<Pair<Book, Review>> {
        val results = mutableListOf<Pair<Book, Review>>()
        for (book in getBooks()) {
            for (review in book.reviews) {
                if (review.reviewerName.equals(userName, ignoreCase = true) ||
                    (currentUser != null && review.reviewerName.equals(currentUser!!.name, ignoreCase = true))
                ) {
                    results.add(Pair(book, review))
                }
            }
        }
        return results
    }

    /**
     * Ambil seluruh histori aktivitas user: review yang ditulis + balasan yang dikirim.
     * Diurutkan sesuai urutan loop buku, bukan per waktu.
     * Return-nya list UserHistoryItem yang bisa berisi ReviewItem atau ReplyItem.
     */
    fun getUserHistory(userName: String): List<com.pemmob.ulasbuku.data.model.UserHistoryItem> {
        val results = mutableListOf<com.pemmob.ulasbuku.data.model.UserHistoryItem>()
        for (book in getBooks()) {
            for (review in book.reviews) {
                if (review.reviewerName.equals(userName, ignoreCase = true) ||
                    (currentUser != null && review.reviewerName.equals(currentUser!!.name, ignoreCase = true))
                ) {
                    results.add(com.pemmob.ulasbuku.data.model.UserHistoryItem.ReviewItem(book, review))
                }
                for (reply in review.replies) {
                    if (reply.replierName.equals(userName, ignoreCase = true) ||
                        (currentUser != null && reply.replierName.equals(currentUser!!.name, ignoreCase = true))
                    ) {
                        results.add(com.pemmob.ulasbuku.data.model.UserHistoryItem.ReplyItem(book, review, reply))
                    }
                }
            }
        }
        return results
    }

    /**
     * Ambil semua review dari seluruh buku di aplikasi.
     * Dibalik urutan (reversed) supaya review paling baru tampil duluan di halaman komunitas.
     */
    fun getAllCommunityReviews(): List<Pair<Book, Review>> {
        val results = mutableListOf<Pair<Book, Review>>()
        for (book in getBooks()) {
            for (review in book.reviews) {
                results.add(Pair(book, review))
            }
        }
        // Sort newest reviews first
        return results.reversed()
    }

    /**
     * Toggle agree pada sebuah review — kalau sudah agree, batalkan; kalau belum, tambahkan.
     * Update langsung ke cache buku di memory dan juga persist ke database.
     * Return true kalau berhasil nemuin review-nya, false kalau reviewId tidak ketemu.
     */
    fun toggleAgree(reviewId: Int): Boolean {
        val user = currentUser ?: return false
        for (i in books.indices) {
            val book = books[i]
            val reviewIndex = book.reviews.indexOfFirst { it.id == reviewId }
            if (reviewIndex != -1) {
                val oldReview = book.reviews[reviewIndex]
                val userIds = (oldReview.agreedUserIds ?: mutableListOf()).toMutableList()
                val alreadyLiked = userIds.contains(user.id)
                val newAgreeCount = if (alreadyLiked) {
                    userIds.remove(user.id)
                    (oldReview.agreeCount - 1).coerceAtLeast(0)
                } else {
                    userIds.add(user.id)
                    oldReview.agreeCount + 1
                }
                val newReview = oldReview.copy(
                    agreedUserIds = userIds,
                    isAgreedByUser = !alreadyLiked,
                    agreeCount = newAgreeCount
                )
                
                val newReviewsList = book.reviews.toMutableList()
                newReviewsList[reviewIndex] = newReview
                
                books[i] = book.copy(reviews = newReviewsList)
                dbHelper.updateReview(newReview)
                return true
            }
        }
        return false
    }

    /**
     * Tambahkan balasan ke sebuah review berdasarkan reviewId.
     * ID reply di-generate dari System.currentTimeMillis() biar unik (cukup untuk skala kecil).
     * Kalau reviewId nggak ketemu, return null.
     */
    fun addReply(reviewId: Int, replyText: String, replierName: String, date: String): Reply? {
        for (book in books) {
            val review = book.reviews.find { it.id == reviewId }
            if (review != null) {
                val newReplyId = (System.currentTimeMillis() % 100000).toInt()
                val reply = Reply(
                    id = newReplyId,
                    reviewId = reviewId,
                    replierName = replierName.ifBlank { "Pembaca UlasBuku" },
                    replyText = replyText,
                    date = date
                )
                review.replies.add(reply)
                dbHelper.insertReply(reply)
                return reply
            }
        }
        return null
    }

    /**
     * Tambahkan review baru ke sebuah buku.
     * Review baru langsung dimasukin ke posisi paling depan (index 0) di list buku,
     * supaya tampil paling atas di UI tanpa perlu sorting ulang.
     * Kalau isAnonymous true, nama reviewer diganti jadi 'Pengulas Anonim'.
     */
    fun addReview(
        bookId: Int,
        reviewerName: String,
        userRating: Float,
        comment: String,
        isAnonymous: Boolean,
        date: String
    ): Review? {
        val book = books.find { it.id == bookId } ?: return null
        val newReviewId = (System.currentTimeMillis() % 100000).toInt()
        val displayName = if (isAnonymous) "Pengulas Anonim 🤫" else reviewerName.ifBlank { "Pembaca" }
        val review = Review(
            id = newReviewId,
            bookId = bookId,
            reviewerName = displayName,
            userRating = userRating,
            comment = comment,
            agreeCount = 0,
            isAgreedByUser = false,
            isAnonymous = isAnonymous,
            replies = mutableListOf(),
            date = date
        )
        book.reviews.add(0, review)
        dbHelper.insertReview(review)
        return review
    }
}

