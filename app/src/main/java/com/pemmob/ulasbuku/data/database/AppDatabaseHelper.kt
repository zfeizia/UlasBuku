package com.pemmob.ulasbuku.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pemmob.ulasbuku.data.model.Book
import com.pemmob.ulasbuku.data.model.Category
import com.pemmob.ulasbuku.data.model.Reply
import com.pemmob.ulasbuku.data.model.Review
import com.pemmob.ulasbuku.data.model.User
import java.io.InputStreamReader

data class SeedDataWrapper(
    val categories: List<Category>,
    val books: List<Book>
)

class AppDatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val gson = Gson()

    companion object {
        private const val DATABASE_NAME = "ulasbuku.db"
        private const val DATABASE_VERSION = 1

        // Table Categories
        const val TABLE_CATEGORIES = "categories"
        const val COL_CAT_ID = "id"
        const val COL_CAT_NAME = "name"

        // Table Books
        const val TABLE_BOOKS = "books"
        const val COL_BOOK_ID = "id"
        const val COL_BOOK_CAT_ID = "category_id"
        const val COL_BOOK_TITLE = "title"
        const val COL_BOOK_AUTHOR = "author"
        const val COL_BOOK_SYNOPSIS = "synopsis"
        const val COL_BOOK_ISBN = "isbn"
        const val COL_BOOK_COVER = "cover_img"
        const val COL_BOOK_PUBLISHER = "publisher"
        const val COL_BOOK_RELEASE_YEAR = "release_year"

        // Table Reviews
        const val TABLE_REVIEWS = "reviews"
        const val COL_REV_ID = "id"
        const val COL_REV_BOOK_ID = "book_id"
        const val COL_REV_NAME = "reviewer_name"
        const val COL_REV_RATING = "user_rating"
        const val COL_REV_COMMENT = "comment"
        const val COL_REV_AGREE_COUNT = "agree_count"
        const val COL_REV_IS_ANON = "is_anonymous"
        const val COL_REV_DATE = "date"
        const val COL_REV_AGREED_USER_IDS = "agreed_user_ids"

        // Table Replies
        const val TABLE_REPLIES = "replies"
        const val COL_REP_ID = "id"
        const val COL_REP_REVIEW_ID = "review_id"
        const val COL_REP_NAME = "replier_name"
        const val COL_REP_TEXT = "reply_text"
        const val COL_REP_DATE = "date"

        // Table Users
        const val TABLE_USERS = "users"
        const val COL_USER_ID = "id"
        const val COL_USER_NAME = "name"
        const val COL_USER_EMAIL = "email"
        const val COL_USER_PASSWORD = "password"
        const val COL_USER_USERNAME = "username"
        const val COL_USER_BIO = "bio"
        const val COL_USER_JOINED = "joined_date"
        const val COL_USER_GENRE = "favorite_genre"
        const val COL_USER_BOOKMARKS = "bookmarked_book_ids"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createCategoriesTable = """
            CREATE TABLE $TABLE_CATEGORIES (
                $COL_CAT_ID INTEGER PRIMARY KEY,
                $COL_CAT_NAME TEXT NOT NULL
            )
        """.trimIndent()

        val createBooksTable = """
            CREATE TABLE $TABLE_BOOKS (
                $COL_BOOK_ID INTEGER PRIMARY KEY,
                $COL_BOOK_CAT_ID INTEGER NOT NULL,
                $COL_BOOK_TITLE TEXT NOT NULL,
                $COL_BOOK_AUTHOR TEXT NOT NULL,
                $COL_BOOK_SYNOPSIS TEXT,
                $COL_BOOK_ISBN TEXT,
                $COL_BOOK_COVER TEXT,
                $COL_BOOK_PUBLISHER TEXT,
                $COL_BOOK_RELEASE_YEAR TEXT,
                FOREIGN KEY($COL_BOOK_CAT_ID) REFERENCES $TABLE_CATEGORIES($COL_CAT_ID)
            )
        """.trimIndent()

        val createReviewsTable = """
            CREATE TABLE $TABLE_REVIEWS (
                $COL_REV_ID INTEGER PRIMARY KEY,
                $COL_REV_BOOK_ID INTEGER NOT NULL,
                $COL_REV_NAME TEXT NOT NULL,
                $COL_REV_RATING REAL NOT NULL,
                $COL_REV_COMMENT TEXT NOT NULL,
                $COL_REV_AGREE_COUNT INTEGER DEFAULT 0,
                $COL_REV_IS_ANON INTEGER DEFAULT 0,
                $COL_REV_DATE TEXT NOT NULL,
                $COL_REV_AGREED_USER_IDS TEXT,
                FOREIGN KEY($COL_REV_BOOK_ID) REFERENCES $TABLE_BOOKS($COL_BOOK_ID)
            )
        """.trimIndent()

        val createRepliesTable = """
            CREATE TABLE $TABLE_REPLIES (
                $COL_REP_ID INTEGER PRIMARY KEY,
                $COL_REP_REVIEW_ID INTEGER NOT NULL,
                $COL_REP_NAME TEXT NOT NULL,
                $COL_REP_TEXT TEXT NOT NULL,
                $COL_REP_DATE TEXT NOT NULL,
                FOREIGN KEY($COL_REP_REVIEW_ID) REFERENCES $TABLE_REVIEWS($COL_REV_ID)
            )
        """.trimIndent()

        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USER_NAME TEXT NOT NULL,
                $COL_USER_EMAIL TEXT NOT NULL UNIQUE,
                $COL_USER_PASSWORD TEXT NOT NULL,
                $COL_USER_USERNAME TEXT NOT NULL UNIQUE,
                $COL_USER_BIO TEXT,
                $COL_USER_JOINED TEXT,
                $COL_USER_GENRE TEXT,
                $COL_USER_BOOKMARKS TEXT
            )
        """.trimIndent()

        db.execSQL(createCategoriesTable)
        db.execSQL(createBooksTable)
        db.execSQL(createReviewsTable)
        db.execSQL(createRepliesTable)
        db.execSQL(createUsersTable)

        // Seed default user
        val defaultBookmarksJson = gson.toJson(listOf(1, 15, 27, 31, 39))

        val defaultUserValues = ContentValues().apply {
            put(COL_USER_ID, 1)
            put(COL_USER_NAME, "Feizia Azahra")
            put(COL_USER_EMAIL, "feizia@ulasbuku.id")
            put(COL_USER_PASSWORD, "password123")
            put(COL_USER_USERNAME, "@feizia_reads")
            put(COL_USER_BIO, "Mahasiswa & penikmat sastra kontemporer serta fiksi spekulatif.")
            put(COL_USER_JOINED, "September 2026")
            put(COL_USER_GENRE, "Sastra & Drama")
            put(COL_USER_BOOKMARKS, defaultBookmarksJson)
        }
        db.insert(TABLE_USERS, null, defaultUserValues)

        // Seed initial data
        seedData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_REPLIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_REVIEWS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    private fun seedData(db: SQLiteDatabase) {
        try {
            // Seed directly from assets books.json into SQLite
            context.assets.open("books.json").use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    val type = object : TypeToken<SeedDataWrapper>() {}.type
                    val data: SeedDataWrapper = gson.fromJson(reader, type)

                    for (cat in data.categories) {
                        val cv = ContentValues().apply {
                            put(COL_CAT_ID, cat.id)
                            put(COL_CAT_NAME, cat.name)
                        }
                        db.insertWithOnConflict(TABLE_CATEGORIES, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
                    }

                    for (book in data.books) {
                        val bcv = ContentValues().apply {
                            put(COL_BOOK_ID, book.id)
                            put(COL_BOOK_CAT_ID, book.categoryId)
                            put(COL_BOOK_TITLE, book.title)
                            put(COL_BOOK_AUTHOR, book.author)
                            put(COL_BOOK_SYNOPSIS, book.synopsis)
                            put(COL_BOOK_ISBN, book.isbn)
                            put(COL_BOOK_COVER, book.displayCoverImg)
                            put(COL_BOOK_PUBLISHER, book.displayPublisher)
                            put(COL_BOOK_RELEASE_YEAR, book.displayReleaseYear)
                        }
                        db.insertWithOnConflict(TABLE_BOOKS, null, bcv, SQLiteDatabase.CONFLICT_REPLACE)

                        for (review in (book.reviews ?: emptyList())) {
                            val agreedJson = gson.toJson(review.agreedUserIds ?: emptyList<Int>())
                            val rcv = ContentValues().apply {
                                put(COL_REV_ID, review.id)
                                put(COL_REV_BOOK_ID, review.bookId)
                                put(COL_REV_NAME, review.reviewerName)
                                put(COL_REV_RATING, review.userRating)
                                put(COL_REV_COMMENT, review.comment)
                                put(COL_REV_AGREE_COUNT, review.agreeCount)
                                put(COL_REV_IS_ANON, if (review.isAnonymous) 1 else 0)
                                put(COL_REV_DATE, review.date)
                                put(COL_REV_AGREED_USER_IDS, agreedJson)
                            }
                            db.insertWithOnConflict(TABLE_REVIEWS, null, rcv, SQLiteDatabase.CONFLICT_REPLACE)

                            for (reply in (review.replies ?: emptyList())) {
                                val repCv = ContentValues().apply {
                                    put(COL_REP_ID, reply.id)
                                    put(COL_REP_REVIEW_ID, reply.reviewId)
                                    put(COL_REP_NAME, reply.replierName)
                                    put(COL_REP_TEXT, reply.replyText)
                                    put(COL_REP_DATE, reply.date)
                                }
                                db.insertWithOnConflict(TABLE_REPLIES, null, repCv, SQLiteDatabase.CONFLICT_REPLACE)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // --- Data Access Operations ---

    fun getAllCategories(): List<Category> {
        val list = mutableListOf<Category>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COL_CAT_ID, $COL_CAT_NAME FROM $TABLE_CATEGORIES ORDER BY $COL_CAT_ID ASC", null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    Category(
                        id = it.getInt(0),
                        name = it.getString(1)
                    )
                )
            }
        }
        return list
    }

    fun getAllBooks(): List<Book> {
        val books = mutableListOf<Book>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_BOOKS ORDER BY $COL_BOOK_ID ASC", null)
        cursor.use {
            while (it.moveToNext()) {
                val bookId = it.getInt(it.getColumnIndexOrThrow(COL_BOOK_ID))
                val catId = it.getInt(it.getColumnIndexOrThrow(COL_BOOK_CAT_ID))
                val title = it.getString(it.getColumnIndexOrThrow(COL_BOOK_TITLE))
                val author = it.getString(it.getColumnIndexOrThrow(COL_BOOK_AUTHOR))
                val synopsis = it.getString(it.getColumnIndexOrThrow(COL_BOOK_SYNOPSIS)) ?: ""
                val isbn = it.getString(it.getColumnIndexOrThrow(COL_BOOK_ISBN)) ?: ""
                val cover = it.getString(it.getColumnIndexOrThrow(COL_BOOK_COVER)) ?: ""
                val publisher = it.getString(it.getColumnIndexOrThrow(COL_BOOK_PUBLISHER)) ?: "Gramedia Pustaka Utama"
                val releaseYear = it.getString(it.getColumnIndexOrThrow(COL_BOOK_RELEASE_YEAR)) ?: "2023"

                val reviews = getReviewsForBook(db, bookId)

                books.add(
                    Book(
                        id = bookId,
                        categoryId = catId,
                        title = title,
                        author = author,
                        synopsis = synopsis,
                        isbn = isbn,
                        coverImg = cover,
                        publisher = publisher,
                        releaseYear = releaseYear,
                        reviews = reviews.toMutableList()
                    )
                )
            }
        }
        return books
    }

    private fun getReviewsForBook(db: SQLiteDatabase, bookId: Int): List<Review> {
        val reviews = mutableListOf<Review>()
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_REVIEWS WHERE $COL_REV_BOOK_ID = ? ORDER BY $COL_REV_ID DESC",
            arrayOf(bookId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                val revId = it.getInt(it.getColumnIndexOrThrow(COL_REV_ID))
                val revBookId = it.getInt(it.getColumnIndexOrThrow(COL_REV_BOOK_ID))
                val reviewerName = it.getString(it.getColumnIndexOrThrow(COL_REV_NAME))
                val userRating = it.getFloat(it.getColumnIndexOrThrow(COL_REV_RATING))
                val comment = it.getString(it.getColumnIndexOrThrow(COL_REV_COMMENT))
                val agreeCount = it.getInt(it.getColumnIndexOrThrow(COL_REV_AGREE_COUNT))
                val isAnon = it.getInt(it.getColumnIndexOrThrow(COL_REV_IS_ANON)) == 1
                val date = it.getString(it.getColumnIndexOrThrow(COL_REV_DATE))
                val agreedUserIdsJson = it.getString(it.getColumnIndexOrThrow(COL_REV_AGREED_USER_IDS))

                val agreedUserIds: MutableList<Int> = if (!agreedUserIdsJson.isNullOrBlank()) {
                    try {
                        val type = object : TypeToken<List<Int>>() {}.type
                        gson.fromJson<List<Int>>(agreedUserIdsJson, type).toMutableList()
                    } catch (e: Exception) {
                        mutableListOf()
                    }
                } else {
                    mutableListOf()
                }

                val replies = getRepliesForReview(db, revId)

                reviews.add(
                    Review(
                        id = revId,
                        bookId = revBookId,
                        reviewerName = reviewerName,
                        userRating = userRating,
                        comment = comment,
                        agreeCount = agreeCount,
                        isAgreedByUser = false,
                        isAnonymous = isAnon,
                        replies = replies.toMutableList(),
                        date = date,
                        agreedUserIds = agreedUserIds
                    )
                )
            }
        }
        return reviews
    }

    private fun getRepliesForReview(db: SQLiteDatabase, reviewId: Int): List<Reply> {
        val replies = mutableListOf<Reply>()
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_REPLIES WHERE $COL_REP_REVIEW_ID = ? ORDER BY $COL_REP_ID ASC",
            arrayOf(reviewId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                val repId = it.getInt(it.getColumnIndexOrThrow(COL_REP_ID))
                val repRevId = it.getInt(it.getColumnIndexOrThrow(COL_REP_REVIEW_ID))
                val replierName = it.getString(it.getColumnIndexOrThrow(COL_REP_NAME))
                val replyText = it.getString(it.getColumnIndexOrThrow(COL_REP_TEXT))
                val date = it.getString(it.getColumnIndexOrThrow(COL_REP_DATE))

                replies.add(
                    Reply(
                        id = repId,
                        reviewId = repRevId,
                        replierName = replierName,
                        replyText = replyText,
                        date = date
                    )
                )
            }
        }
        return replies
    }

    fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS ORDER BY $COL_USER_ID ASC", null)
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(COL_USER_ID))
                val name = it.getString(it.getColumnIndexOrThrow(COL_USER_NAME))
                val email = it.getString(it.getColumnIndexOrThrow(COL_USER_EMAIL))
                val password = it.getString(it.getColumnIndexOrThrow(COL_USER_PASSWORD))
                val username = it.getString(it.getColumnIndexOrThrow(COL_USER_USERNAME))
                val bio = it.getString(it.getColumnIndexOrThrow(COL_USER_BIO)) ?: ""
                val joined = it.getString(it.getColumnIndexOrThrow(COL_USER_JOINED)) ?: ""
                val genre = it.getString(it.getColumnIndexOrThrow(COL_USER_GENRE)) ?: ""
                val bookmarksJson = it.getString(it.getColumnIndexOrThrow(COL_USER_BOOKMARKS))

                val bookmarks: MutableList<Int> = if (!bookmarksJson.isNullOrBlank()) {
                    try {
                        val type = object : TypeToken<List<Int>>() {}.type
                        gson.fromJson<List<Int>>(bookmarksJson, type).toMutableList()
                    } catch (e: Exception) {
                        mutableListOf()
                    }
                } else mutableListOf()

                users.add(
                    User(
                        id = id,
                        name = name,
                        email = email,
                        password = password,
                        username = username,
                        bio = bio,
                        joinedDate = joined,
                        favoriteGenre = genre,
                        bookmarkedBookIds = bookmarks
                    )
                )
            }
        }
        return users
    }

    fun insertUser(user: User): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_USER_NAME, user.name)
            put(COL_USER_EMAIL, user.email)
            put(COL_USER_PASSWORD, user.password)
            put(COL_USER_USERNAME, user.username)
            put(COL_USER_BIO, user.bio)
            put(COL_USER_JOINED, user.joinedDate)
            put(COL_USER_GENRE, user.favoriteGenre)
            put(COL_USER_BOOKMARKS, gson.toJson(user.bookmarkedBookIds))
        }
        return db.insert(TABLE_USERS, null, cv)
    }

    fun updateUser(user: User): Int {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_USER_NAME, user.name)
            put(COL_USER_EMAIL, user.email)
            put(COL_USER_PASSWORD, user.password)
            put(COL_USER_USERNAME, user.username)
            put(COL_USER_BIO, user.bio)
            put(COL_USER_JOINED, user.joinedDate)
            put(COL_USER_GENRE, user.favoriteGenre)
            put(COL_USER_BOOKMARKS, gson.toJson(user.bookmarkedBookIds))
        }
        return db.update(TABLE_USERS, cv, "$COL_USER_ID = ?", arrayOf(user.id.toString()))
    }

    fun insertReview(review: Review): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_REV_ID, review.id)
            put(COL_REV_BOOK_ID, review.bookId)
            put(COL_REV_NAME, review.reviewerName)
            put(COL_REV_RATING, review.userRating)
            put(COL_REV_COMMENT, review.comment)
            put(COL_REV_AGREE_COUNT, review.agreeCount)
            put(COL_REV_IS_ANON, if (review.isAnonymous) 1 else 0)
            put(COL_REV_DATE, review.date)
            put(COL_REV_AGREED_USER_IDS, gson.toJson(review.agreedUserIds))
        }
        return db.insertWithOnConflict(TABLE_REVIEWS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun updateReview(review: Review): Int {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_REV_NAME, review.reviewerName)
            put(COL_REV_RATING, review.userRating)
            put(COL_REV_COMMENT, review.comment)
            put(COL_REV_AGREE_COUNT, review.agreeCount)
            put(COL_REV_IS_ANON, if (review.isAnonymous) 1 else 0)
            put(COL_REV_DATE, review.date)
            put(COL_REV_AGREED_USER_IDS, gson.toJson(review.agreedUserIds))
        }
        return db.update(TABLE_REVIEWS, cv, "$COL_REV_ID = ?", arrayOf(review.id.toString()))
    }

    fun insertReply(reply: Reply): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_REP_ID, reply.id)
            put(COL_REP_REVIEW_ID, reply.reviewId)
            put(COL_REP_NAME, reply.replierName)
            put(COL_REP_TEXT, reply.replyText)
            put(COL_REP_DATE, reply.date)
        }
        return db.insertWithOnConflict(TABLE_REPLIES, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun updateReviewerAndReplierName(oldName: String, newName: String) {
        val db = writableDatabase
        val cvReplies = ContentValues().apply {
            put(COL_REP_NAME, newName)
        }
        db.update(TABLE_REPLIES, cvReplies, "LOWER($COL_REP_NAME) = LOWER(?)", arrayOf(oldName))

        val cvReviews = ContentValues().apply {
            put(COL_REV_NAME, newName)
        }
        db.update(TABLE_REVIEWS, cvReviews, "LOWER($COL_REV_NAME) = LOWER(?)", arrayOf(oldName))
    }
}
