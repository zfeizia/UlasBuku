package com.pemmob.ulasbuku.data.model

data class Category(
    val id: Int,
    val name: String
)

data class Reply(
    val id: Int,
    val reviewId: Int,
    val replierName: String,
    val replyText: String,
    val date: String
)

data class Review(
    val id: Int,
    val bookId: Int,
    val reviewerName: String,
    val userRating: Float,
    val comment: String,
    var agreeCount: Int = 0,
    var isAgreedByUser: Boolean = false,
    val isAnonymous: Boolean = false,
    val replies: MutableList<Reply> = mutableListOf(),
    val date: String,
    val agreedUserIds: MutableList<Int> = mutableListOf()
)

data class Book(
    val id: Int,
    val categoryId: Int,
    val title: String,
    val author: String,
    val synopsis: String,
    val isbn: String,
    val coverImg: String,
    val publisher: String? = "Gramedia Pustaka Utama",
    val releaseYear: String? = "2023",
    val reviews: MutableList<Review> = mutableListOf()
) {
    val rating: Double
        get() = if (reviews.isEmpty()) 0.0 else reviews.map { it.userRating.toDouble() }.average()

    val totalReviews: Int
        get() = reviews.size

    val displayPublisher: String
        get() = if (publisher.isNullOrBlank()) "Gramedia Pustaka Utama" else publisher

    val displayReleaseYear: String
        get() = if (releaseYear.isNullOrBlank()) "2023" else releaseYear
        
    val displayCoverImg: String
        get() = if (coverImg.isNullOrBlank()) "https://covers.openlibrary.org/b/isbn/$isbn-M.jpg" else coverImg
}

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val password: String = "password123",
    val username: String = "@feizia_reads",
    val bio: String = "Pecinta buku & pembaca setia sastra Indonesia.",
    val joinedDate: String = "September 2026",
    val favoriteGenre: String = "Sastra & Drama",
    val bookmarkedBookIds: MutableList<Int> = mutableListOf(1, 15, 27, 31, 39)
)

sealed class UserHistoryItem {
    data class ReviewItem(val book: Book, val review: Review) : UserHistoryItem()
    data class ReplyItem(val book: Book, val originalReview: Review, val reply: Reply) : UserHistoryItem()
}
