package com.pemmob.ulasbuku.data.model

// Data class sederhana buat nyimpen info kategori buku (misal: Fantasi, Misteri, dll)
data class Category(
    val id: Int,
    val name: String
)

// Satu balasan (reply) dari user ke sebuah review
// reviewId digunain buat nyambungin reply ini ke review mana
data class Reply(
    val id: Int,
    val reviewId: Int,
    val replierName: String,
    val replyText: String,
    val date: String
)

// Data class buat nyimpen satu ulasan buku dari user
// agreeCount dan isAgreedByUser pake var karena bisa berubah real-time pas user klik agree
// agreedUserIds nyimpen list ID user yang udah kasih agree, disimpen sebagai JSON di database
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

// Data class utama buat representasi satu buku di aplikasi
// publisher dan releaseYear nullable karena data dari JSON kadang bisa kosong
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
    // Hitung rata-rata rating dari semua review, kalau belum ada review return 0.0
    val rating: Double
        get() = if (reviews.isEmpty()) 0.0 else reviews.map { it.userRating.toDouble() }.average()

    // Jumlah total review yang ada di buku ini
    val totalReviews: Int
        get() = reviews.size

    // Fallback ke publisher default kalau datanya null atau kosong
    val displayPublisher: String
        get() = if (publisher.isNullOrBlank()) "Gramedia Pustaka Utama" else publisher

    // Fallback ke tahun default kalau data tahun rilis null atau kosong
    val displayReleaseYear: String
        get() = if (releaseYear.isNullOrBlank()) "2023" else releaseYear

    // Kalau coverImg kosong, generate URL cover dari Open Library pake ISBN sebagai fallback
    val displayCoverImg: String
        get() = if (coverImg.isNullOrBlank()) "https://covers.openlibrary.org/b/isbn/$isbn-M.jpg" else coverImg
}

// Data class buat nyimpen info profil user yang login
// bookmarkedBookIds nyimpen list ID buku yang di-bookmark, disimpen sebagai JSON di database
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

// Sealed class buat nampung histori aktivitas user: bisa berupa review atau balasan
// Dipake di halaman Histori buat nampilin semua riwayat aktivitas user dalam satu list
sealed class UserHistoryItem {
    // User nulis review ke sebuah buku
    data class ReviewItem(val book: Book, val review: Review) : UserHistoryItem()
    // User nulis balasan ke review orang lain di sebuah buku
    data class ReplyItem(val book: Book, val originalReview: Review, val reply: Reply) : UserHistoryItem()
}
