package com.pemmob.ulasbuku.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pemmob.ulasbuku.data.model.Book
import com.pemmob.ulasbuku.data.model.Category
import com.pemmob.ulasbuku.data.model.Reply
import com.pemmob.ulasbuku.data.model.Review
import com.pemmob.ulasbuku.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

data class BookDataWrapper(
    val categories: List<Category>,
    val books: List<Book>
)

class BookRepository(private val context: Context) {

    private val categories = mutableListOf<Category>()
    private val books = mutableListOf<Book>()
    private var isInitialized = false

    // Default User
    private val users = mutableListOf(
        User(
            id = 1,
            name = "Feizia Azahra",
            email = "feizia@ulasbuku.id",
            password = "password123",
            username = "@feizia_reads",
            bio = "Mahasiswa & penikmat sastra kontemporer serta fiksi spekulatif.",
            joinedDate = "September 2026",
            favoriteGenre = "Sastra & Drama",
            bookmarkedBookIds = mutableListOf(1, 15, 27, 31, 39),
            readingStatusMap = mutableMapOf(
                1 to "READING",
                15 to "WANT_TO_READ",
                27 to "COMPLETED",
                31 to "WANT_TO_READ",
                39 to "COMPLETED"
            )
        )
    )

    private var currentUser: User? = null

    suspend fun loadInitialData(): Pair<List<Category>, List<Book>> = withContext(Dispatchers.IO) {
        if (!isInitialized) {
            try {
                context.assets.open("books.json").use { inputStream ->
                    InputStreamReader(inputStream).use { reader ->
                        val type = object : TypeToken<BookDataWrapper>() {}.type
                        val data: BookDataWrapper = Gson().fromJson(reader, type)
                        categories.clear()
                        categories.addAll(data.categories)
                        books.clear()
                        books.addAll(data.books)
                        isInitialized = true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        Pair(categories.toList(), books.toList())
    }

    fun getBooks(): List<Book> = books.toList()
    fun getCategories(): List<Category> = categories.toList()
    fun getBookById(id: Int): Book? = books.find { it.id == id }

    // User Session & Auth
    fun getCurrentUser(): User? = currentUser

    fun login(email: String, pass: String): User? {
        val user = users.find { it.email.equals(email.trim(), ignoreCase = true) && it.password == pass }
        if (user != null) {
            currentUser = user
            return user
        }
        return null
    }

    fun register(name: String, email: String, pass: String, favoriteGenre: String): User? {
        if (users.any { it.email.equals(email.trim(), ignoreCase = true) }) {
            return null
        }
        val newUser = User(
            id = users.size + 1,
            name = name.trim(),
            email = email.trim(),
            password = pass,
            username = "@" + name.trim().lowercase().replace(" ", "_"),
            bio = "Pembaca aktif UlasBuku",
            joinedDate = "September 2026",
            favoriteGenre = favoriteGenre.ifBlank { "Semua Genre" },
            bookmarkedBookIds = mutableListOf(1, 20)
        )
        users.add(newUser)
        currentUser = newUser
        return newUser
    }

    fun logout() {
        currentUser = null
    }

    fun updateProfile(name: String, username: String, bio: String, favoriteGenre: String): Boolean {
        val user = currentUser ?: return false
        user.name = name.trim()
        user.username = if (username.startsWith("@")) username.trim() else "@${username.trim()}"
        user.bio = bio.trim()
        user.favoriteGenre = favoriteGenre.trim()
        return true
    }

    fun setReadingStatus(bookId: Int, status: String): Boolean {
        val user = currentUser ?: return false
        if (status.isBlank()) {
            user.readingStatusMap.remove(bookId)
            user.bookmarkedBookIds.remove(bookId)
        } else {
            user.readingStatusMap[bookId] = status
            if (!user.bookmarkedBookIds.contains(bookId)) {
                user.bookmarkedBookIds.add(bookId)
            }
        }
        return true
    }

    fun getReadingStatus(bookId: Int): String {
        return currentUser?.readingStatusMap?.get(bookId) ?: "NONE"
    }

    fun toggleBookmark(bookId: Int): Boolean {
        val user = currentUser ?: return false
        val isBookmarked = user.bookmarkedBookIds.contains(bookId)
        if (isBookmarked) {
            user.bookmarkedBookIds.remove(bookId)
            user.readingStatusMap.remove(bookId)
        } else {
            user.bookmarkedBookIds.add(bookId)
            user.readingStatusMap[bookId] = "WANT_TO_READ"
        }
        return !isBookmarked
    }

    fun isBookmarked(bookId: Int): Boolean {
        return currentUser?.bookmarkedBookIds?.contains(bookId) == true
    }

    fun getUserReviews(userName: String): List<Pair<Book, Review>> {
        val results = mutableListOf<Pair<Book, Review>>()
        for (book in books) {
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

    fun getAllCommunityReviews(): List<Pair<Book, Review>> {
        val results = mutableListOf<Pair<Book, Review>>()
        for (book in books) {
            for (review in book.reviews) {
                results.add(Pair(book, review))
            }
        }
        // Sort newest reviews first
        return results.reversed()
    }

    fun toggleAgree(reviewId: Int): Boolean {
        for (book in books) {
            val review = book.reviews.find { it.id == reviewId }
            if (review != null) {
                if (review.isAgreedByUser) {
                    review.isAgreedByUser = false
                    review.agreeCount = (review.agreeCount - 1).coerceAtLeast(0)
                } else {
                    review.isAgreedByUser = true
                    review.agreeCount += 1
                }
                return true
            }
        }
        return false
    }

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
                return reply
            }
        }
        return null
    }

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
        return review
    }
}
